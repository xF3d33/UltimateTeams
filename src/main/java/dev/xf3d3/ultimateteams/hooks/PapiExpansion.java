package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Preferences;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamHome;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import dev.xf3d3.ultimateteams.utils.UsersStorage;
import dev.xf3d3.ultimateteams.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private String getNotInTeamPlaceholder() {
        return plugin.getSettings().getNotInTeamPlaceholder();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Optional<Team> optionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (params.startsWith("team_balance_")) {
            return Optional.of(params.substring("team_balance_".length()))
                    .map(name -> name.replace("%", ""))
                    .filter(name -> !name.isEmpty())
                    .flatMap(name -> plugin.getTeamStorageUtil().findTeamByName(name))
                    .map(Team::getBalance)
                    .map(String::valueOf)
                    .orElse(null);
        }

        if (params.startsWith("is_in_team_")) {
            return Optional.of(params.substring("is_in_team_".length()))
                    .map(name -> name.replace("%", ""))
                    .filter(name -> !name.isEmpty())
                    .map(Bukkit::getOfflinePlayerIfCached)
                    .map(OfflinePlayer::getUniqueId)
                    .flatMap(uuid -> plugin.getTeamStorageUtil().findTeamByMember(uuid))
                    .map(team -> "true")
                    .orElse("false");
        }


        switch (params.toLowerCase()) {
            case "team_name", "teamname":
                return Utils.Color(optionalTeam
                        .map(Team::getName)
                        .orElse(getNotInTeamPlaceholder()));

            case "team_name_raw":
                return Utils.Color(optionalTeam
                        .map(Team::getName)
                        .map(Utils::removeColors)
                        .orElse(getNotInTeamPlaceholder()));

            case "team_prefix", "teamprefix": {
                String openBracket = plugin.getSettings().getPrefixBracketsOpening();
                String closeBracket = plugin.getSettings().getPrefixBracketsClosing();

                return Utils.Color(optionalTeam
                        .map(team -> {
                            String prefix = team.getPrefix();
                            if (prefix == null || prefix.isEmpty()) {
                                return "";
                            }
                            return plugin.getSettings().addPrefixBrackets()
                                    ? openBracket + prefix + closeBracket
                                    : prefix + "&r" + (plugin.getSettings().addSpaceAfterPrefix() ? " " : "");
                        })
                        .orElse("") // Empty string if no team
                        + (optionalTeam.isPresent() ? "" : getNotInTeamPlaceholder())
                );
            }

            case "team_owner":
                return optionalTeam
                        .map(Team::getOwner)
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .orElse(getNotInTeamPlaceholder());

            case "team_role":
                return optionalTeam
                        .map(team -> team.getMembers().get(player.getUniqueId()))
                        .map(role -> switch (role) {
                            case 3 -> "Owner";
                            case 2 -> "Manager";
                            default -> "Member";
                        })
                        .orElse(getNotInTeamPlaceholder());

            case "team_online_members_count":
                return optionalTeam
                        .map(Team::getOnlineMembers)
                        .map(List::size)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_balance":
                return optionalTeam
                        .map(Team::getBalance)
                        //.map(Double::intValue)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_motd":
                return optionalTeam
                        .map(team -> Objects.requireNonNullElse(team.getMotd(), plugin.getMessages().getTeamMotdNotSet()))
                        .orElse(getNotInTeamPlaceholder());

            case "team_fee":
                return optionalTeam
                        .map(Team::getJoin_fee)
                        //.map(Double::intValue)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_fee_status":
                return optionalTeam
                        .map(Team::getJoin_fee)
                        .map(fee -> fee > 0)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_pvp", "friendlyfire":
                return Utils.Color(optionalTeam
                        .map(Team::isFriendlyFire)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder()));

            case "team_home_set", "teamhomeset":
                return Utils.Color(optionalTeam
                        .map(plugin.getTeamStorageUtil()::isHomeSet)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder()));

            case "team_home_world":
                return optionalTeam
                        .map(Team::getHome)
                        .map(TeamHome::getWarpWorld)
                        .orElse(getNotInTeamPlaceholder());

            case "team_home_x":
                return optionalTeam
                        .map(Team::getHome)
                        .map(TeamHome::getWarpX)
                        .map(Double::intValue)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_home_y":
                return optionalTeam
                        .map(Team::getHome)
                        .map(TeamHome::getWarpY)
                        .map(Double::intValue)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_home_z":
                return optionalTeam
                        .map(Team::getHome)
                        .map(TeamHome::getWarpZ)
                        .map(Double::intValue)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_members_count", "teammemberssize":
                return Utils.Color(optionalTeam
                        .map(Team::getMembers)
                        .map(Map::size)
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder()));

            case "team_allies_count", "teamallysize":
                return Utils.Color(optionalTeam
                        .map(team -> team.getRelations(plugin).values().stream())
                        .map(relationStream -> relationStream.filter(relation -> relation == Team.Relation.ALLY).count())
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder()));

            case "team_enemies_count", "teamenemysize":
                return Utils.Color(optionalTeam
                        .map(team -> team.getRelations(plugin).values().stream())
                        .map(relationStream -> relationStream.filter(relation -> relation == Team.Relation.ENEMY).count())
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder()));

            case "is_in_team", "isinteam":
                return String.valueOf(optionalTeam.isPresent());

            case "team_max_members":
                return Optional.ofNullable(plugin.getUsersStorageUtil().getUsermap().get(player.getUniqueId()))
                        .flatMap(teamPlayer ->
                                Optional.ofNullable(Bukkit.getPlayer(player.getUniqueId()))
                                        .map(onlinePlayer ->
                                                teamPlayer.getMaxMembers(
                                                        onlinePlayer,
                                                        plugin.getSettings().getTeamMaxSize(),
                                                        plugin.getSettings().getStackedTeamSize()
                                                )
                                        )

                        )
                        .map(String::valueOf)
                        .orElse(null);

            case "team_kills_total":
                return optionalTeam
                        .map(team ->
                            team.getMembers().keySet().stream()
                                    .map(Bukkit::getOfflinePlayer)
                                    .mapToInt(offlinePlayer -> offlinePlayer.getStatistic(Statistic.PLAYER_KILLS))
                                    .sum()
                        )
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "team_deaths_total":
                return optionalTeam
                        .map(team ->
                                team.getMembers().keySet().stream()
                                        .map(Bukkit::getOfflinePlayer)
                                        .mapToInt(offlinePlayer -> offlinePlayer.getStatistic(Statistic.DEATHS))
                                        .sum()
                        )
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());


            case "player_invites_status":
                return Optional.ofNullable(plugin.getUsersStorageUtil().getUsermap().get(player.getUniqueId()))
                        .map(TeamPlayer::getPreferences)
                        .map(Preferences::isAcceptInvitations)
                        .map(String::valueOf)
                        .orElse(null);

            case "player_team_chat_talking":
                return Optional.of(UsersStorage.ChatType.TEAM_CHAT.equals(
                        plugin.getUsersStorageUtil()
                                .getChatPlayers()
                                .get(player.getUniqueId())
                        ))
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());

            case "player_ally_chat_talking":
                return Optional.of(UsersStorage.ChatType.ALLY_CHAT.equals(
                                plugin.getUsersStorageUtil()
                                        .getChatPlayers()
                                        .get(player.getUniqueId())
                        ))
                        .map(String::valueOf)
                        .orElse(getNotInTeamPlaceholder());


            // No matching placeholder found
            default:
                return null;
        }
    }
}