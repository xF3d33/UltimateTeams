package dev.xf3d3.ultimateteams;

import co.aikar.commands.PaperCommandManager;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xf3d3.ultimateteams.commands.TeamAdmin;
import dev.xf3d3.ultimateteams.commands.TeamChatCommand;
import dev.xf3d3.ultimateteams.commands.TeamChatSpyCommand;
import dev.xf3d3.ultimateteams.commands.TeamCommand;
import dev.xf3d3.ultimateteams.config.TeamsGui;
import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.database.MySqlDatabase;
import dev.xf3d3.ultimateteams.database.SQLiteDatabase;
import dev.xf3d3.ultimateteams.hooks.FloodgateAPIHook;
import dev.xf3d3.ultimateteams.hooks.HuskHomesAPIHook;
import dev.xf3d3.ultimateteams.hooks.PapiExpansion;
import dev.xf3d3.ultimateteams.config.MessagesFileManager;
import dev.xf3d3.ultimateteams.config.Settings;
import dev.xf3d3.ultimateteams.listeners.PlayerConnectEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDamageEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDisconnectEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.*;
import net.william278.annotaml.Annotaml;
import net.william278.desertwell.util.ThrowingConsumer;
import net.william278.desertwell.util.Version;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class UltimateTeams extends JavaPlugin implements TaskRunner {
    private static final int METRICS_ID = 18842;

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    private static UltimateTeams instance;

    public MessagesFileManager msgFileManager;

    private FloodgateAPIHook floodgateAPIHook;
    private Database database;
    private Utils utils;
    private ConcurrentHashMap<Integer, ScheduledTask> tasks;
    private MorePaperLib paperLib;
    private PaperCommandManager manager;
    private TeamStorageUtil teamStorageUtil;
    private UsersStorageUtil usersStorageUtil;
    private TeamInviteUtil teamInviteUtil;
    private HuskHomesAPIHook huskHomesHook;
    private UpdateCheck updateChecker;
    private Settings settings;
    private TeamsGui teamsGui;

    // HashMaps
    private final ConcurrentHashMap<Player, String> connectedPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, String> bedrockPlayers = new ConcurrentHashMap<>();

    @Override
    public void onLoad() {
        // Set the instance
        instance = this;
    }

    private void initialize(@NotNull String name, @NotNull ThrowingConsumer<UltimateTeams> runner) {
        log(Level.INFO, "Initializing " + name + "...");
        try {
            runner.accept(this);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize " + name, e);
        }
        log(Level.INFO, "Successfully initialized " + name);
    }

    @Override
    public void onEnable() {
        this.tasks = new ConcurrentHashMap<>();
        this.paperLib = new MorePaperLib(this);
        this.manager = new PaperCommandManager(this);
        this.msgFileManager = new MessagesFileManager(this);
        this.teamStorageUtil = new TeamStorageUtil(this);
        this.usersStorageUtil = new UsersStorageUtil(this);
        this.teamInviteUtil = new TeamInviteUtil(this);
        this.utils = new Utils(this);
        this.updateChecker = new UpdateCheck(this);

        // Load settings and locales
        initialize("plugin config & locale files", (plugin) -> {
            if (!loadConfigs()) {
                throw new IllegalStateException("Failed to load config files. Please check the console for errors");
            }
        });

        // Initialize the database
        initialize(getSettings().getDatabaseType().getDisplayName() + " database connection", (plugin) -> {
            this.database = switch (getSettings().getDatabaseType()) {
                case MYSQL -> new MySqlDatabase(this);
                case SQLITE -> new SQLiteDatabase(this);
            };

            database.initialize();
        });

        if (!database.hasLoaded()) {
            log(Level.SEVERE, "Failed to load database! Please check your credentials! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        initialize("commands", (plugin) -> registerCommands());

        // Register events
        initialize("events", (plugin) -> registerEvents());

        // Load the teams
        initialize("teams", (plugin) -> runAsync(() -> {
            teamStorageUtil.loadTeams();
        }));

        // Initialize HuskHomes hook
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null && getSettings().HuskHomesHook()) {
            initialize("huskhomes" , (plugin) -> this.huskHomesHook = new HuskHomesAPIHook(this));
        }


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

        // Update banned tags list
        TeamCommand.updateBannedTagsList();

        // Register PlaceHolderAPI hooks
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") || isPlaceholderAPIEnabled()) {

            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3PlaceholderAPI found!");

            initialize("placeholderapi", (plugin) -> new PapiExpansion(this).register());

            sendConsole("&6UltimateTeams: &3External placeholders enabled!");
            sendConsole("-------------------------------------------");
        } else {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &cPlaceholderAPI not found!");
            sendConsole("&6UltimateTeams: &cExternal placeholders disabled!");
            sendConsole("-------------------------------------------");
        }

        // Register FloodgateApi hooks
        if (getServer().getPluginManager().isPluginEnabled("floodgate") && getSettings().FloodGateHook()) {

            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3FloodgateApi found!");

            initialize("floodgate", (plugin) -> this.floodgateAPIHook = new FloodgateAPIHook());

            sendConsole("&6UltimateTeams: &3Full Bedrock support enabled!");
            sendConsole("-------------------------------------------");
        } else {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3FloodgateApi not found!");
            sendConsole("&6UltimateTeams: &3Bedrock support won't work!");
            sendConsole("-------------------------------------------");
        }

        // Start auto invite clear task
        if (getSettings().enableAutoInviteWipe()) {
            runSyncRepeating(() -> {
                try {
                    teamInviteUtil.emptyInviteList();
                    if (getSettings().enableAutoInviteWipe()){
                        sendConsole(msgFileManager.getMessagesConfig().getString("auto-invite-wipe-complete"));
                    }
                } catch (UnsupportedOperationException e) {
                    log(Level.WARNING, Utils.Color(msgFileManager.getMessagesConfig().getString("invite-wipe-failed")));
                }
            }, 20L * 900);
        }

        // Hook into bStats
        initialize("metrics", (plugin) -> this.registerMetrics(METRICS_ID));

        // Check for updates
        if (getSettings().doCheckForUpdates()) {
            updateChecker.checkForUpdates();
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

    public void setSettings(@NotNull Settings settings) {
        this.settings = settings;
    }

    public void setGuiFile(@NotNull TeamsGui teamsGui) {
        this.teamsGui = teamsGui;
    }

     /**
     * Reloads the {@link Settings} from its config file
     *
     * @return {@code true} if the reload was successful, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean loadConfigs() {
        try {
            // Load settings
            setSettings(Annotaml.create(new File(getDataFolder(), "config.yml"), Settings.class).get());

            // Load Gui File
            setGuiFile(Annotaml.create(new File(getDataFolder(), "teamgui.yml"), TeamsGui.class).get());

            return true;
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log(Level.SEVERE, "Failed to reload UltimateTeams config or messages file", e);
        }
        return false;
    }

    @NotNull
    public Settings getSettings() {
        return settings;
    }

    @NotNull
    public TeamsGui getTeamsGui() {
        return teamsGui;
    }


    private void registerCommands() {
        // Register the plugin commands
        this.manager.registerCommand(new TeamCommand(this));
        this.manager.registerCommand(new TeamChatSpyCommand(this));
        this.manager.registerCommand(new TeamChatCommand(this));
        this.manager.registerCommand(new TeamAdmin(this));
    }

    public void registerEvents() {
        // Register the plugin events
        this.getServer().getPluginManager().registerEvents(new PlayerConnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(this), this);
    }

    private boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");

            if (getSettings().debugModeEnabled()) {
                sendConsole("&6UltimateTeams-Debug: &aFound PlaceholderAPI main class at:");
                sendConsole("&6UltimateTeams-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin");
            }
            return true;

        } catch (ClassNotFoundException e) {
            if (getSettings().debugModeEnabled()) {
                sendConsole("&6UltimateTeams-Debug: &aCould not find PlaceholderAPI main class at:");
                sendConsole("&6UltimateTeams-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin");
            }
            return false;
        }
    }

    public void registerMetrics(int metricsId) {
        try {
            final Metrics metrics = new Metrics(this, metricsId);

            metrics.addCustomChart(new SimplePie("database_type", () -> getSettings().getDatabaseType().getDisplayName()));
            metrics.addCustomChart(new SimplePie("huskhomes_hook", () -> Boolean.toString(getSettings().HuskHomesHook())));
            metrics.addCustomChart(new SimplePie("floodgate_hook", () -> Boolean.toString(getSettings().FloodGateHook())));

        } catch (Throwable e) {
            log(Level.WARNING, "Failed to register bStats metrics (" + e.getMessage() + ")");
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
        return floodgateAPIHook.getHook();
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
    public HuskHomesAPIHook getHuskHomesHook() {
        return huskHomesHook;
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