package dev.xf3d3.ultimateteams.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.network.Broker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Plugin settings, read from config.yml
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Settings {

    public static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃     UltimateTeams Config     ┃
            ┃      Developed by xF3d3      ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┃
            ┗╸ Information: https://modrinth.com/plugin/ultimate-teams""";

    @Comment("Hook into luckperms to create contexts (e.g. is-in-team) [Default value: false]. Needs LuckPerms")
    private boolean luckpermsHook = false;

    @Comment("use HuskHomes to teleport players instead of built-in teleport handler [Default value: false]")
    private boolean useHuskhomes = false;

    @Comment("Hook into floodgate to handle bedrock players properly [Default value: false]. Needs FloodGate")
    private boolean floodgateHook = false;

    @Comment("Enable the Apollo Team View integration for Lunar Client players. Teammates will appear as markers on the minimap and direction HUD. [Default value: true]")
    private boolean apolloHook = true;

    @Comment("GUI Settings")
    private GuiSettings gui = new GuiSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuiSettings {
        @Comment("Do you want to use the GUI system? [Default value: true]")
        private boolean enable = true;

        @Comment("Should the plugin open the team list GUI instead of sending a chat message when /team list is used? [Default value: false]")
        private boolean useGuiForTeamList = false;
    }

    @Comment("Database Settings")
    private DatabaseSettings database = new DatabaseSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DatabaseSettings {

        @Comment("Type of database to use (SQLITE, H2, MYSQL, MARIADB, or POSTGRESQL). MARIADB is preferred over MYSQL. H2 is preferred over SQLITE")
        private Database.Type type = Database.Type.SQLITE;

        private MysqlSettings mysql = new MysqlSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class MysqlSettings {

            @Comment("Specify credentials here if you are using MYSQL, MARIADB or POSTGRESQL as database type")
            private Credentials credentials = new Credentials();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class Credentials {
                private String host = "localhost";
                private int port = 3306;
                private String database = "ultimate_teams";
                private String username = "root";
                private String password = "pa55w0rd";
                private String parameters = "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8";
            }

            @Comment("MYSQL / MARIADB / POSTGRESQL database connection pool properties. Don't modify this unless you know what you're doing!")
            private ConnectionPool connectionPool = new ConnectionPool();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class ConnectionPool {
                private int size = 12;
                private int idle = 12;
                private long lifetime = 1800000;
                private long keepalive = 30000;
                private long timeout = 20000;
            }
        }

        @Comment("Names of tables to use on your database. Don't modify this unless you know what you're doing!")
        @Getter(AccessLevel.NONE)
        private Map<String, String> tableNames = Map.of(
                Database.Table.TEAM_DATA.name().toLowerCase(Locale.ENGLISH), Database.Table.TEAM_DATA.getDefaultName(),
                Database.Table.USER_DATA.name().toLowerCase(Locale.ENGLISH), Database.Table.USER_DATA.getDefaultName()
        );

        @NotNull
        public String getTableName(@NotNull Database.Table table) {
            return Optional.ofNullable(tableNames.get(table.name().toLowerCase(Locale.ENGLISH))).orElse(table.getDefaultName());
        }
    }

    @Comment("Cross-Server Settings")
    private CrossServerSettings crossServer = new CrossServerSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CrossServerSettings {

        @Comment("Whether enable cross-server mode. You must use MYSQL/MARIADB/POSTGRESQL to use cross-server")
        private boolean enable = false;

        @Comment("The name of the server as it appears on Bungee/Velocity (case-sensitive)")
        private String serverName = "Survival";

        @Comment("The cluster ID, for if you're networking multiple separate groups of UltimateTeams-enabled servers. Do not change unless you know what you're doing")
        private String clusterId = "main";

        @Comment("Type of network message broker to ues for data synchronization (PLUGIN_MESSAGE or REDIS). Always use REDIS if possible.")
        private Broker.Type broker = Broker.Type.PLUGIN_MESSAGE;

        @Comment("Settings for if you're using REDIS as your message broker")
        private RedisSettings redis = new RedisSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class RedisSettings {
            private String host = "localhost";
            private int port = 6379;
            private String password = "";
            private boolean useSsl = false;
        }
    }

    @Comment("Placeholder Settings")
    private PlaceholderSettings placeholder = new PlaceholderSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaceholderSettings {
        @Comment("When the placeholder would be blank, the text below will be shown instead (you can use color codes)")
        private String notInATeam = "Not in a team";
    }

    @Comment("Team Settings")
    private TeamSettings team = new TeamSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeamSettings {

        @Comment("Should the plugin cancel teleports if the player moves or is damaged during the tp delay? [Changes require restart]")
        private boolean cancelTp = true;

        private NameSettings name = new NameSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class NameSettings {
            @Comment("Whether to allow color codes (& and #) in the team name")
            private boolean allowColorCodes = false;

            @Comment("If enabled, players will need the ultimateteams.team.create.usecolors permission to use color codes")
            private boolean requirePermForColorCodes = false;

            private int minLength = 4;
            private int maxLength = 10;

            private RegexSettings regex = new RegexSettings("[a-zA-Z]+");
        }

        private JoinSettings join = new JoinSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class JoinSettings {
            @Comment("Do you want a message to be sent to team players when a player joins a team? [Default value: true]")
            private boolean announce = true;
        }

        private LeaveSettings leave = new LeaveSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class LeaveSettings {
            @Comment("Do you want a message to be sent to team players when a player leaves a team? [Default value: true]")
            private boolean announce = true;
        }

        private SizeSettings size = new SizeSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class SizeSettings {
            @Comment("Set the default maximum amount of members that can join a players' team. [Default value: 8]")
            private int defaultMaxTeamSize = 8;

            @Comment({
                    "If true, the default members limit will be stacked with permission limit.",
                    "If a player has ultimateteams.max_members.2 and the default limit is 8, he will be able to have 10 members. otherwise only 2."
            })
            private boolean stackMembers = false;
        }

        private HomeSettings home = new HomeSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class HomeSettings {
            @Comment("Enable the '/team [sethome|home]' system. [Default value: true]")
            private boolean enabled = true;

            @Comment({
                    "Define the delay (cooldown) in seconds before the tp starts.",
                    "This value has no effect if using HuskHomes as teleport handler"
            })
            private int tpDelay = 3;

            private CooldownSettings coolDown = new CooldownSettings(true, 120);
        }

        private PvpSettings pvp = new PvpSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PvpSettings {
            @Comment("Globally enable the team friendly fire system (the friendly fire is disabled by default in new teams). [Default value: true]")
            private boolean enabled = true;

            @Comment("Enable the ability for a player to bypass the pvp protection using 'ultimateteams.bypass.pvp'. [Default value: true]")
            private boolean bypassPermission = true;

            @Comment("If this is enabled, new teams will have PvP (friendly fire) enabled by default. [Default value: false]")
            private boolean defaultAllowPvp = false;
        }

        private WarpSettings warp = new WarpSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class WarpSettings {
            @Comment("Enable the '/team [setwarp|warp]' system. [Default value: true]")
            private boolean enable = true;

            @Comment({
                    "Define the delay (cooldown) in seconds before the tp starts.",
                    "This value has no effect if using HuskHomes as teleport handler"
            })
            private int tpDelay = 3;

            @Comment({
                    "Decide how many warps a team owner can set.",
                    "Can be overwritten by giving a player the ultimateteams.max_warps.<number> permission"
            })
            private int limit = 2;

            @Comment({
                    "If true, the default warp limit will be stacked with permission limit.",
                    "For example: If a player has ultimateteams.max_warps.3 and the default limit is 2, he will be able to set 5 warps."
            })
            private boolean stackWarps = false;

            private CooldownSettings coolDown = new CooldownSettings(true, 120);
        }

        private ChatSettings chat = new ChatSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ChatSettings {
            @Comment("Enable the team chat system. [Default value: true]")
            private boolean enabled = true;

            @Comment("Below is the prefix for the team chat messages. (you can also use any other PAPI placeholder)")
            private String prefix = "&6[&3TC&6]&r %TEAM% %PLAYER%:";
        }

        private AlliesSettings allies = new AlliesSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class AlliesSettings {
            @Comment("Enable the team ally system. [Default value: true]")
            private boolean enabled = true;

            @Comment("Set the maximum amount of allied teams that can a team can have. [Default value: 4]")
            private int maxAllies = 4;

            private ChatSettings chat = new ChatSettings("&6[&eAC&6]&r");

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class ChatSettings {
                @Comment("Enable the team ally chat system. [Default value: true]")
                private boolean enabled = true;

                @Comment("Below is the prefix for the team ally chat messages. (you can also use any other PAPI placeholder)")
                private String prefix;

                public ChatSettings(String prefix) {
                    this.prefix = prefix;
                }
            }
        }

        private EnemiesSettings enemies = new EnemiesSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EnemiesSettings {
            @Comment("Enable the team enemies system. [Default value: true]")
            private boolean enabled = true;

            @Comment("Set the maximum amount of enemies teams that can a team can have. [Default value: 2")
            private int maxEnemies = 2;
        }

        private PrefixSettings prefix = new PrefixSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PrefixSettings {
            @Comment("Whether to allow color codes (& and #) in the teams prefix")
            private boolean allowColorCodes = true;

            @Comment("If enabled, players need the ultimateteams.team.tag.usecolors permission to use color codes")
            private boolean requirePermForColorCodes = false;

            @Comment("Set the minimum length of the team prefix. [Default value: 3]")
            private int minLength = 3;

            @Comment("Set the maximum length of the team prefix. [Default value: 8]")
            private int maxLength = 8;

            private RegexSettings regex = new RegexSettings("[a-zA-Z]+");

            @Comment("Set below names that are not allowed to be used in prefixes or names. [They are NOT casesensitive]")
            private List<String> disallowedTags = List.of("Gamers", "Rise", "Up");

            @Comment("Add a space after the team prefix in chat. [Default value: false].")
            private boolean prefixAddSpaceAfter = false;

            @Comment("Add `[]` characters before and after the team prefix in the chat. [Default value: false]")
            private boolean prefixAddBrackets = false;

            @Comment("Below is how the above brackets should appear.")
            private String bracketsOpening = "&f[";
            private String bracketsClosing = "&f]";
        }

        private MotdSettings motd = new MotdSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class MotdSettings {
            @Comment("Should players be allowed to set a MOTD for their team?")
            private boolean enable = true;

            @Comment("Should players be be sent the team MOTD when they join the team?")
            private boolean sendOnJoin = true;

            private boolean allowColors = false;

            @Comment("If enabled, players will need the ultimateteams.team.motd.usecolors permission to use color codes")
            private boolean colorsRequirePerm = false;

            private LengthSettings length = new LengthSettings();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class LengthSettings {
                private int min = 5;
                private int max = 30;
            }

            private RegexSettings regex = new RegexSettings("[a-zA-Z]+");
        }

        private EchestSettings echest = new EchestSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EchestSettings {
            @Comment({
                    "Enable the team enderchest system. [Default value: true]",
                    "This is not compatible with cross-server (yet).",
                    "If you are currently on 1.21.4 or below, and you want to update your server remember to migrate the team enderchests (guide in the docs)"
            })
            private boolean enabled = true;

            @Comment({
                    "How many rows will the default enderchest have? [Default value: 3]",
                    "Value can go from 1 to 6, being 3 a normal chest and 6 a double chest"
            })
            private int rows = 3;

            @Comment({
                    "ONLY FOR SERVERS WHO JUST UPDATED TO A VERSION THAT SUPPORTS THIS FEATURE!",
                    "Should the plugin add an enderchest to each team on startup? [Default value: false]",
                    "Since teams created before this version didn't have enderchests, add one.",
                    "",
                    "AFTER A FULL STARTUP DISABLE THIS AND RESTART THE SERVER!"
            })
            private boolean migrate = false;
        }
    }

    @Comment("Chat Spy Settings")
    private GlobalChatSettings chat = new GlobalChatSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GlobalChatSettings {
        private ChatSpySettings chatSpy = new ChatSpySettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ChatSpySettings {
            @Comment("Do you want players with the perm 'ultimateteams.chat.spy' be able to spy on all team chat messages? [Default value: true]")
            private boolean enabled = true;

            @Comment("Below is the prefix for th chat spy messages. [Default value: &6[&cSPY&6]&r]")
            private String prefix = "&6[&cSPY&6]&r";
        }
    }

    @Comment("Economy Settings")
    private EconomySettings economy = new EconomySettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EconomySettings {
        @Comment("Whether to enable economy (Vault is required). Changes might require to restart the server")
        private boolean enable = false;

        private TeamCreateSettings teamCreate = new TeamCreateSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class TeamCreateSettings {
            @Comment("Should players pay to create a team?")
            private boolean enabled = false;

            @Comment("The cost to create a team")
            private double cost = 100.0;
        }

        private TeamBankSettings teamBank = new TeamBankSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class TeamBankSettings {
            @Comment("Whether to enable the team bank")
            private boolean enabled = false;

            @Comment("Whether to enable the team bank limit")
            private boolean bankLimit = false;

            @Comment("The bank limit")
            private double limit = 20000.0;
        }

        private TeamJoinFeeSettings teamJoinFee = new TeamJoinFeeSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class TeamJoinFeeSettings {
            @Comment("Should players be allowed to ask who joins a team a fee (that will be deposited in the team bank)?")
            private boolean enabled = false;

            @Comment("The default cost of the join fee")
            private double defaultValue = 100.0; // Avoid 'default' keyword, mapped to defaultValue

            @Comment("The max cost of the join fee")
            private double maxFee = 10000.0;
        }
    }

    @Comment("Update Checker Notifications")
    private PluginUpdateNotifications pluginUpdateNotifications = new PluginUpdateNotifications();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PluginUpdateNotifications {
        @Comment("Do you want to enable in game plugin update notifications? (Permission:'ultimateteams.update'). [Default value: true]")
        private boolean enabled = true;
    }

    @Comment("General Settings")
    private GeneralSettings general = new GeneralSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GeneralSettings {

        @Comment("Do you want to enable the plugins ability to auto-wipe the invites list? [Default value: true]")
        private boolean runAutoInviteWipeTask = true;

        @Comment("Do you want the plugin to send a message in console when it does the auto-wipe of the invites list? [Default value: true]")
        private boolean runAutoInviteWipeTaskLog = true;

        @Comment("Do you want the plugin to send a message in console when it does the team echests backup? [Default value: true]")
        private boolean echestBackupLog = true;

        @Comment("Do you want to see a lot of debug messages in console when most actions are performed? [Default value: false]")
        private boolean developerDebugMode = false;
    }

    // --- Common reusable sub-settings below --- //

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RegexSettings {
        @Comment("If enabled, it will need to additionally pass the regex in order to be used. Advanced users only.")
        private boolean enable = false;
        private String value;

        public RegexSettings(String value) {
            this.value = value;
        }
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CooldownSettings {
        @Comment("Enable the cool down to prevent spamming (RECOMMENDED).")
        private boolean enabled = true;

        @Comment("Cool-down time in seconds.")
        private int time = 120;

        public CooldownSettings(boolean enabled, int time) {
            this.enabled = enabled;
            this.time = time;
        }
    }
}