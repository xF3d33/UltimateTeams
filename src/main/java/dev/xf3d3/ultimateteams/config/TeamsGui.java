package dev.xf3d3.ultimateteams.config;

import lombok.Getter;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;


@YamlFile(header = """
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃     UltimateTeams Config     ┃
        ┃      Developed by xF3d3      ┃
        ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
        ┃
        ┗╸ Information: https://modrinth.com/plugin/ultimate-teams""")
public class TeamsGui {

    // Previous page
    @YamlComment("The name of the previous page icon")
    @YamlKey("gui.previous-page.name")
    @Getter
    private String previousPage = "&r&7Go to previous page (%prevpage%)";

    @YamlComment("The Material of the previous page icon")
    @YamlKey("gui.previous-page.material")
    @Getter
    private Material previousPageMaterial = Material.ARROW;


    // next page
    @YamlComment("The name of the next page icon")
    @YamlKey("gui.next-page.name")
    @Getter
    private String nextPage = "&r&7Go to next page (%nextpage%)";

    @YamlComment("The Material of the next page icon")
    @YamlKey("gui.next-page.material")
    @Getter
    private Material nextPageMaterial = Material.ARROW;


    // Back
    @YamlComment("The name of the back button icon")
    @YamlKey("gui.back-button.name")
    @Getter
    private String backButtonName = "&r&7Go Back";

    @YamlComment("The Material of the back button")
    @YamlKey("gui.back-button.material")
    @Getter
    private Material backButtonMaterial = Material.ENDER_EYE;

    // Close
    @YamlComment("The name of the close button")
    @YamlKey("gui.close-button.name")
    @Getter
    private String closeButtonName = "&r&7Close";

    @YamlComment("The Material of the close button")
    @YamlKey("gui.close-button.material")
    @Getter
    private Material closeButtonMaterial = Material.BARRIER;

    @YamlComment("Modify this to change the slot of the items.\neach character is an item, and the 'g' are the slots for the teams.\nYou can move them as you want as long as you don't modify the structure/size of the lines.")
    @YamlKey("team-list.layout")
    @Getter
    private List<String> teamsListguiSetup = List.of(
            "         ",
            "  ggggg  ",
            "  ggggg  ",
            "  ggggg  ",
            "         ",
            "f   c   n"
    );


    // GUI NAME
    @YamlComment("What name would you like the gui to have?")
    @YamlKey("team-list.name")
    private String teamsListGuiName = "&3Teams List";

    @YamlComment("This allows you to customise the lore text for the player head icons in the TeamList GUI")
    @YamlKey("team-list.icons.lore")
    private Map<String, String> loreMap = Map.ofEntries(
            Map.entry("header", "&7----------"),
            Map.entry("team-name", "&3Team Name: &r"),
            Map.entry("owner-online", "&3Team Owner: &d"),
            Map.entry("owner-offline", "&3Team Owner &7&o(Offline)&3: &d"),
            Map.entry("members", "&3Team Members: &d"),
            Map.entry("managers", "&3Team Managers: &d"),
            Map.entry("allies", "&3Team Allies: &r"),
            Map.entry("enemies", "&3Team Enemies: &r"),
            Map.entry("prefix", "&3Team Prefix: &r"),
            Map.entry("home", "&3Team Home: &r"),
            Map.entry("pvp", "&3Team PvP: &r"),
            Map.entry("footer-1", "&f"),
            Map.entry("action", "&f"),
            Map.entry("footer-2", "&7----------")
    );

    @YamlComment("Modify this to change the slot of the items. each character is an item.\nYou can move them as you want as long as you don't modify the structure/size of the lines.")
    @YamlKey("team-manager.layout")
    @Getter
    private List<String> teamsManagerguiSetup = List.of(
            "         ",
            "    x    ",
            "  abcde  ",
            "    h    ",
            "         ",
            "    y    "
    );


    @YamlComment("What name would you like the gui to have?")
    @YamlKey("team-manager.name")
    @Getter
    private String teamsManagerGuiName = "&3&lTeam Manager";

    // INFO
    @YamlComment("Team info item material (according to the server's version).\nThe displayed information follows the pattern of team-list.icons.lore")
    @YamlKey("team-manager.info.material")
    @Getter
    private Material teamsManagerGuiInfoMaterial = Material.LIGHT_BLUE_BANNER;


    // HOME
    @YamlComment("Home item material (according to the server's version)")
    @YamlKey("team-manager.home.material")
    @Getter
    private Material teamsManagerGuiHomeMaterial = Material.COMPASS;

    @YamlComment("Home item name and lore (you can add more lines)")
    @YamlKey("team-manager.home.text")
    @Getter
    private List<String> teamsManagerGuiHomeText = List.of(
            "&6&lTEAM HOME",
            " ",
            "&r&7Home set: &e&l%HOMESET%",
            " ",
            "&r&7If your team has a home,",
            "&r&8&lLEFT CLICK &r&7to TP to the team home",
            "&r&8&lRIGHT CLICK &r&7to delete the team home (you must be the team owner)"
    );

    // WARPS
    @YamlComment("Modify this to change the slot of the items.\neach character is an item, and the 'g' are the slots for the teams.\nYou can move them as you want as long as you don't modify the structure/size of the lines.")
    @YamlKey("team-manager.warps.layout")
    @Getter
    private List<String> teamsManagerWarpsguiSetup = List.of(
            "         ",
            "  ggggg  ",
            "  ggggg  ",
            "  ggggg  ",
            "         ",
            "f   b   n"
    );

    @YamlComment("Warps item material (according to the server's version)")
    @YamlKey("team-manager.warps.material")
    @Getter
    private Material teamsManagerGuiWarpsMaterial = Material.ENDER_PEARL;

    @YamlComment("Warps item name and lore (you can add more lines)")
    @YamlKey("team-manager.warps.text")
    @Getter
    private List<String> teamsManagerGuiWarpsText = List.of(
            "&c&lTEAM WARPS",
            " ",
            "&r&8&lLEFT CLICK &r&7to open warps manager"
    );

    @YamlComment("Warps manager GUI name")
    @YamlKey("team-manager.warps.manager.name")
    @Getter
    private String warpsManagerGuiName = "&3&lWarps Manager";

    @YamlComment("Warps manager item name and lore (you can add more lines)")
    @YamlKey("team-manager.warps.manager.text")
    @Getter
    private List<String> warpsManagerGuiWarpsText = List.of(
            "&c&l%WARP%",
            " ",
            "&r&8&lLEFT CLICK &r&7to TP to the warp position",
            "&r&8&lRIGHT CLICK &r&7to delete the warp (you must be the team owner)"
    );

    // MEMBERS
    @YamlComment("Modify this to change the slot of the items.\neach character is an item, and the 'g' are the slots for the teams.\nYou can move them as you want as long as you don't modify the structure/size of the lines.")
    @YamlKey("team-manager.members.layout")
    @Getter
    private List<String> teamsManagerMembersguiSetup = List.of(
            "         ",
            "  ggggg  ",
            "  ggggg  ",
            "  ggggg  ",
            "         ",
            "f   b   n"
    );

    @YamlComment("Members item material (according to the server's version)")
    @YamlKey("team-manager.members.material")
    @Getter
    private Material teamsManagerGuiMembersMaterial = Material.PLAYER_HEAD;

    @YamlComment("Members item name and lore (you can add more lines)")
    @YamlKey("team-manager.members.text")
    @Getter
    private List<String> teamsManagerGuiMembersText = List.of(
            "&e&lTEAM MEMBERS",
            " ",
            "&r&8&lLEFT CLICK &r&7to open members manager"
    );

    @YamlComment("Members manager GUI name")
    @YamlKey("team-manager.members.manager.name")
    @Getter
    private String membersManagerGuiName = "&3&lMembers Manager";

    @YamlComment("Members manager item name and lore (you can add more lines)")
    @YamlKey("team-manager.members.manager.text")
    @Getter
    private List<String> membersManagerGuiText = List.of(
            "&e&l%NAME%",
            " ",
            "&r&8&lRIGHT CLICK &r&7to kick the player (you must be the team owner)"
    );

    // PVP
    @YamlComment("PvP item material (according to the server's version)")
    @YamlKey("team-manager.pvp.material")
    @Getter
    private Material teamsManagerGuiPvpMaterial = Material.DIAMOND_SWORD;

    @YamlComment("PvP item name and lore (you can add more lines)")
    @YamlKey("team-manager.pvp.text")
    @Getter
    private List<String> teamsManagerGuiPvpText = List.of(
            "&B&lTEAM PvP",
            " ",
            "&r&7enabled: &a&l%ENABLED%",
            " ",
            "&r&8&lLEFT CLICK &r&7to enable/disable team PvP"
    );

    // ALLIES
    @YamlComment("Modify this to change the slot of the items.\neach character is an item, and the 'g' are the slots for the teams.\nYou can move them as you want as long as you don't modify the structure/size of the lines.")
    @YamlKey("team-manager.allies.layout")
    @Getter
    private List<String> teamsManagerAlliesguiSetup = List.of(
            "         ",
            "  ggggg  ",
            "  ggggg  ",
            "  ggggg  ",
            "         ",
            "f   b   n"
    );

    @YamlComment("Allies item material (according to the server's version)")
    @YamlKey("team-manager.allies.material")
    @Getter
    private Material teamsManagerGuiAlliesMaterial = Material.TOTEM_OF_UNDYING;

    @YamlComment("Allies item name and lore (you can add more lines)")
    @YamlKey("team-manager.allies.text")
    @Getter
    private List<String> teamsManagerGuiAlliesText = List.of(
            "&a&lTEAM ALLIES",
            " ",
            "&r&8&lLEFT CLICK &r&7to open allies manager"
    );

    @YamlComment("Allies manager GUI name")
    @YamlKey("team-manager.allies.manager.name")
    @Getter
    private String alliesManagerGuiName = "&3&lAllies Manager";

    @YamlComment("Allies manager item name and lore (you can add more lines)")
    @YamlKey("team-manager.allies.manager.text")
    @Getter
    private List<String> alliesManagerGuiText = List.of(
            "&e&l%NAME%",
            " ",
            "&r&8&lRIGHT CLICK &r&7to delete the team from your allies (you must be the team owner)"
    );

    // ENEMIES
    @YamlComment("Modify this to change the slot of the items.\neach character is an item, and the 'g' are the slots for the teams.\nYou can move them as you want as long as you don't modify the structure/size of the lines.")
    @YamlKey("team-manager.enemies.layout")
    @Getter
    private List<String> teamsManagerEnemiesguiSetup = List.of(
            "         ",
            "  ggggg  ",
            "  ggggg  ",
            "  ggggg  ",
            "         ",
            "f   b   n"
    );

    @YamlComment("Enemies item material (according to the server's version)")
    @YamlKey("team-manager.enemies.material")
    @Getter
    private Material teamsManagerGuiEnemiesMaterial = Material.NETHERITE_SWORD;

    @YamlComment("Enemies item name and lore (you can add more lines)")
    @YamlKey("team-manager.enemies.text")
    @Getter
    private List<String> teamsManagerGuiEnemiesText = List.of(
            "&4&lTEAM ENEMIES",
            " ",
            "&r&8&lLEFT CLICK &r&7to open enemies manager"
    );

    @YamlComment("Enemies manager GUI name")
    @YamlKey("team-manager.enemies.manager.name")
    @Getter
    private String enemiesManagerGuiName = "&3&lEnemies Manager";

    @YamlComment("Enemies manager item name and lore (you can add more lines)")
    @YamlKey("team-manager.enemies.manager.text")
    @Getter
    private List<String> enemiesManagerGuiText = List.of(
            "&e&l%NAME%",
            " ",
            "&r&8&lRIGHT CLICK &r&7to delete the team from your enemies (you must be the team owner)"
    );

    @SuppressWarnings("unused")
    private TeamsGui() {
    }

    public String getTeamsListGuiName() {
        return teamsListGuiName;
    }

    public Map<String, String> getLoreMap() {
        return loreMap;
    }

}