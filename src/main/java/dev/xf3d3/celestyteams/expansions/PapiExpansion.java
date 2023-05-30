package dev.xf3d3.celestyteams.expansions;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.models.TeamPlayer;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {

    private final CelestyTeams plugin;

    public PapiExpansion(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "CelestyTeams";
    }

    @Override
    public @NotNull String getAuthor() {
        return CelestyTeams.getPlugin().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return CelestyTeams.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        FileConfiguration configFile = plugin.getConfig();
        Team teamOwner = plugin.getTeamStorageUtil().findTeamByOfflineOwner(player);
        Team teamMember = plugin.getTeamStorageUtil().findClanByOfflinePlayer(player);

        if (params.equalsIgnoreCase("teamName")){
            if (teamOwner != null) {
                return ColorUtils.translateColorCodes(teamOwner.getTeamFinalName() + "&r ");
            } else if (teamMember != null){
                return ColorUtils.translateColorCodes(teamMember.getTeamFinalName() + "&r ");
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamPrefix")) {
            String openBracket = configFile.getString("team-tags.brackets-opening");
            String closeBracket = configFile.getString("team-tags.brackets-closing");

            if (teamOwner != null){
                if (configFile.getBoolean("team-tags.prefix-add-brackets")){
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return ColorUtils.translateColorCodes(openBracket + teamOwner.getTeamPrefix() + closeBracket +"&r ");
                    } else {
                        return ColorUtils.translateColorCodes(openBracket + teamOwner.getTeamPrefix() + closeBracket +"&r");
                    }
                } else {
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return ColorUtils.translateColorCodes(teamOwner.getTeamPrefix() + "&r ");
                    } else {
                        return ColorUtils.translateColorCodes(teamOwner.getTeamPrefix() + "&r");
                    }
                }
            } else if (teamMember != null){
                if (configFile.getBoolean("team-tags.prefix-add-brackets")){
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return ColorUtils.translateColorCodes(openBracket + teamMember.getTeamPrefix() + closeBracket +"&r ");
                    } else {
                        return ColorUtils.translateColorCodes(openBracket + teamMember.getTeamPrefix() + closeBracket +"&r");
                    }
                } else {
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return ColorUtils.translateColorCodes(teamMember.getTeamPrefix() + "&r ");
                    } else {
                        return ColorUtils.translateColorCodes(teamMember.getTeamPrefix() + "&r");
                    }
                }
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("friendlyFire")){
            if (teamOwner != null){
                return String.valueOf(teamOwner.isFriendlyFireAllowed());
            }else if (teamMember != null){
                return String.valueOf(teamMember.isFriendlyFireAllowed());
            }else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamHomeSet")){
            if (teamOwner != null){
                return String.valueOf(plugin.getTeamStorageUtil().isHomeSet(teamOwner));
            } else if (teamMember != null){
                return String.valueOf(plugin.getTeamStorageUtil().isHomeSet(teamMember));
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamMembersSize")){
            if (teamOwner != null){
                return String.valueOf(teamOwner.getClanMembers().size());
            } else if (teamMember != null){
                return String.valueOf(teamMember.getClanMembers().size());
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamAllySize")){

            if (teamOwner != null){
                return String.valueOf(teamOwner.getClanAllies().size());
            } else if (teamMember != null){
                return String.valueOf(teamMember.getClanAllies().size());
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamEnemySize")){
            if (teamOwner != null){
                return String.valueOf(teamOwner.getClanEnemies().size());
            } else if (teamMember != null){
                return String.valueOf(teamMember.getClanEnemies().size());
            } else {
                return "";
            }
        }

        return null;
    }
}
