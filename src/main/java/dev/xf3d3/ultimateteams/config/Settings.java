package dev.xf3d3.ultimateteams.config;

import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.network.Broker;
import lombok.Getter;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Plugin settings, read from config.yml
 */
@SuppressWarnings("unused")
@YamlFile(header = """
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃     UltimateTeams Config     ┃
        ┃      Developed by xF3d3      ┃
        ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
        ┃
        ┗╸ Information: https://modrinth.com/plugin/ultimate-teams""")
public class Settings {

    // Top-level settings
    @YamlComment("Do you want to use the GUI system? [Default value: true]")
    @YamlKey("use-global-GUI-system")
    private boolean useGlobalGui = true;

    // Database settings
    @YamlComment("Type of database to use (SQLITE, H2, MYSQL, MARIADB, or POSTGRESQL). MARIADB is preferred over MYSQL. H2 is preferred over SQLITE")
    @YamlKey("database.type")
    private Database.Type databaseType = Database.Type.SQLITE;

    @YamlComment("Specify credentials here if you are using MYSQL as your database type")
    @YamlKey("database.mysql.credentials.host")
    private String mySqlHost = "localhost";

    @YamlKey("database.mysql.credentials.port")
    private int mySqlPort = 3306;

    @YamlKey("database.mysql.credentials.database")
    private String mySqlDatabase = "ultimate_teams";

    @YamlKey("database.mysql.credentials.username")
    private String mySqlUsername = "root";

    @YamlKey("database.mysql.credentials.password")
    private String mySqlPassword = "pa55w0rd";

    @YamlKey("database.mysql.credentials.parameters")
    private String mySqlConnectionParameters = "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8";

    @YamlComment("MYSQL / MARIADB / POSTGRESQL database connection pool properties. Don't modify this unless you know what you're doing!")
    @YamlKey("database.mysql.connection_pool.size")
    private int mySqlConnectionPoolSize = 12;

    @YamlKey("database.mysql.connection_pool.idle")
    private int mySqlConnectionPoolIdle = 12;

    @YamlKey("database.mysql.connection_pool.lifetime")
    private long mySqlConnectionPoolLifetime = 1800000;

    @YamlKey("database.mysql.connection_pool.keepalive")
    private long mySqlConnectionPoolKeepAlive = 30000;

    @YamlKey("database.mysql.connection_pool.timeout")
    private long mySqlConnectionPoolTimeout = 20000;

    @YamlComment("Names of tables to use on your database. Don't modify this unless you know what you're doing!")
    @YamlKey("database.table_names")
    private Map<String, String> tableNames = Map.of(
        Database.Table.TEAM_DATA.name().toLowerCase(), Database.Table.TEAM_DATA.getDefaultName(),
        Database.Table.USER_DATA.name().toLowerCase(), Database.Table.USER_DATA.getDefaultName()
    );


    // cross-server settings
    @YamlComment("Whether enable cross-server mode. You must use MYSQL/MARIADB/POSTGRESQL to use cross-server")
    @YamlKey("cross-server.enable")
    @Getter
    private boolean enableCrossServer = false;

    @YamlComment("The name of the server as it appears on Bungee/Velocity (case-sensitive)")
    @YamlKey("cross-server.server-name")
    @Getter
    private String serverName = "Survival";

    @YamlComment("The cluster ID, for if you're networking multiple separate groups of UltimateTeams-enabled servers. Do not change unless you know what you're doing")
    @YamlKey("cross-server.cluster-id")
    @Getter
    private String clusterId = "main";

    @YamlComment("Type of network message broker to ues for data synchronization (PLUGIN_MESSAGE or REDIS). REDIS is preferred over PLUGIN_MESSAGE.")
    @YamlKey("cross-server.broker")
    @Getter
    private Broker.Type brokerType = Broker.Type.PLUGIN_MESSAGE;

    @YamlComment("Settings for if you're using REDIS as your message broker")
    @YamlKey("cross-server.redis.host")
    @Getter
    private String redisHost = "localhost";

    @YamlKey("cross-server.redis.port")
    @Getter
    private int redisPort = 6379;

    @YamlKey("cross-server.redis.password")
    @Getter
    private String redisPassword = "";

    @YamlKey("cross-server.redis.use-ssl")
    @Getter
    private boolean redisUseSSL = false;



    @YamlComment("Hook into luckperms to create contexts (e.g. is-in-team) [Default value: false]. Needs LuckPerms")
    @YamlKey("luckperms-hook")
    private boolean luckpermshook = false;

    @YamlComment("use HuskHomes to teleport players instead of built-in teleport handler [Default value: true]")
    @YamlKey("use-huskhomes")
    private boolean useHuskhomes = false;

    @YamlComment("Hook into floodgate to handle bedrock players properly [Default value: false]. Needs FloodGate")
    @YamlKey("floodgate-hook")
    private boolean floodgateHook = false;

    @YamlComment("When the placeholder would be blank, the text below will be shown instead (you can use color codes)")
    @YamlKey("placeholder.not-in-a-team")
    private String notInTeamPlaceholder = "Not in a team";


    @YamlComment("Whether to allow color codes (& and #) in the teams name")
    @YamlKey("team.name.allow-color-codes")
    @Getter
    private boolean teamCreateAllowColorCodes = false;

    @YamlComment("If enabled, players need the ultimateteams.team.create.usecolors permission to use color codes")
    @YamlKey("team.name.require-perm-for-color-codes")
    @Getter
    private boolean teamCreateRequirePermColorCodes = false;


    // Team name
    @YamlKey("team.name.min-length")
    @Getter
    private int teamNameMinLength = 4;

    @YamlKey("team.name.max-length")
    @Getter
    private int teamNameMaxLength = 10;


    // Team join
    @YamlComment("Do you want a message to be sent to team players when a player joins a team? [Default value: true]")
    @YamlKey("team.join.announce")
    private boolean teamJoinAnnounce = true;

    @YamlComment("Do you want a message to be sent to team players when a player leaves a team? [Default value: true]")
    @YamlKey("team.leave.announce")
    private boolean teamLeftAnnounce = true;


    // Team Size
    @YamlComment("Set the default maximum amount of members that can join a players' team. [Default value: 8]")
    @YamlKey("team.size.default-max-team-size")
    private int maxTeamSize = 8;

    @YamlComment("If true, the default members limit will be stacked with permission limit.\n If a player has ultimateteams.max_members.2 and the default limit is 8, he will be able to have 10 members. otherwise only 2.")
    @YamlKey("team.size.stack-members")
    private boolean stackTeamSizeWithPermission = false;


    // Team Home
    @YamlComment("Enable the '/team [sethome|home]' system. [Default value: true]")
    @YamlKey("team.home.enabled")
    private boolean teamHomeEnabled = true;

    @YamlComment("Define the delay (cooldown) in seconds before the tp starts.\nThis value has no effect if using HuskHomes as teleport handler")
    @YamlKey("team.home.tp-delay")
    private int teamHomeTpDelay = 3;

    @YamlComment("Enable the cool down on the '/team warp <name>' command to prevent tp spamming (RECOMMENDED). [Default value: true]")
    @YamlKey("team.home.cool-down.enabled")
    private boolean teamHomeCooldownEnabled = true;

    @YamlComment("Cool-down time in seconds. [Default value: 120 = 2 minutes]")
    @YamlKey("team.home.cool-down.time")
    private int teamHomeCooldownValue = 120;


    // Team PvP
    @YamlComment("Globally enable the team friendly fire system (the friendly fire is disabled by default in new teams). [Default value: true]")
    @YamlKey("team.pvp.enabled")
    private boolean pvpCommandEnabled = true;

    @YamlComment("Enable the ability for a player to bypass the pvp protection using 'ultimateteams.bypass.pvp'. [Default value: true]")
    @YamlKey("team.pvp.bypass-permission")
    private boolean pvpCommandBypassPerm = true;

    @YamlComment("If this is enabled, new teams will have PvP (friendly fire) enabled by default. [Default value: false]")
    @YamlKey("team.pvp.default-allow-pvp")
    @Getter
    private boolean pvpDefaultAllow = false;


    // Team Warp
    @YamlComment("Enable the '/team [setwarp|warp]' system. [Default value: true]")
    @YamlKey("team.warp.enable")
    private boolean teamWarpEnable = true;

    @YamlComment("Define the delay (cooldown) in seconds before the tp starts.\nThis value has no effect if using HuskHomes as teleport handler")
    @YamlKey("team.warp.tp-delay")
    private int teamWarpTpDelay = 3;

    @YamlComment("Decide how many warps a team owner can set.\n Can be overwritten by ultimateteams.max_warps.<number>")
    @YamlKey("team.warp.limit")
    private int teamWarpLimit = 2;

    @YamlComment("If true, the default warp limit will be stacked with permission limit.\n If a player has ultimateteams.max_warps.3 and the default limit is 2, he will be able to set 5 warps.")
    @YamlKey("team.warp.stack-warps")
    private boolean stackWarpLimitWithPermission = false;

    @YamlComment("Enable the cool down on the '/team warp <name>' command to prevent tp spamming (RECOMMENDED). [Default value: true]")
    @YamlKey("team.warp.cool-down.enabled")
    private boolean teamWarpCooldownEnabled = true;

    @YamlComment("Cool-down time in seconds. [Default value: 120 = 2 minutes]")
    @YamlKey("team.warp.cool-down.time")
    private int teamWarpCooldownValue = 120;


    // Team Chat
    @YamlComment("Enable the team chat system. [Default value: true]")
    @YamlKey("team.chat.enabled")
    private boolean teamChatEnabled = true;

    @YamlComment("Below is the prefix for the team chat messages. (Placeholders: %TEAM% %PLAYER%)")
    @YamlKey("team.chat.prefix")
    private String teamChatPrefix = "&6[&3TC&6]&r";


    // Team Allies
    @YamlComment("Set the maximum amount of allied teams that can a team can have. [Default value: 4]")
    @YamlKey("team.allies.max-allies")
    private int maxAllies = 4;

    // Ally Chat
    @YamlComment("Enable the team ally chat system. [Default value: true]")
    @YamlKey("team.allies.chat.enabled")
    private boolean teamAllyChatEnabled = true;

    @YamlComment("Below is the prefix for the team ally chat messages. (Placeholders: %TEAM% %PLAYER%)")
    @YamlKey("team.allies.chat.prefix")
    private String teamAllyChatPrefix = "&6[&eAC&6]&r";


    // Team Enemies
    @YamlComment("Set the maximum amount of enemies teams that can a team can have. [Default value: 2")
    @YamlKey("team.enemies.max-enemies")
    private int maxEnemies = 2;


    // Team Tags
    @YamlComment("Whether to allow color codes (& and #) in the teams tag")
    @YamlKey("team.tag.allow-color-codes")
    @Getter
    private boolean teamTagAllowColorCodes = true;

    @YamlComment("If enabled, players need the ultimateteams.team.tag.usecolors permission to use color codes")
    @YamlKey("team.tag.require-perm-for-color-codes")
    @Getter
    private boolean teamTagRequirePermColorCodes = false;

    @YamlComment("Set the minimum length of the team prefix. [Default value: 3]")
    @YamlKey("team.tag.min-length")
    private int minCharacterLimit = 3;

    @YamlComment("Set the minimum length of the team prefix. [Default value: 8]")
    @YamlKey("team.tag.max-length")
    private int maxCharacterLimit = 8;

    @YamlComment("Set below names that are not allowed to be used in prefixes or names. [They are NOT casesensitive]")
    @YamlKey("team.tag.disallowed-tags")
    private List<String> disallowedTags = List.of("Gamers", "Rise", "Up");

    @YamlComment("Add a space after the team prefix in chat. [Default value: false].")
    @YamlKey("team.tag.prefix-add-space-after")
    private boolean addPrefixChatAfter = false;

    @YamlComment("Add `[]` characters before and after the team prefix in the chat. [Default value: false]")
    @YamlKey("team.tag.prefix-add-brackets")
    private boolean addPrefixBrackets = false;

    @YamlComment("Below is how the above brackets should appear.")
    @YamlKey("team.tag.brackets-opening")
    private String bracketsOpening = "&f[";

    @YamlKey("team.tag.brackets-closing")
    private String bracketsClosing = "&f]";


    // Team Echest
    @YamlComment("Enable the team enderchest system. [Default value: true]\nThis is not compatible with cross-server (yet).")
    @YamlKey("team.echest.enabled")
    @Getter
    private boolean teamEnderChestEnabled = true;

    @YamlComment("How many rows will the default enderchest have? [Default value: 3]\nValue can go from 1 to 6, being 3 a normal chest and 6 a double chest")
    @YamlKey("team.echest.enabled")
    @Getter
    private int teamEnderChestRows = 3;


    // Chat Spy
    @YamlComment("Do you want players with the perm 'ultimateteams.chat.spy' be able to spy on all team chat messages? [Default value: true]")
    @YamlKey("chat.chat-spy.enabled")
    private boolean teamChatSpyEnabled = true;


    @YamlComment("Below is the prefix for th chat spy messages. [Default value: &6[&cSPY&6]&r]")
    @YamlKey("chat.chat-spy.prefix")
    private String teamChatSpyPrefix = "&6[&cSPY&6]&r";


    // Economy
    @YamlComment("Whether to enable economy (Vault is required)")
    @YamlKey("economy.enable")
    @Getter
    private boolean economyEnabled = false;

    @YamlComment("The cost to create a team")
    @YamlKey("economy.team-create")
    @Getter
    private double teamCreateCost = 100.0;


    // Update Checker
    @YamlComment("Do you want to enable in game plugin update notifications? (Permission:'ultimateteams.update'). [Default value: true]")
    @YamlKey("plugin-update-notifications.enabled")
    private boolean checkForUpdates = true;


    // General Settings
    @YamlComment("Do you want to enable the plugins ability to auto-wipe the invites list? [Default value: true]")
    @YamlKey("general.run-auto-invite-wipe-task")
    private boolean autoInviteWipeTask = true;

    @YamlComment("Do you want the plugin to send a message in console when it does the auto-wipe of the invites list? [Default value: true]")
    @YamlKey("general.run-auto-invite-wipe-task-log")
    private boolean autoInviteWipeTaskLog = true;

    @YamlComment("Do you want to see a lot of debug messages in console when most actions are performed? [Default value: false]")
    @YamlKey("general.developer-debug-mode")
    private boolean debugMode = false;


    private Settings() {
    }

    public boolean doCheckForUpdates() {
        return checkForUpdates;
    }

    public boolean useGlobalGui() {
        return useGlobalGui;
    }

    public Database.Type getDatabaseType() {
        return databaseType;
    }

    public String getMySqlHost() {
        return mySqlHost;
    }

    public int getMySqlPort() {
        return mySqlPort;
    }

    public String getMySqlDatabase() {
        return mySqlDatabase;
    }

    public String getMySqlUsername() {
        return mySqlUsername;
    }

    public String getMySqlPassword() {
        return mySqlPassword;
    }

    public String getMySqlConnectionParameters() {
        return mySqlConnectionParameters;
    }

    public int getMySqlConnectionPoolSize() {
        return mySqlConnectionPoolSize;
    }

    public int getMySqlConnectionPoolIdle() {
        return mySqlConnectionPoolIdle;
    }

    public long getMySqlConnectionPoolLifetime() {
        return mySqlConnectionPoolLifetime;
    }

    public long getMySqlConnectionPoolKeepAlive() {
        return mySqlConnectionPoolKeepAlive;
    }

    public long getMySqlConnectionPoolTimeout() {
        return mySqlConnectionPoolTimeout;
    }

    public Map<String, String> getTableNames() {
        return tableNames;
    }

    @NotNull
    public String getTableName(@NotNull Database.Table table) {
        return Optional.ofNullable(getTableNames().get(table.name().toLowerCase())).orElse(table.getDefaultName());
    }

    public String getNotInTeamPlaceholder() {
        return notInTeamPlaceholder;
    }

    public boolean LuckPermsHook() {
        return luckpermshook;
    }

    public boolean HuskHomesHook() {
        return useHuskhomes;
    }

    public boolean FloodGateHook() {
        return floodgateHook;
    }

    public int getTeamTagsMinCharLimit() {
        return minCharacterLimit;
    }

    public int getTeamTagsMaxCharLimit() {
        return maxCharacterLimit;
    }

    public boolean isTagsBanned(@NotNull String tag) {
        return disallowedTags.stream()
                .anyMatch(name -> name.equalsIgnoreCase(tag));
    }

    public List<String> getTeamTagsDisallowedList() {
        return disallowedTags;
    }

    public boolean addSpaceAfterPrefix() {
        return addPrefixChatAfter;
    }

    public boolean addPrefixBrackets() {
        return addPrefixBrackets;
    }

    public String getPrefixBracketsOpening() {
        return bracketsOpening;
    }

    public String getPrefixBracketsClosing() {
        return bracketsClosing;
    }

    public int getTeamMaxSize() {
        return maxTeamSize;
    }

    public boolean getStackedTeamSize() {
        return stackTeamSizeWithPermission;
    }

    public boolean teamJoinAnnounce() {
        return teamJoinAnnounce;
    }

    public boolean teamLeftAnnounce() {
        return teamLeftAnnounce;
    }

    public boolean teamWarpEnabled() {
        return teamWarpEnable;
    }

    public int getTeamWarpTpDelay() {
        return teamWarpTpDelay;
    }

    public int getTeamWarpLimit() {
        return teamWarpLimit;
    }

    public boolean getTeamWarpStackEnabled() {
        return stackWarpLimitWithPermission;
    }

    public boolean teamWarpCooldownEnabled() {
        return teamWarpCooldownEnabled;
    }

    public int getTeamWarpCooldownValue() {
        return teamWarpCooldownValue;
    }

    public boolean teamChatEnabled() {
        return teamChatEnabled;
    }

    public String getTeamChatPrefix() {
        return teamChatPrefix;
    }

    public boolean teamAllyChatEnabled() {
        return teamAllyChatEnabled;
    }

    public String getTeamAllyChatPrefix() {
        return teamAllyChatPrefix;
    }

    public boolean teamChatSpyEnabled() {
        return teamChatSpyEnabled;
    }

    public String getTeamChatSpyPrefix() {
        return teamChatSpyPrefix;
    }

    public int getMaxTeamAllies() {
        return maxAllies;
    }

    public int getMaxTeamEnemies() {
        return maxEnemies;
    }

    public boolean isPvpCommandEnabled() {
        return pvpCommandEnabled;
    }

    public boolean enablePvPBypassPermission() {
        return pvpCommandBypassPerm;
    }

    public boolean teamHomeEnabled() {
        return teamHomeEnabled;
    }

    public int getTeamHomeTpDelay() {
        return teamHomeTpDelay;
    }

    public boolean teamHomeCooldownEnabled() {
        return teamHomeCooldownEnabled;
    }

    public int getTeamHomeCooldownValue() {
        return teamHomeCooldownValue;
    }

    public boolean enableAutoInviteWipe() {
        return autoInviteWipeTask;
    }

    public boolean enableAutoInviteWipeLog() {
        return autoInviteWipeTaskLog;
    }

    public boolean debugModeEnabled() {
        return debugMode;
    }

}