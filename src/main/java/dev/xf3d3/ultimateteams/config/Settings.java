package dev.xf3d3.ultimateteams.config;

import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import dev.xf3d3.ultimateteams.database.Database;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    @YamlComment("Type of database to use (MYSQL, SQLITE). MYSQL is preferred over SQLITE.")
    @YamlKey("database.type")
    private Database.Type databaseType = Database.Type.SQLITE;

    @YamlComment("Specify credentials here if you are using MYSQL as your database type")
    @YamlKey("database.mysql.credentials.host")
    private String mySqlHost = "localhost";

    @YamlKey("database.mysql.port")
    private int mySqlPort = 3306;

    @YamlKey("database.mysql.database")
    private String mySqlDatabase = "ultimate_teams";

    @YamlKey("database.mysql.username")
    private String mySqlUsername = "root";

    @YamlKey("database.mysql.password")
    private String mySqlPassword = "pa55w0rd";

    @YamlKey("database.mysql.parameters")
    private String mySqlConnectionParameters = "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8";

    @YamlComment("MYSQL database Hikari connection pool properties. Don't modify this unless you know what you're doing!")
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

    @YamlComment("use HuskHomes to teleport players instead of built-in teleport handler [Default value: true]")
    @YamlKey("use-huskhomes")
    private boolean useHuskhomes = false;

    @YamlComment("Hook into floodgate to handle bedrock players properly [Default value: true]. Needs FloodGate")
    @YamlKey("floodgate-hook")
    private boolean floodgateHook = false;

    // Team Tags
    @YamlComment("Set the minimum length of the team prefix and name. [Default value: 3]")
    @YamlKey("team-tags.min-character-limit")
    private int minCharacterLimit = 3;

    @YamlComment("Set the minimum length of the team prefix and name. [Default value: 32]")
    @YamlKey("team-tags.max-character-limit")
    private int maxCharacterLimit = 32;

    @YamlComment("Set below names that are not allowed to be used in prefixes or names. [They ARE case & syntax sensitive]")
    @YamlKey("team-tags.disallowed-tags")
    private List<String> disallowedTags = List.of("Gamers", "Rise", "Up");

    @YamlComment("Add a space after the team prefix in chat. [Default value: false].")
    @YamlKey("team-tags.prefix-add-space-after")
    private boolean addPrefixChatAfter = false;

    @YamlComment("Add `[]` characters before and after the team prefix in the chat. [Default value: false]")
    @YamlKey("team-tags.prefix-add-brackets")
    private boolean addPrefixBrackets = false;

    @YamlComment("Below is how the above brackets should appear.")
    @YamlKey("team-tags.brackets-opening")
    private String bracketsOpening = "&f[";

    @YamlKey("team-tags.brackets-closing")
    private String bracketsClosing = "&f]";

    // Team Size
    // todo: should add team tiered?
    @YamlComment("Set the default maximum amount of members that can join a players' team. [Default value: 8]")
    @YamlKey("team-size.default-max-team-size")
    private int maxTeamSize = 8;

    // Team join
    @YamlComment("Do you want a message to be sent to team players when a player joins a team? [Default value: true]")
    @YamlKey("team-join.announce")
    private boolean teamJoinAnnounce = true;

    // Team Warp
    @YamlComment("Enable the '/team [setwarp|warp]' system. [Default value: true]")
    @YamlKey("team-warp.enable")
    private boolean teamWarpEnable = true;

    @YamlComment("Define the delay (cooldown) in seconds before the tp starts")
    @YamlKey("team-warp.tp-delay")
    private int teamWarpTpDelay = 3;

    @YamlComment("Decide how many warps a team can have")
    @YamlKey("team-warp.limit")
    private int teamWarpLimit = 2;

    @YamlComment("Enable the cool down on the '/team warp <name>' command to prevent tp spamming (RECOMMENDED). [Default value: true]")
    @YamlKey("team-warp.cool-down.enabled")
    private boolean teamWarpCooldownEnabled = true;

    @YamlComment("Cool-down time in seconds. [Default value: 120 = 2 minutes]")
    @YamlKey("team-warp.cool-down.time")
    private int teamWarpCooldownValue = 120;

    // Team Chat
    @YamlComment("Enable the team chat system. [Default value: true]")
    @YamlKey("team-chat.enabled")
    private boolean teamChatEnabled = true;

    @YamlComment("Below is the prefix for the team chat messages. [Default value: &6[&3CC&6]&r]")
    @YamlKey("team-chat.prefix")
    private String teamChatPrefix = "&6[&3TC&6]&r";

    // Ally Chat
    @YamlComment("Enable the team ally chat system. [Default value: true]")
    @YamlKey("ally-chat.enabled")
    private boolean teamAllyChatEnabled = true;

    @YamlComment("Below is the prefix for the team ally chat messages. [Default value: &6[&3CC&6]&r]")
    @YamlKey("ally-chat.prefix")
    private String teamAllyChatPrefix = "&6[&eAC&6]&r";

    // Chat Spy
    @YamlComment("Do you want players with the perm 'ultimateteams.chat.spy' be able to spy on all team chat messages? [Default value: true]")
    @YamlKey("chat-spy.enabled")
    private boolean teamChatSpyEnabled = true;


    @YamlComment("Below is the prefix for th chat spy messages. [Default value: &6[&cSPY&6]&r]")
    @YamlKey("chat-spy.prefix")
    private String teamChatSpyPrefix = "&6[&cSPY&6]&r";

    // Team Allies
    @YamlComment("Set the maximum amount of allied teams that can a team can have. [Default value: 4]")
    @YamlKey("max-team-allies")
    private int maxAllies = 4;

    // Team Enemies
    @YamlComment("Set the maximum amount of enemies teams that can a team can have. [Default value: 2")
    @YamlKey("max-team-enemies")
    private int maxEnemies = 2;

    // Team PvP
    @YamlComment("Globally enable the team friendly fire system (the friendly fire is disabled by default in new teams). [Default value: true]")
    @YamlKey("pvp.pvp-command")
    private boolean pvpCommandEnabled = true;

    @YamlComment("Enable the ability for a player to bypass the pvp protection using 'ultimateteams.bypass.pvp'. [Default value: true]")
    @YamlKey("pvp.pvp-command-bypass-permission")
    private boolean pvpCommandBypassPerm = true;

    // Team Home
    @YamlComment("Enable the '/team [sethome|home]' system. [Default value: true]")
    @YamlKey("team-home.enabled")
    private boolean teamHomeEnabled = true;

    @YamlComment("Define the delay (cooldown) in seconds before the tp starts")
    @YamlKey("team-home.tp-delay")
    private int teamHomeTpDelay = 3;

    @YamlComment("Enable the cool down on the '/team warp <name>' command to prevent tp spamming (RECOMMENDED). [Default value: true]")
    @YamlKey("team-home.cool-down.enabled")
    private boolean teamHomeCooldownEnabled = true;

    @YamlComment("Cool-down time in seconds. [Default value: 120 = 2 minutes]")
    @YamlKey("team-home.cool-down.time")
    private int teamHomeCooldownValue = 120;

    // Update Checker
    @YamlComment("Do you want to enable in game plugin update notifications? (Permission:'ultimateteams.update'). [Default value: true]")
    @YamlKey("plugin-update-notifications.enabled")
    private boolean checkForUpdates = true;

    // General Settings
    @YamlComment("Do you want to enable the plugins ability to auto-wipe the invites list? [Default value: true]")
    @YamlKey("general.run-auto-invite-wipe-task")
    private boolean autoInviteWipeTask = true;

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

    public boolean teamJoinAnnounce() {
        return teamJoinAnnounce;
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

    public boolean TeamWarpCooldownEnabled() {
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

    public boolean debugModeEnabled() {
        return debugMode;
    }

}