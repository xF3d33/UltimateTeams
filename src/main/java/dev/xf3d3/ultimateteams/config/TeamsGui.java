package dev.xf3d3.ultimateteams.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamsGui {

    public static final String GUI_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃     UltimateTeams Config     ┃
            ┃      Developed by xF3d3      ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┃
            ┗╸ Information: https://modrinth.com/plugin/ultimate-teams""";

    private GuiSettings gui = new GuiSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuiSettings {
        @Comment("The name and material of the previous page icon")
        private ButtonSettings previousPage = new ButtonSettings("&r&7Go to previous page (%prevpage%)", Material.ARROW);

        @Comment("The name and material of the next page icon")
        private ButtonSettings nextPage = new ButtonSettings("&r&7Go to next page (%nextpage%)", Material.ARROW);

        @Comment("The name and material of the back button")
        private ButtonSettings backButton = new ButtonSettings("&r&7Go Back", Material.ENDER_EYE);

        @Comment("The name and material of the close button")
        private ButtonSettings closeButton = new ButtonSettings("&r&7Close", Material.BARRIER);
    }

    private NotInTeamSettings notInTeam = new NotInTeamSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NotInTeamSettings {

        @Comment({"Modify this to change the slot of the items. each character is an item.",
                "You can move them as you want as long as you don't modify the structure/size of the lines."})
        private List<String> layout = List.of(
                "         ",
                "   + i   ",
                "         "
        );

        @Comment("what name would you like the not in team gui to have?")
        private String name = "&3You are not in a team";

        @Comment("Create team item material (according to the server's version) and lore")
        private MenuItemSettings createTeam = new MenuItemSettings(
                Material.GREEN_BANNER,
                List.of(
                        "&a&lCREATE A TEAM",
                        " ",
                        "&r&8&lLEFT CLICK &r&7to create a team"
                )
        );

        @Comment("Team list button material and lore")
        private MenuItemSettings teamListButton = new MenuItemSettings(
                Material.CREEPER_BANNER_PATTERN,
                List.of(
                        "&3&lTEAMS LIST",
                        " ",
                        "&r&8&lLEFT CLICK &r&7to open the teams list"
                )
        );
    }

    private TeamListSettings teamList = new TeamListSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeamListSettings {

        @Comment("What name would you like the gui to have?")
        private String name = "&3Teams List";

        @Comment({"Modify this to change the slot of the items.",
                "each character is an item, and the 'g' are the slots for the teams.",
                "You can move them as you want as long as you don't modify the structure/size of the lines."})
        private List<String> layout = List.of(
                "         ",
                "  ggggg  ",
                "  ggggg  ",
                "  ggggg  ",
                "         ",
                "f   c   n"
        );

        private IconsSettings icons = new IconsSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class IconsSettings {
            @Comment("This allows you to customize the lore text for the player head icons in the TeamList GUI")
            private Map<String, String> lore = Map.ofEntries(
                    Map.entry("header", "&7----------"),
                    Map.entry("team-name", "&3Team Name: &r"),
                    Map.entry("owner-online", "&3Team Owner: &d"),
                    Map.entry("owner-offline", "&3Team Owner &7&o(Offline)&3: &d"),
                    Map.entry("members", "&3Team Members:"),
                    Map.entry("members-color", "&d"),
                    Map.entry("managers", "&3Team Managers:"),
                    Map.entry("managers-color", "&d"),
                    Map.entry("allies", "&3Team Allies:"),
                    Map.entry("allies-color", "&d"),
                    Map.entry("enemies", "&3Team Enemies:"),
                    Map.entry("enemies-color", "&d"),
                    Map.entry("prefix", "&3Team Prefix: &r"),
                    Map.entry("home", "&3Team Home: &r"),
                    Map.entry("pvp", "&3Team PvP: &r"),
                    Map.entry("footer-1", "&f"),
                    Map.entry("action", "&f"),
                    Map.entry("footer-2", "&7----------")
            );
        }
    }

    private TeamManagerSettings teamManager = new TeamManagerSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeamManagerSettings {

        @Comment("What name would you like the gui to have?")
        private String name = "&3&lTeam Manager";

        @Comment({"Modify this to change the slot of the items. each character is an item.",
                "You can move them as you want as long as you don't modify the structure/size of the lines."})
        private List<String> layout = List.of(
                "         ",
                "    x    ",
                "  abcde  ",
                "    h    ",
                "         ",
                "    y    "
        );

        private InfoSettings info = new InfoSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class InfoSettings {
            @Comment({"Team info item material (according to the server's version).",
                    "The displayed information follows the pattern of team-list.icons.lore"})
            private Material material = Material.LIGHT_BLUE_BANNER;
        }

        private HomeSettings home = new HomeSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class HomeSettings {
            @Comment("Home item material (according to the server's version)")
            private Material material = Material.COMPASS;

            @Comment("Home item name and lore (you can add more lines)")
            private List<String> text = List.of(
                    "&6&lTEAM HOME",
                    " ",
                    "&r&7Home set: &e&l%HOMESET%",
                    " ",
                    "&r&7If your team has a home,",
                    "&r&8&lLEFT CLICK &r&7to TP to the team home",
                    "&r&8&lRIGHT CLICK &r&7to delete the team home (you must be the team owner)"
            );
        }

        private WarpsSettings warps = new WarpsSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class WarpsSettings {
            @Comment({"Modify this to change the slot of the items.",
                    "each character is an item, and the 'g' are the slots for the teams.",
                    "You can move them as you want as long as you don't modify the structure/size of the lines."})
            private List<String> layout = List.of(
                    "         ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "         ",
                    "f   b   n"
            );

            @Comment("Warps item material (according to the server's version)")
            private Material material = Material.ENDER_PEARL;

            @Comment("Warps item name and lore (you can add more lines)")
            private List<String> text = List.of(
                    "&c&lTEAM WARPS",
                    " ",
                    "&r&8&lLEFT CLICK &r&7to open warps manager"
            );

            private ManagerSettings manager = new ManagerSettings(
                    "&3&lWarps Manager",
                    List.of(
                            "&c&l%WARP%",
                            " ",
                            "&r&8&lLEFT CLICK &r&7to TP to the warp position",
                            "&r&8&lRIGHT CLICK &r&7to delete the warp"
                    )
            );
        }

        private MembersSettings members = new MembersSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class MembersSettings {
            @Comment({"Modify this to change the slot of the items.",
                    "each character is an item, and the 'g' are the slots for the teams.",
                    "You can move them as you want as long as you don't modify the structure/size of the lines."})
            private List<String> layout = List.of(
                    "         ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "         ",
                    "f   b   n"
            );

            @Comment("Members item material (according to the server's version)")
            private Material material = Material.PLAYER_HEAD;

            @Comment("Members item name and lore (you can add more lines)")
            private List<String> text = List.of(
                    "&e&lTEAM MEMBERS",
                    " ",
                    "&r&8&lLEFT CLICK &r&7to open members manager"
            );

            private ManagerSettings manager = new ManagerSettings(
                    "&3&lMembers Manager",
                    List.of(
                            "&e&l%NAME%",
                            " ",
                            "&r&8&lRIGHT CLICK &r&7to kick the player"
                    )
            );
        }

        private PvpSettings pvp = new PvpSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PvpSettings {
            @Comment("PvP item material (according to the server's version)")
            private Material material = Material.DIAMOND_SWORD;

            @Comment("PvP item name and lore (you can add more lines)")
            private List<String> text = List.of(
                    "&B&lTEAM PvP",
                    " ",
                    "&r&7enabled: &a&l%ENABLED%",
                    " ",
                    "&r&8&lLEFT CLICK &r&7to enable/disable team PvP"
            );
        }

        private AlliesSettings allies = new AlliesSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class AlliesSettings {
            @Comment({"Modify this to change the slot of the items.",
                    "each character is an item, and the 'g' are the slots for the teams.",
                    "You can move them as you want as long as you don't modify the structure/size of the lines."})
            private String[] layout = {
                    "         ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "         ",
                    "f   b   n"
            };

            @Comment("Allies item material (according to the server's version)")
            private Material material = Material.TOTEM_OF_UNDYING;

            @Comment("Allies item name and lore (you can add more lines)")
            private List<String> text = List.of(
                    "&a&lTEAM ALLIES",
                    " ",
                    "&r&8&lLEFT CLICK &r&7to open allies manager"
            );

            private ManagerSettings manager = new ManagerSettings(
                    "&3&lAllies Manager",
                    List.of(
                            "&e&l%NAME%",
                            " ",
                            "&r&8&lRIGHT CLICK &r&7to remove the team from your allies"
                    )
            );
        }

        private EnemiesSettings enemies = new EnemiesSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EnemiesSettings {
            @Comment({"Modify this to change the slot of the items.",
                    "each character is an item, and the 'g' are the slots for the teams.",
                    "You can move them as you want as long as you don't modify the structure/size of the lines."})
            private List<String> layout = List.of(
                    "         ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "  ggggg  ",
                    "         ",
                    "f   b   n"
            );

            @Comment("Enemies item material (according to the server's version)")
            private Material material = Material.NETHERITE_SWORD;

            @Comment("Enemies item name and lore (you can add more lines)")
            private List<String> text = List.of(
                    "&4&lTEAM ENEMIES",
                    " ",
                    "&r&8&lLEFT CLICK &r&7to open enemies manager"
            );

            private ManagerSettings manager = new ManagerSettings(
                    "&3&lEnemies Manager",
                    List.of(
                            "&e&l%NAME%",
                            " ",
                            "&r&8&lRIGHT CLICK &r&7to delete the team from your enemies (you must be the team owner)"
                    )
            );
        }
    }

    // --- Common reusable sub-settings below --- //

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ButtonSettings {
        private String name;
        private Material material;

        public ButtonSettings(String name, Material material) {
            this.name = name;
            this.material = material;
        }
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MenuItemSettings {
        private Material material;
        private List<String> text;

        public MenuItemSettings(Material material, List<String> text) {
            this.material = material;
            this.text = text;
        }
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ManagerSettings {
        @Comment("Manager GUI name")
        private String name;

        @Comment("Manager item name and lore (you can add more lines)")
        private List<String> text;

        public ManagerSettings(String name, List<String> text) {
            this.name = name;
            this.text = text;
        }
    }
}