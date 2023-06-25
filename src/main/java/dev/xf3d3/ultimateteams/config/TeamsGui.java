package dev.xf3d3.ultimateteams.config;

import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

import java.util.Map;


@YamlFile(header = """
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃     UltimateTeams Config     ┃
        ┃      Developed by xF3d3      ┃
        ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
        ┃
        ┗╸ Information: https://modrinth.com/plugin/ultimate-teams""")
public class TeamsGui {

    @YamlComment("What name would you like the gui to have?")
    @YamlKey("team-list.name")
    private String teamsListGuiName = "&3Teams List";

    @YamlComment("Message sent when your on the first page")
    @YamlKey("team-list.GUI-first-page")
    private String firstPageName = "&7You are on the first page.";

    @YamlComment("Message sent when your on the last page")
    @YamlKey("team-list.GUI-last-page")
    private String lastPageName = "&7You are on the last page.";

    @YamlComment("The name of the previous page icon")
    @YamlKey("team-list.menu-controls.previous-page-icon-name")
    private String previousPage = "&2Previous Page";

    @YamlComment("The name of the next page icon")
    @YamlKey("team-list.menu-controls.next-page-icon-name")
    private String nextPage = "&2Next Page";

    @YamlComment("The name of the close/go back icon")
    @YamlKey("team-list.menu-controls.close-go-back-icon-name")
    private String closeGui = "&4Close/Go Back";

    // todo: this
    @YamlComment("Do you want to use the Team Name as the title for the icon: [Default value: true]\n If below is 'false' the name will be empty.")
    @YamlKey("team-list.icons.icon-display-name")
    private boolean iconsDisplayName = true;

    @YamlComment("This allows you to customise the lore text for the player head icons in the TeamList GUI")
    @YamlKey("team-list.icons.lore")
    private Map<String, String> loreMap = Map.of(
            "header", "&7----------",
    "prefix", "&3Team Prefix: ",
    "owner-online", "&3Team Owner: &d",
    "owner-offline", "&3Team Owner &7&o(Offline)&3: &d",
    "members", "&3Team Members:",
    "allies", "&3Team Allies:",
    "enemies", "&3Team Enemies:",
    "footer-1", "&7----------",
    "action", "&fClick to send an invite request to this team owner if online",
    "footer-2", "&7----------"
    );


    @YamlComment("What name would you like the gui to have?")
    @YamlKey("team-join.name")
    private String teamJoinGuiName = "&3Ask to join Team?";

    @YamlComment("This allows you to customise the name text for the icons in the TeamJoinRequest GUI.")
    @YamlKey("team-join.icons.send-request-name")
    private String teamJoinSendRequestName = "&a&oSend request to join?";

    @YamlKey("team-join.icons.cancel-request-name")
    private String teamJoinCancelRequestName = "&c&oCancel and go back";


    @SuppressWarnings("unused")
    private TeamsGui() {
    }

    public String getTeamsListGuiName() {
        return teamsListGuiName;
    }

    public String getFirstPageName() {
        return firstPageName;
    }

    public String getLastPageName() {
        return lastPageName;
    }

    public String getPreviousPageName() {
        return previousPage;
    }

    public String getNextPageName() {
        return nextPage;
    }

    public String getCloseGuiName() {
        return closeGui;
    }

    public boolean isIconsDisplayName() {
        return iconsDisplayName;
    }

    public Map<String, String> getLoreMap() {
        return loreMap;
    }

    public String getTeamJoinGuiName() {
        return teamJoinGuiName;
    }

    public String getSendRequestText() {
        return teamJoinSendRequestName;
    }

    public String getCancelRequestText() {
        return teamJoinCancelRequestName;
    }

}