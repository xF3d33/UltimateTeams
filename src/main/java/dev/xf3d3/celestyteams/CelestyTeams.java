package dev.xf3d3.celestyteams;

import co.aikar.commands.PaperCommandManager;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcoded.folialib.FoliaLib;
import dev.xf3d3.celestyteams.commands.TeamAdmin;
import dev.xf3d3.celestyteams.commands.TeamChatCommand;
import dev.xf3d3.celestyteams.commands.TeamChatSpyCommand;
import dev.xf3d3.celestyteams.commands.TeamCommand;
import dev.xf3d3.celestyteams.commands.commandTabCompleters.ClanAdminTabCompleter;
import dev.xf3d3.celestyteams.database.MySqlDatabase;
import dev.xf3d3.celestyteams.expansions.PapiExpansion;
import dev.xf3d3.celestyteams.files.TeamGUIFileManager;
import dev.xf3d3.celestyteams.files.MessagesFileManager;
import dev.xf3d3.celestyteams.listeners.MenuEvent;
import dev.xf3d3.celestyteams.listeners.PlayerConnectionEvent;
import dev.xf3d3.celestyteams.listeners.PlayerDamageEvent;
import dev.xf3d3.celestyteams.listeners.PlayerDisconnectEvent;
import dev.xf3d3.celestyteams.menuSystem.PlayerMenuUtility;
import dev.xf3d3.celestyteams.utils.TeamStorageUtil;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import dev.xf3d3.celestyteams.utils.TaskRunner;
import dev.xf3d3.celestyteams.utils.UsersStorageUtil;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CelestyTeams extends JavaPlugin implements TaskRunner {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    private final FoliaLib foliaLib = new FoliaLib(this);
    Logger logger = this.getLogger();

    private static CelestyTeams instance;
    private static FloodgateApi floodgateApi;
    public MessagesFileManager messagesFileManager;
    public TeamGUIFileManager teamGUIFileManager;

    public static HashMap<Player, String> connectedPlayers = new HashMap<>();
    public static HashMap<Player, String> bedrockPlayers = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private MySqlDatabase database;
    private ConcurrentHashMap<Integer, ScheduledTask> tasks;
    private MorePaperLib paperLib;
    private PaperCommandManager manager;
    private TeamStorageUtil teamStorageUtil;
    private UsersStorageUtil usersStorageUtil;

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

            return teamStorageUtil.getClanListNames();
        });

        //Register the plugin commands
        this.manager.registerCommand(new TeamCommand(this));
        this.manager.registerCommand(new TeamChatSpyCommand(this));
        this.manager.registerCommand(new TeamChatCommand(this));
        this.getCommand("teamadmin").setExecutor(new TeamAdmin(this));

        //Register the command tab completer
        this.getCommand("teamadmin").setTabCompleter(new ClanAdminTabCompleter());

        //Register the plugin events
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(), this);
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

        //Start auto save task
        if (getConfig().getBoolean("general.run-auto-save-task.enabled")){
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    //TaskRunner.runClansAutoSaveOne();
                    logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-save-started")));
                }
            }, 5L, TimeUnit.SECONDS);
        }

        //Start auto invite clear task
        if (getConfig().getBoolean("general.run-auto-invite-wipe-task.enabled")){
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    //TaskRunner.runClanInviteClearOne();

                    logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-started")));
                }
            }, 5L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDisable() {
        //Plugin shutdown logic

        //Safely stop the background tasks if running
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Plugin by: &b&lLoving11ish"));
        /*try {
            if (!TaskRunner.task1.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aWrapped task: " + TaskRunner.task1.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aTimed task 1 canceled successfully"));
                }
                TaskRunner.task1.cancel();
            }
            if (!TaskRunner.task2.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aWrapped task: " + TaskRunner.task2.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aTimed task 2 canceled successfully"));
                }
                TaskRunner.task2.cancel();
            }
            if (!TaskRunner.task3.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aWrapped task: " + TaskRunner.task3.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aTimed task 3 canceled successfully"));
                }
                TaskRunner.task3.cancel();
            }
            if (!TaskRunner.task4.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aWrapped task: " + TaskRunner.task4.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aTimed task 4 canceled successfully"));
                }
                TaskRunner.task4.cancel();
            }
            if (!ClanListGUI.task5.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aWrapped task: " + ClanListGUI.task5.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aTimed task 5 canceled successfully"));
                }
                ClanListGUI.task5.cancel();
            }
            if (foliaLib.isUnsupported()){
                Bukkit.getScheduler().cancelTasks(this);
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aBukkit scheduler tasks canceled successfully"));
                }
            }
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Background tasks have disabled successfully!"));
        }catch (Exception e){
            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams: &3Background tasks have disabled successfully!"));
        }*/

        database.close();

        //Final plugin shutdown message
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

    public static FloodgateApi getFloodgateApi() {
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