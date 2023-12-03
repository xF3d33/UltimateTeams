package dev.xf3d3.ultimateteams;

import co.aikar.commands.PaperCommandManager;
import dev.xf3d3.ultimateteams.commands.*;
import dev.xf3d3.ultimateteams.commands.TeamAllyChatCommand;
import dev.xf3d3.ultimateteams.config.MessagesFileManager;
import dev.xf3d3.ultimateteams.config.Server;
import dev.xf3d3.ultimateteams.config.Settings;
import dev.xf3d3.ultimateteams.config.TeamsGui;
import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.database.MySqlDatabase;
import dev.xf3d3.ultimateteams.database.SQLiteDatabase;
import dev.xf3d3.ultimateteams.hooks.FloodgateAPIHook;
import dev.xf3d3.ultimateteams.hooks.HuskHomesAPIHook;
import dev.xf3d3.ultimateteams.hooks.PapiExpansion;
import dev.xf3d3.ultimateteams.listeners.PlayerConnectEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDamageEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDisconnectEvent;
import dev.xf3d3.ultimateteams.network.RedisBroker;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.team.TeamWarp;
import dev.xf3d3.ultimateteams.network.Broker;
import dev.xf3d3.ultimateteams.network.PluginMessages;
import dev.xf3d3.ultimateteams.utils.*;
import net.william278.annotaml.Annotaml;
import net.william278.desertwell.util.ThrowingConsumer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public final class UltimateTeams extends JavaPlugin implements TaskRunner, GsonProvider, PluginMessageListener {
    private static final int METRICS_ID = 18842;
    private UpdateCheck updateChecker;

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    private static UltimateTeams instance;

    public MessagesFileManager msgFileManager;


    private Utils utils;
    private ConcurrentHashMap<Integer, ScheduledTask> tasks;
    private MorePaperLib paperLib;
    private PaperCommandManager manager;

    // Communication
    private Database database;
    private Broker broker;

    // Handlers
    private TeamStorageUtil teamStorageUtil;
    private UsersStorageUtil usersStorageUtil;
    private TeamInviteUtil teamInviteUtil;

    // Hooks
    private HuskHomesAPIHook huskHomesHook;
    private FloodgateAPIHook floodgateAPIHook;

    // Configs
    private Settings settings;
    private TeamsGui teamsGui;
    private Server server;

    // HashMaps
    private final ConcurrentHashMap<Player, String> connectedPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, String> bedrockPlayers = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<Team> teams = new ConcurrentLinkedQueue<>();

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
        this.broker = this.loadBroker();

        // Load settings and locales
        initialize("plugin config & locale files", (plugin) -> {
            if (!loadConfigs()) {
                throw new IllegalStateException("Failed to load config files. Please check the console for errors");
            }

            if (getSettings().doCrossServer()) {
                setServer(Annotaml.create(new File(getDataFolder(), "server.yml"), Server.class).get());
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
            initialize("HuskHomes" , (plugin) -> this.huskHomesHook = new HuskHomesAPIHook(this));
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
            } else {
                ArrayList<String> members = new ArrayList<>();
                team.getTeamMembers().forEach(memberUUIDString -> {
                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(memberUUIDString));

                    members.add(offlinePlayer.getName());
                });

                return members;
            }
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
            sendConsole("&6UltimateTeams: &3FloodgateApi not found/feature disabled!");
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
            }, 12000);
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

        getMessageBroker().ifPresent(Broker::close);

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

    public void setServer(@NotNull Server server) {
        this.server = server;
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

    @NotNull
    public String getServerName() {
        return server != null ? server.getName() : "server";
    }


    private void registerCommands() {
        // Register the plugin commands
        this.manager.registerCommand(new TeamCommand(this));
        this.manager.registerCommand(new TeamChatSpyCommand(this));
        this.manager.registerCommand(new TeamChatCommand(this));
        this.manager.registerCommand(new TeamAllyChatCommand(this));
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

    public Broker loadBroker() throws RuntimeException {
        if (!getSettings().doCrossServer()) {
            return null;
        }

        final Broker broker = switch (getSettings().getBrokerType()) {
            case PLUGIN_MESSAGE -> new PluginMessages(this);
            case REDIS -> new RedisBroker(this);
        };
        broker.initialize();
        log(Level.INFO, "Successfully initialized the " + getSettings().getBrokerType().getDisplayName() + " broker");
        return broker;
    }

    public void log(@NotNull Level level, @NotNull String message, @Nullable Throwable... throwable) {
        if (throwable != null && throwable.length > 0) {
            getLogger().log(level, message, throwable[0]);
            return;
        }
        getLogger().log(level, message);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (broker != null && broker instanceof PluginMessages pluginMessenger
                && getSettings().getBrokerType() == Broker.Type.PLUGIN_MESSAGE) {
            pluginMessenger.onReceive(channel, player, message);
        }
    }

    public void initializePluginChannels() {
        Bukkit.getMessenger().registerIncomingPluginChannel(this, PluginMessages.BUNGEE_CHANNEL_ID, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMessages.BUNGEE_CHANNEL_ID);
    }


    public void sendConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(Utils.Color(text));
    }

    public static UltimateTeams getPlugin() {
        return instance;
    }

    public ConcurrentLinkedQueue<Team> getTeams() {
        return teams;
    }

    public void setTeams(@NotNull List<Team> teams) {
        this.teams = new ConcurrentLinkedQueue<>(teams);
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
    public Optional<Broker> getMessageBroker() {
        return Optional.ofNullable(broker);
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