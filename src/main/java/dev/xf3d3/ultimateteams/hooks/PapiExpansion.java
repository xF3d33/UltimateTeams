package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {

    private final UltimateTeams plugin;

    public PapiExpansion(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "UltimateTeams";
    }

    @Override
    public @NotNull String getAuthor() {
        return UltimateTeams.getPlugin().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return UltimateTeams.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        FileConfiguration configFile = plugin.getConfig();
        Team teamOwner = plugin.getTeamStorageUtil().findTeamByOfflineOwner(player);
        Team teamMember = plugin.getTeamStorageUtil().findTeamByOfflinePlayer(player);

        if (params.equalsIgnoreCase("teamName")){
            if (teamOwner != null) {
                return Utils.Color(teamOwner.getTeamFinalName() + "&r ");
            } else if (teamMember != null){
                return Utils.Color(teamMember.getTeamFinalName() + "&r ");
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
                        return Utils.Color(openBracket + teamOwner.getTeamPrefix() + closeBracket +"&r ");
                    } else {
                        return Utils.Color(openBracket + teamOwner.getTeamPrefix() + closeBracket +"&r");
                    }
                } else {
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return Utils.Color(teamOwner.getTeamPrefix() + "&r ");
                    } else {
                        return Utils.Color(teamOwner.getTeamPrefix() + "&r");
                    }
                }
            } else if (teamMember != null){
                if (configFile.getBoolean("team-tags.prefix-add-brackets")){
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return Utils.Color(openBracket + teamMember.getTeamPrefix() + closeBracket +"&r ");
                    } else {
                        return Utils.Color(openBracket + teamMember.getTeamPrefix() + closeBracket +"&r");
                    }
                } else {
                    if (configFile.getBoolean("team-tags.prefix-add-space-after")){
                        return Utils.Color(teamMember.getTeamPrefix() + "&r ");
                    } else {
                        return Utils.Color(teamMember.getTeamPrefix() + "&r");
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
                return String.valueOf(teamOwner.getTeamMembers().size());
            } else if (teamMember != null){
                return String.valueOf(teamMember.getTeamMembers().size());
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamAllySize")){

            if (teamOwner != null){
                return String.valueOf(teamOwner.getTeamAllies().size());
            } else if (teamMember != null){
                return String.valueOf(teamMember.getTeamAllies().size());
            } else {
                return "";
            }
        }

        if (params.equalsIgnoreCase("teamEnemySize")){
            if (teamOwner != null){
                return String.valueOf(teamOwner.getTeamEnemies().size());
            } else if (teamMember != null){
                return String.valueOf(teamMember.getTeamEnemies().size());
            } else {
                return "";
            }
        }

        return null;
    }
}
