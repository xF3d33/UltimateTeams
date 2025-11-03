package dev.xf3d3.ultimateteams;

import co.aikar.commands.BukkitCommandManager;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import dev.xf3d3.ultimateteams.api.UltimateTeamsAPI;
import dev.xf3d3.ultimateteams.api.UltimateTeamsAPIImpl;
import dev.xf3d3.ultimateteams.commands.TeamAdmin;
import dev.xf3d3.ultimateteams.commands.TeamCommand;
import dev.xf3d3.ultimateteams.commands.chat.TeamAllyChatCommand;
import dev.xf3d3.ultimateteams.commands.chat.TeamChatCommand;
import dev.xf3d3.ultimateteams.commands.chat.TeamChatSpyCommand;
import dev.xf3d3.ultimateteams.config.MessagesFileManager;
import dev.xf3d3.ultimateteams.config.Settings;
import dev.xf3d3.ultimateteams.config.TeamsGui;
import dev.xf3d3.ultimateteams.database.*;
import dev.xf3d3.ultimateteams.hooks.*;
import dev.xf3d3.ultimateteams.listeners.PlayerChatEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerConnectEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDamageEvent;
import dev.xf3d3.ultimateteams.listeners.PlayerDisconnectEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.User;
import dev.xf3d3.ultimateteams.network.Broker;
import dev.xf3d3.ultimateteams.network.PluginMessageBroker;
import dev.xf3d3.ultimateteams.network.RedisBroker;
import dev.xf3d3.ultimateteams.utils.*;
import dev.xf3d3.ultimateteams.utils.gson.GsonUtils;
import lombok.Getter;
import lombok.Setter;
import net.william278.annotaml.Annotaml;
import net.william278.desertwell.util.ThrowingConsumer;
import net.william278.desertwell.util.Version;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class UltimateTeams extends JavaPlugin implements TaskRunner, GsonUtils, PluginMessageListener {
    private static UltimateTeams instance;
    private UltimateTeamsAPI api;

    @Getter @Setter
    public boolean loaded = false;

    private static final int METRICS_ID = 18842;
    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();

    public MessagesFileManager msgFileManager;

    @Getter private Database database;
    @Nullable private Broker broker;

    private FoliaLib foliaLib;
    private BukkitCommandManager manager;
    private TeamsStorage teamsStorage;
    private UsersStorage usersStorage;
    private TeamInviteUtil teamInviteUtil;
    private FloodgateHook floodgateHook;
    private UpdateCheck updateChecker;

    @Getter private HuskHomesHook huskHomesHook;
    @Getter private Utils utils;
    @Getter private Settings settings;
    @Getter private TeamsGui teamsGui;
    @Getter @Nullable private VaultHook economyHook;
    @Getter @Nullable private EssentialsHook essentialsHook;
    @Getter private EnderChestBackupManager backupManager;

    // HashMaps
    private final ConcurrentHashMap<String, Player> bedrockPlayers = new ConcurrentHashMap<>();

    @Override
    public void onLoad() {
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
        this.foliaLib = new FoliaLib(this);
        this.manager = new BukkitCommandManager(this);
        this.msgFileManager = new MessagesFileManager(this);
        this.teamsStorage = new TeamsStorage(this);
        this.usersStorage = new UsersStorage(this);
        this.teamInviteUtil = new TeamInviteUtil(this);
        this.utils = new Utils(this);
        this.updateChecker = new UpdateCheck(this);

        this.api = new UltimateTeamsAPIImpl(this);

        // Load settings and locales
        initialize("plugin config & locale files", (plugin) -> {
            if (!loadConfigs()) {
                throw new IllegalStateException("Failed to load config files. Please check the console for errors");
            }
        });

        // Initialize the database
        initialize(getSettings().getDatabaseType().getDisplayName() + " database connection", (plugin) -> {
            this.database = switch (getSettings().getDatabaseType()) {
                case MYSQL, MARIADB -> new MySqlDatabase(this);
                case SQLITE -> new SQLiteDatabase(this);
                case H2 -> new H2Database(this);
                case POSTGRESQL -> new PostgreSqlDatabase(this);
            };

            database.initialize();
        });

        // Initialize message broker
        if (getSettings().isEnableCrossServer()) {
            initialize(getSettings().getBrokerType() + " broker", (plugin) -> {
                final Broker.Type brokerType = getSettings().getBrokerType();

                this.broker = switch (brokerType) {
                    case PLUGIN_MESSAGE -> new PluginMessageBroker(this);
                    case REDIS -> new RedisBroker(this);
                };

                broker.initialize();
            });
        }

        // Register commands
        initialize("commands", (plugin) -> {
            this.manager.registerCommand(new TeamCommand(this));
            this.manager.registerCommand(new TeamChatSpyCommand(this));
            this.manager.registerCommand(new TeamChatCommand(this));
            this.manager.registerCommand(new TeamAllyChatCommand(this));
            this.manager.registerCommand(new TeamAdmin(this));
        });

        // Register events
        initialize("events", (plugin) -> {
            this.getServer().getPluginManager().registerEvents(new PlayerConnectEvent(this), this);
            this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(this), this);
            this.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(this), this);
            this.getServer().getPluginManager().registerEvents(new PlayerChatEvent(this), this);
        });

        // Load the teams
        initialize("teams", (plugin) -> runAsync(task -> teamsStorage.loadTeams()));

        // Initialize backup manager for ender chests
        initialize("ender chest backup manager", (plugin) -> this.backupManager = new EnderChestBackupManager(this));

        // Initialize HuskHomes hook
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null && getSettings().HuskHomesHook()) {
            initialize("huskhomes" , (plugin) -> this.huskHomesHook = new HuskHomesHook(this));
        }

        // Initialize LuckPerms hook
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null && getSettings().LuckPermsHook()) {
            initialize("luckperms" , (plugin) -> new LuckPermsHook(this));
        }

        // Register command completions
        this.manager.getCommandCompletions().registerAsyncCompletion("onlineUsers", c -> getUsersStorageUtil().getUserList().stream().map(User::getUsername).collect(Collectors.toList()));
        this.manager.getCommandCompletions().registerAsyncCompletion("teams", c -> teamsStorage.getTeamsName().stream().map(Utils::removeColors).collect(Collectors.toList()));
        this.manager.getCommandCompletions().registerAsyncCompletion("warps", c -> getTeamStorageUtil().findTeamByMember(c.getPlayer().getUniqueId())
                .map(team -> team.getWarps().keySet())
                .orElse(Collections.emptySet())
        );
        this.manager.getCommandCompletions().registerAsyncCompletion("teamPlayers", c -> getTeamStorageUtil().findTeamByMember(c.getPlayer().getUniqueId())
                .map(team -> team.getMembers().keySet().stream()
                        .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet())
        );
        this.manager.getCommandCompletions().registerAsyncCompletion("allies", c -> getTeamStorageUtil().findTeamByMember(c.getPlayer().getUniqueId())
                .map(team -> team.getRelations(this).entrySet().stream()
                        .filter(teamRelationEntry -> teamRelationEntry.getValue() == Team.Relation.ALLY)
                        .map(entry -> entry.getKey().getName())
                        .map(Utils::removeColors)
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet())
        );
        this.manager.getCommandCompletions().registerAsyncCompletion("enemies", c -> getTeamStorageUtil().findTeamByMember(c.getPlayer().getUniqueId())
                .map(team -> team.getRelations(this).entrySet().stream()
                        .filter(teamRelationEntry -> teamRelationEntry.getValue() == Team.Relation.ENEMY)
                        .map(entry -> entry.getKey().getName())
                        .map(Utils::removeColors)
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet())
        );
        this.manager.getCommandCompletions().registerAsyncCompletion("teamPermissions", c -> getTeamStorageUtil().findTeamByMember(c.getPlayer().getUniqueId())
                .map(team -> team.getPermissions().stream()
                        .map(String::valueOf)
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet())
        );
        this.manager.getCommandCompletions().registerAsyncCompletion("permissions", c -> Arrays.stream(Team.Permission.values()).map(String::valueOf).collect(Collectors.toSet())
        );

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

            initialize("floodgate", (plugin) -> this.floodgateHook = new FloodgateHook());

            sendConsole("&6UltimateTeams: &3Full Bedrock support enabled!");
            sendConsole("-------------------------------------------");
        } else {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3FloodgateApi not found/feature disabled!");
            sendConsole("&6UltimateTeams: &3Bedrock support won't work!");
            sendConsole("-------------------------------------------");
        }

        // Enable economy features
        if (getServer().getPluginManager().isPluginEnabled("Vault") && getSettings().isEconomyEnabled()) {
            initialize("economy", (plugin) -> this.economyHook = new VaultHook(this));
        }

        // Initialize EssentialsX hook
        if (getServer().getPluginManager().isPluginEnabled("Essentials") && getSettings().EssentialsHook()) {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3EssentialsX found!");
            
            initialize("essentials", (plugin) -> {
                this.essentialsHook = new EssentialsHook(this);
                // Update all online players' scoreboard teams
                this.essentialsHook.updateAllPlayers();
            });
            
            sendConsole("&6UltimateTeams: &3Team data will be available via {TEAMNAME}, {TEAMPREFIX}, {TEAMSUFFIX}");
            sendConsole("-------------------------------------------");
        } else {
            sendConsole("-------------------------------------------");
            sendConsole("&6UltimateTeams: &3EssentialsX not found/feature disabled!");
            sendConsole("&6UltimateTeams: &3Scoreboard team integration disabled!");
            sendConsole("-------------------------------------------");
        }

        // Start auto invite clear task
        if (getSettings().enableAutoInviteWipe()) {
            runSyncRepeating(() -> {
                teamInviteUtil.emptyInviteList();
                if (getSettings().enableAutoInviteWipeLog()){
                    sendConsole(msgFileManager.getMessagesConfig().getString("auto-invite-wipe-complete"));
                }
            }, 12000);
        }

        // Hook into bStats
        initialize("metrics", (plugin) -> this.registerMetrics(METRICS_ID));

        // Check for updates
        if (getSettings().doCheckForUpdates()) {
            updateChecker.checkForUpdates();
        }

        // Plugin enabled message
        sendConsole("-------------------------------------------");
        sendConsole("&6UltimateTeams: &3Plugin by: &b&lxF3d3");
        sendConsole("&6UltimateTeams: &3Improved by: &b&ldei0");
        sendConsole("&6UltimateTeams: &3GitHub: &e&nhttps://github.com/dei2004");
        sendConsole("&6UltimateTeams: &3Version: &d&l" + pluginVersion);
        sendConsole("&6UltimateTeams: &aSuccessfully enabled!");
        sendConsole("-------------------------------------------");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        sendConsole("-------------------------------------------");
        sendConsole("&6UltimateTeams: &3Plugin by: &b&lxF3d3");
        sendConsole("&6UltimateTeams: &3Improved by: &b&ldei0");

        // Shutdown backup manager
        if (backupManager != null) {
            backupManager.shutdown();
        }

        // Cancel plugin tasks and close the database connection
        getScheduler().cancelAllTasks();
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
            metrics.addCustomChart(new SimplePie("economy", () -> Boolean.toString(getSettings().isEconomyEnabled())));

            metrics.addCustomChart(new SimplePie("cross_server", () -> Boolean.toString(getSettings().isEnableCrossServer())));
            if (getSettings().isEnableCrossServer()) {
                metrics.addCustomChart(new SimplePie("broker_type", () -> getSettings().getBrokerType().name().toLowerCase()));
            }

        } catch (Exception e) {
            log(Level.WARNING, "Failed to register bStats metrics", e);
        }
    }

    public void initializePluginChannels() {
        getServer().getMessenger().registerIncomingPluginChannel(this, PluginMessageBroker.BUNGEE_CHANNEL_ID, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PluginMessageBroker.BUNGEE_CHANNEL_ID);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (broker != null && broker instanceof PluginMessageBroker pluginMessenger
                && getSettings().getBrokerType() == Broker.Type.PLUGIN_MESSAGE) {
            pluginMessenger.onReceive(channel, player, message);
        }
    }

    public void log(@NotNull Level level, @NotNull String message, @Nullable Throwable... throwable) {
        if (throwable != null && throwable.length > 0) {
            getLogger().log(level, message, throwable[0]);
            return;
        }
        getLogger().log(level, message);
    }

    @NotNull
    public Optional<Broker> getMessageBroker() {
        return Optional.ofNullable(broker);
    }

    @NotNull
    public Version getPluginVersion() {
        return Version.fromString(getDescription().getVersion());
    }

    public static UltimateTeamsAPI getAPI() {
        return instance.api;
    }

    public void sendConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(Utils.Color(text));
    }

    public static UltimateTeams getPlugin() {
        return instance;
    }

    @NotNull
    public TeamsStorage getTeamStorageUtil() {
        return teamsStorage;
    }

    @NotNull
    public UsersStorage getUsersStorageUtil() {
        return usersStorage;
    }

    @NotNull
    public TeamInviteUtil getTeamInviteUtil() {
        return teamInviteUtil;
    }

    public FloodgateApi getFloodgateApi() {
        return floodgateHook.getHook();
    }

    @Override
    @NotNull
    public PlatformScheduler getScheduler() {
        return foliaLib.getScheduler();
    }
}