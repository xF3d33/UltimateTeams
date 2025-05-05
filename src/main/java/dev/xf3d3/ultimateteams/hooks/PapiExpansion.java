package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

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
        return "xF3d3";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginVersion().toStringWithoutMetadata();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {;
        Optional<Team> optionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (params.equalsIgnoreCase("teamName")) {
            return Utils.Color(optionalTeam.map(Team::getName).orElse(plugin.getSettings().getNotInTeamPlaceholder()));
        }

        if (params.equalsIgnoreCase("teamPrefix")) {
            String openBracket = plugin.getSettings().getPrefixBracketsOpening();
            String closeBracket = plugin.getSettings().getPrefixBracketsClosing();

            return Utils.Color(optionalTeam
                    .map(Team::getPrefix)
                    .map(prefix -> plugin.getSettings().addPrefixBrackets()
                            ? openBracket + prefix + closeBracket
                            : prefix + "&r" + (plugin.getSettings().addSpaceAfterPrefix() ? " " : ""))
                    .orElse(plugin.getSettings().getNotInTeamPlaceholder()));
        }

        if (params.equalsIgnoreCase("friendlyFire")) {
            return Utils.Color(optionalTeam.map(Team::isFriendlyFire).map(String::valueOf).orElse(plugin.getSettings().getNotInTeamPlaceholder()));
        }

        if (params.equalsIgnoreCase("teamHomeSet")) {
            return Utils.Color(optionalTeam.map(plugin.getTeamStorageUtil()::isHomeSet).map(String::valueOf).orElse(plugin.getSettings().getNotInTeamPlaceholder()));
        }

        if (params.equalsIgnoreCase("teamMembersSize")) {
            return Utils.Color(optionalTeam.map(Team::getMembers).map(Map::size).map(String::valueOf).orElse(plugin.getSettings().getNotInTeamPlaceholder()));
        }

        if (params.equalsIgnoreCase("teamAllySize")) {
            return Utils.Color(
                    optionalTeam
                            .map(team -> team.getRelations(plugin).values().stream())
                            .map(relationStream -> relationStream.filter(relation -> relation == Team.Relation.ALLY).count())
                            .map(String::valueOf)
                            .orElse(plugin.getSettings().getNotInTeamPlaceholder())
            );
        }

        if (params.equalsIgnoreCase("teamEnemySize")) {
            return Utils.Color(
                    optionalTeam
                            .map(team -> team.getRelations(plugin).values().stream())
                            .map(relationStream -> relationStream.filter(relation -> relation == Team.Relation.ENEMY).count())
                            .map(String::valueOf)
                            .orElse(plugin.getSettings().getNotInTeamPlaceholder())
            );
        }

        if (params.equalsIgnoreCase("isInTeam")) {
            return String.valueOf(optionalTeam.isPresent());
        }

        return null;
    }
}
