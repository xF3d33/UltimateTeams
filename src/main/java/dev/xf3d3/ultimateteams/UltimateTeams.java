package dev.xf3d3.ultimateteams;

import co.aikar.commands.PaperCommandManager;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xf3d3.ultimateteams.commands.TeamAdmin;
import dev.xf3d3.ultimateteams.commands.TeamChatCommand;
import dev.xf3d3.ultimateteams.commands.TeamChatSpyCommand;
import dev.xf3d3.ultimateteams.commands.TeamCommand;
import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.database.MySqlDatabase;
import dev.xf3d3.ultimateteams.database.SQLiteDatabase;
import dev.xf3d3.ultimateteams.expansions.PapiExpansion;
import dev.xf3d3.ultimateteams.files.MessagesFileManager;
import dev.xf3d3.ultimateteams.files.TeamGUIFileManager;
import dev.xf3d3.ultimateteams.listeners.PlayerConnectEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDamageEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDisconnectEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class UltimateTeams extends JavaPlugin implements TaskRunner {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    private static UltimateTeams instance;

    public MessagesFileManager msgFileManager;
    public TeamGUIFileManager teamGUIFileManager;

    private FloodgateApi floodgateApi;
    private Database database;
    private Utils utils;
    private ConcurrentHashMap<Integer, ScheduledTask> tasks;
    private MorePaperLib paperLib;
    private PaperCommandManager manager;
    private TeamStorageUtil teamStorageUtil;
    private UsersStorageUtil usersStorageUtil;
    private TeamInviteUtil teamInviteUtil;

    // HashMaps
    private final ConcurrentHashMap<Player, String> connectedPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, String> bedrockPlayers = new ConcurrentHashMap<>();

    @Override
    public void onLoad() {
        // Set the instance
        instance = this;
    }

    @Override
    public void onEnable() {
        this.tasks = new ConcurrentHashMap<>();
        this.paperLib = new MorePaperLib(this);
        this.manager = new PaperCommandManager(this);
        this.teamGUIFileManager = new TeamGUIFileManager(this);
        this.msgFileManager = new MessagesFileManager(this);
        this.teamStorageUtil = new TeamStorageUtil(this);
        this.usersStorageUtil = new UsersStorageUtil(this);
        this.teamInviteUtil = new TeamInviteUtil(this);
        this.utils = new Utils(this);

        // Load the plugin configs
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Initialize the database
        this.database = switch (getConfig().getString("database.type")) {
            case "MySQL" -> new MySqlDatabase(this);
            default -> new SQLiteDatabase(this);
        };
        database.initialize();

        if (!database.hasLoaded()) {
            log(Level.SEVERE, "Failed to load database! Please check your credentials! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load the teams
        runAsync(() -> {
            teamStorageUtil.loadTeams();
        });

        // Register command completions
        this.manager.getCommandCompletions().registerAsyncCompletion("teams", c -> teamStorageUtil.getTeamsListNames());
        this.manager.getCommandCompletions().registerAsyncCompletion("warps", c -> {
            Team team;
            if (teamStorageUtil.findTeamByOwner(c.getPlayer()) != null) {
                team = teamStorageUtil.findTeamByOwner(c.getPlayer());
            } else {
                team = teamStorageUtil.findTeamByPlayer(c.getPlayer());
            }

            if (team == null) {
                return new ArrayList<>();
            }

            final Collection<TeamWarp> warps = team.getTeamWarps();
            final Collection<String> names = new ArrayList<>();

            warps.forEach(warp -> names.add(warp.getName()));

            return names;
        });
        this.manager.getCommandCompletions().registerAsyncCompletion("teamPlayers", c -> {
            Team team;
            if (teamStorageUtil.findTeamByOwner(c.getPlayer()) != null) {
                team = teamStorageUtil.findTeamByOwner(c.getPlayer());
            } else {
                team = teamStorageUtil.findTeamByPlayer(c.getPlayer());
            }

            if (team == null) {
                return new ArrayList<>();
            }

            return team.getTeamMembers();
        });

        // Register the plugin commands
        this.manager.registerCommand(new TeamCommand(this));
        this.manager.registerCommand(new TeamChatSpyCommand(this));
        this.manager.registerCommand(new TeamChatCommand(this));
        this.manager.registerCommand(new TeamAdmin(this));

        // Register the plugin events
        this.getServer().getPluginManager().registerEvents(new PlayerConnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(this), this);

        // Update banned tags list
        TeamCommand.updateBannedTagsList();

        // Register PlaceHolderAPI hooks
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") || isPlaceholderAPIEnabled()) {
            new PapiExpansion(this).register();

            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3PlaceholderAPI found!");
            sendConsole("&6UltimateTeams: &3External placeholders enabled!");
            sendConsole("-------------------------------------------");
        } else {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &cPlaceholderAPI not found!");
            sendConsole("&6UltimateTeams: &cExternal placeholders disabled!");
            sendConsole("-------------------------------------------");
        }

        // Register FloodgateApi hooks
        if (getServer().getPluginManager().isPluginEnabled("floodgate") || isFloodgateEnabled()) {
            floodgateApi = FloodgateApi.getInstance();
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3FloodgateApi found!");
            sendConsole("&6UltimateTeams: &3Full Bedrock support enabled!");
            sendConsole("-------------------------------------------");
        } else {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3FloodgateApi not found!");
            sendConsole("&6UltimateTeams: &3Bedrock support may not function!");
            sendConsole("-------------------------------------------");
        }

        // Start auto invite clear task
        if (getConfig().getBoolean("general.run-auto-invite-wipe-task.enabled")) {
            runSyncRepeating(() -> {
                try {
                    teamInviteUtil.emptyInviteList();
                    if (getConfig().getBoolean("general.show-auto-invite-wipe-message.enabled")){
                        sendConsole(msgFileManager.getMessagesConfig().getString("auto-invite-wipe-complete"));
                    }
                } catch (UnsupportedOperationException e) {
                    getLogger().info(Utils.Color(msgFileManager.getMessagesConfig().getString("invite-wipe-failed")));
                }
            }, 20L * 900);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        sendConsole("-------------------------------------------");
        sendConsole("&6UltimateTeams: &3Plugin by: &b&lxF3d3");

        // Cancel plugin tasks
        getScheduler().cancelGlobalTasks();

        // Close database connection
        database.close();

        // Final plugin shutdown message
        sendConsole("&6UltimateTeams: &3Plugin Version: &d&l" + pluginVersion);
        sendConsole("&6UltimateTeams: &3Has been shutdown successfully");
        sendConsole("&6UltimateTeams: &3Goodbye!");
        sendConsole("-------------------------------------------");
    }

    private boolean isFloodgateEnabled() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")) {
                sendConsole("&6UltimateTeams-Debug: &aFound FloodgateApi class at:");
                sendConsole("&6UltimateTeams-Debug: &dorg.geysermc.floodgate.api.FloodgateApi");
            }
            return true;
            
        } catch (ClassNotFoundException e) {
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")) {
                sendConsole("&6UltimateTeams-Debug: &aCould not find FloodgateApi class at:");
                sendConsole("&6UltimateTeams-Debug: &dorg.geysermc.floodgate.api.FloodgateApi");
            }
            return false;
        }
    }

    private boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");

            if (getConfig().getBoolean("general.developer-debug-mode.enabled")) {
                sendConsole("&6UltimateTeams-Debug: &aFound PlaceholderAPI main class at:");
                sendConsole("&6UltimateTeams-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin");
            }
            return true;

        } catch (ClassNotFoundException e) {
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")) {
                sendConsole("&6UltimateTeams-Debug: &aCould not find PlaceholderAPI main class at:");
                sendConsole("&6UltimateTeams-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin");
            }
            return false;
        }
    }

    public void log(@NotNull Level level, @NotNull String message, @Nullable Throwable... throwable) {
        if (throwable != null && throwable.length > 0) {
            getLogger().log(level, message, throwable[0]);
            return;
        }
        getLogger().log(level, message);
    }

    public void sendConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(Utils.Color(text));
    }

    public static UltimateTeams getPlugin() {
        return instance;
    }

    public FloodgateApi getFloodgateApi() {
        return floodgateApi;
    }

    @NotNull
    public TeamStorageUtil getTeamStorageUtil() {
        return teamStorageUtil;
    }

    @NotNull
    public UsersStorageUtil getUsersStorageUtil() {
        return usersStorageUtil;
    }

    @NotNull
    public TeamInviteUtil getTeamInviteUtil() {
        return teamInviteUtil;
    }

    @NotNull
    public ConcurrentHashMap<Player, String> getConnectedPlayers() {
        return connectedPlayers;
    }

    @NotNull
    public ConcurrentHashMap<Player, String> getBedrockPlayers() {
        return bedrockPlayers;
    }

    @NotNull
    public Database getDatabase() {
        return database;
    }

    @NotNull
    public Utils getUtils() {
        return utils;
    }

    @NotNull
    public Gson getGson() {
        return Converters.registerOffsetDateTime(new GsonBuilder().excludeFieldsWithoutExposeAnnotation()).create();
    }

    @Override
    @NotNull
    public GracefulScheduling getScheduler() {
        return paperLib.scheduling();
    }

    @Override
    @NotNull
    public ConcurrentHashMap<Integer, ScheduledTask> getTasks() {
        return tasks;
    }

}