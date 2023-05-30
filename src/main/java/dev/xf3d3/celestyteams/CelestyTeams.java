package dev.xf3d3.celestyteams;

import co.aikar.commands.PaperCommandManager;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xf3d3.celestyteams.commands.TeamAdmin;
import dev.xf3d3.celestyteams.commands.TeamChatCommand;
import dev.xf3d3.celestyteams.commands.TeamChatSpyCommand;
import dev.xf3d3.celestyteams.commands.TeamCommand;
import dev.xf3d3.celestyteams.commands.commandTabCompleters.ClanAdminTabCompleter;
import dev.xf3d3.celestyteams.database.MySqlDatabase;
import dev.xf3d3.celestyteams.expansions.PapiExpansion;
import dev.xf3d3.celestyteams.files.MessagesFileManager;
import dev.xf3d3.celestyteams.files.TeamGUIFileManager;
import dev.xf3d3.celestyteams.listeners.MenuEvent;
import dev.xf3d3.celestyteams.listeners.PlayerConnectEvent;
import dev.xf3d3.celestyteams.listeners.PlayerDamageEvent;
import dev.xf3d3.celestyteams.listeners.PlayerDisconnectEvent;
import dev.xf3d3.celestyteams.menuSystem.PlayerMenuUtility;
import dev.xf3d3.celestyteams.utils.*;
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

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CelestyTeams extends JavaPlugin implements TaskRunner {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    Logger logger = this.getLogger();
    private static CelestyTeams instance;
    private FloodgateApi floodgateApi;
    public MessagesFileManager messagesFileManager;
    public TeamGUIFileManager teamGUIFileManager;
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private MySqlDatabase database;
    private ConcurrentHashMap<Integer, ScheduledTask> tasks;
    private MorePaperLib paperLib;
    private PaperCommandManager manager;
    private TeamStorageUtil teamStorageUtil;
    private UsersStorageUtil usersStorageUtil;
    private TeamInviteUtil teamInviteUtil;
    private HashMap<Player, String> connectedPlayers = new HashMap<>();
    private HashMap<Player, String> bedrockPlayers = new HashMap<>();

    @Override
    public void onLoad() {
        // Set the instance
        instance = this;
    }

    @Override
    public void onEnable() {
        this.paperLib = new MorePaperLib(this);
        this.tasks = new ConcurrentHashMap<>();
        this.manager = new PaperCommandManager(this);
        this.teamGUIFileManager = new TeamGUIFileManager(this);
        this.messagesFileManager = new MessagesFileManager(this);
        this.teamStorageUtil = new TeamStorageUtil(this);
        this.usersStorageUtil = new UsersStorageUtil(this);
        this.teamInviteUtil = new TeamInviteUtil(this);

        // Load the plugin configs
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.database = new MySqlDatabase(this);
        database.initialize();

        runAsync(() -> {
            teamStorageUtil.loadTeams();
            usersStorageUtil.loadUsers();
        });

        this.manager.getCommandCompletions().registerCompletion("teams", ctx -> {
            if (!(ctx.getSender() instanceof Player)) {
                return null;
            }

            return teamStorageUtil.getTeamsListNames();
        });

        //Register the plugin commands
        this.manager.registerCommand(new TeamCommand(this));
        this.manager.registerCommand(new TeamChatSpyCommand(this));
        this.manager.registerCommand(new TeamChatCommand(this));
        this.getCommand("teamadmin").setExecutor(new TeamAdmin(this));

        //Register the command tab completer
        this.getCommand("teamadmin").setTabCompleter(new ClanAdminTabCompleter());

        //Register the plugin events
        this.getServer().getPluginManager().registerEvents(new PlayerConnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new MenuEvent(), this);

        // Update banned tags list
        TeamCommand.updateBannedTagsList();

        //Register PlaceHolderAPI hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") || isPlaceholderAPIEnabled()){
            new PapiExpansion(this).register();
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3PlaceholderAPI found!"));
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3External placeholders enabled!"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        } else {
            logger.warning(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.warning(ColorUtils.translateColorCodes("&6CelestyTeams: &cPlaceholderAPI not found!"));
            logger.warning(ColorUtils.translateColorCodes("&6CelestyTeams: &cExternal placeholders disabled!"));
            logger.warning(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Register FloodgateApi hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate") || isFloodgateEnabled()){
            floodgateApi = FloodgateApi.getInstance();
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3FloodgateApi found!"));
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Full Bedrock support enabled!"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        } else {
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3FloodgateApi not found!"));
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Bedrock support may not function!"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Start auto invite clear task
        if (getConfig().getBoolean("general.run-auto-invite-wipe-task.enabled")){
            runAsyncRepeating(() -> {
                try {
                    teamInviteUtil.emptyInviteList();
                    if (getConfig().getBoolean("general.show-auto-invite-wipe-message.enabled")){
                        getLogger().info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-complete")));
                    }
                }catch (UnsupportedOperationException exception){
                    getLogger().info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("invite-wipe-failed")));
                }
            }, 20L * 900);
        }
    }

    @Override
    public void onDisable() {
        //Plugin shutdown logic
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Plugin by: &b&lxF3d3"));

        database.close();

        // Final plugin shutdown message
        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Plugin Version: &d&l" + pluginVersion));
        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Has been shutdown successfully"));
        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Goodbye!"));
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(player))) {
            playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player);
        }
    }

    public boolean isFloodgateEnabled() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFound FloodgateApi class at:"));
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &dorg.geysermc.floodgate.api.FloodgateApi"));
            }
            return true;
        } catch (ClassNotFoundException e) {
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aCould not find FloodgateApi class at:"));
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &dorg.geysermc.floodgate.api.FloodgateApi"));
            }
            return false;
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFound PlaceholderAPI main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aCould not find PlaceholderAPI main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
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

    public static CelestyTeams getPlugin() {
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
    public HashMap<Player, String> getConnectedPlayers() {
        return connectedPlayers;
    }

    @NotNull
    public HashMap<Player, String> getBedrockPlayers() {
        return bedrockPlayers;
    }

    @NotNull
    public MySqlDatabase getDatabase() {
        return database;
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