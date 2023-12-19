package dev.xf3d3.ultimateteams.manager;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamDisbandEvent;
import dev.xf3d3.ultimateteams.api.TeamTransferOwnershipEvent;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.team.Warp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class TeamsManager extends Manager {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
    //private static final Map<UUID, Team> teamsList = new HashMap<>();

    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    protected TeamsManager(@NotNull UltimateTeams plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void loadTeams() {
        final List<Team> teams = plugin.getDatabase().getAllTeams();

        plugin.log(Level.INFO, "Loading teams from the database...");
        LocalTime startTime = LocalTime.now();
        plugin.setTeams(teams);

        final int teamCount = plugin.getTeams().size();
        final int memberCount = plugin.getTeams().stream().mapToInt(town -> town.getTeamMembers().size()).sum();
        plugin.log(Level.INFO, "Loaded " + teamCount + " team(s) with " + memberCount + " member(s) in " +
                (ChronoUnit.MILLIS.between(startTime, LocalTime.now()) / 1000d) + " seconds");
    }


    public Team createTeam(Player player, String teamName) {
        UUID ownerUUID = player.getUniqueId();
        //Team newTeam = new Team(ownerUUIDString, teamName);

        final Team team = plugin.getDatabase().createTeam(teamName, player);
        plugin.getTeams().add(team);
        plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                .type(Message.Type.TEAM_UPDATE)
                .payload(Payload.integer(team.getID()))
                .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                .build()
                .send(broker, player));

        return team;
    }

    public void deleteTeam(Player player) {
        UUID uuid = player.getUniqueId();

        findTeamByOwner(player).ifPresentOrElse(
                team -> {
                    fireTeamDisbandEvent(player);

                    // TODO: also remove from enemies teams
                    for (String teamUUIDString : team.getTeamAllies()) {
                        Player alliedTeamOwner = Bukkit.getOfflinePlayer(UUID.fromString(teamUUIDString)).getPlayer();

                        if (alliedTeamOwner == null) {
                            plugin.log(Level.SEVERE, "Cannot delete team because allied team owner is null");
                            return;
                        }

                        findTeamByOwner(alliedTeamOwner).ifPresent(alliedTeam -> {
                            alliedTeam.removeTeamAlly(uuid.toString());

                            plugin.runAsync(() -> plugin.getManager().updateTeamData(player, alliedTeam));
                        });

                        player.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));
                    }

                    deleteTeam(team);
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-disband-failure")))
        );

    }

    // TODO: add checks to remove enemies and allies
    public void deleteTeam(Team team) {
        plugin.runAsync(() -> plugin.getDatabase().deleteTeam(team.getID()));
        plugin.getTeams().removeIf(t -> t.getID() == team.getID());
    }

    public boolean isTeamOwner(Player player) {
        UUID uuid = player.getUniqueId();
        String ownerUUID = uuid.toString();

        for (Team team : plugin.getTeams()) {
            if (Objects.equals(team.getTeamOwner(), ownerUUID))
                return true;
        }
        return false;
    }

    public Optional<Team> findTeamByOwner(Player player) {
        UUID uuid = player.getUniqueId();

        for (Team team : plugin.getTeams()) {
            if (Objects.equals(team.getTeamOwner(), String.valueOf(uuid)))
                return Optional.of(team);
        }
        return Optional.empty();
    }

    public Optional<Team> findTeamByPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        for (Team team : plugin.getTeams()) {
            if (team.getTeamMembers() != null) {
                for (String member : team.getTeamMembers()) {
                    if (Objects.equals(team.getTeamOwner(), String.valueOf(uuid)))
                        return Optional.of(team);

                    if (Objects.equals(member, String.valueOf(uuid)))
                        return Optional.of(team);
                }
            }
        }
        return Optional.empty();
    }


    public Optional<Team> findTeamByMember(Player player) {
        for (Team team : plugin.getTeams()) {
            if (team.getTeamMembers() != null) {
                for (String member : team.getTeamMembers()) {
                    if (Objects.equals(member, String.valueOf(player.getUniqueId()))) {
                        return Optional.of(team);
                    }
                }
            }
        }
        return Optional.empty();
    }


    public void updatePrefix(Team team, Player player, String prefix) {
        UUID uuid = player.getUniqueId();
        if (!isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        team.setTeamPrefix(prefix);
        plugin.runAsync(() -> plugin.getManager().updateTeamData(player, team));
    }

    public boolean addTeamMember(Team team, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        team.addTeamMember(memberUUID);

        plugin.runAsync(() -> plugin.getManager().updateTeamData(player, team));

        return true;
    }

    public void addTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID uuid = enemyTeamOwner.getUniqueId();

        String allyUUIDString = uuid.toString();
        UUID allyUUID = UUID.fromString(allyUUIDString);

        // team update
        Team team = teamsList.get(ownerUUID);
        team.addTeamEnemy(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.addTeamEnemy(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void removeTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID uuid = enemyTeamOwner.getUniqueId();

        String allyUUIDString = uuid.toString();
        UUID allyUUID = UUID.fromString(allyUUIDString);

        // team update
        Team team = teamsList.get(ownerUUID);
        team.removeTeamEnemy(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.removeTeamEnemy(String.valueOf(ownerUUID));

        // Update the team
        plugin.runAsync(() -> plugin.getManager().updateTeamData(teamOwner, team));

        // Update the allied team
        plugin.runAsync(() -> plugin.getManager().updateTeamData(enemyTeamOwner, alliedTeam));
    }

    public void addTeamAlly(Player teamOwner, Player allyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID allyUUID = allyTeamOwner.getUniqueId();

        String allyUUIDString = allyUUID.toString();

        // team update
        Team team = teamsList.get(ownerUUID);
        team.addTeamAlly(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.addTeamAlly(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(alliedTeam));
    }

    public void removeTeamAlly(Player teamOwner, Player allyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID uuid = allyTeamOwner.getUniqueId();

        String allyUUIDString = uuid.toString();
        UUID allyUUID = UUID.fromString(allyUUIDString);

        // team update
        Team team = teamsList.get(ownerUUID);
        team.removeTeamAlly(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.removeTeamAlly(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(alliedTeam));
    }

    public boolean isHomeSet(Team team) {
        return Optional.ofNullable(team.getTeamHomeWorld()).isPresent();
    }

    public void deleteHome(Player player, Team team) {
        team.setTeamHomeWorld(null);

        plugin.runAsync(() -> plugin.getManager().updateTeamData(player, team));
    }

    public boolean kickPlayer(Team team, OfflinePlayer player) {
        boolean removed = team.removeTeamMember(player.getUniqueId().toString());

        plugin.runAsync(() -> plugin.getManager().updateTeamData(team));

        return removed;
    }

    public String stripTeamNameColorCodes(Team team) {
        String teamFinalName = team.getName();

        return teamFinalName == null ? null : STRIP_COLOR_PATTERN.matcher(teamFinalName).replaceAll("");
    }

    // TODO: make it works again
    public boolean transferTeamOwner(Team originalTeam, Player originalTeamOwner, Player newTeamOwner) {
        Optional<Team> team = findTeamByOwner(originalTeamOwner);

        if (team.isPresent()) {
            if (!Objects.equals(team.get().getName(), originalTeam.getName())) {
                return false;
            }

            if (findTeamByMember(newTeamOwner).isPresent()) {
                return false;
            }

            team.get().setTeamOwner(String.valueOf(newTeamOwner.getUniqueId()));
            plugin.runAsync(() -> plugin.getManager().updateTeamData(originalTeamOwner, team.get()));

            return true;
        } else {
            return false;
        }
    }

    public void addWarp(@NotNull Player player, @NotNull Team team, @NotNull Warp warp) {
        team.addTeamWarp(warp);
        plugin.runAsync(() -> plugin.getManager().updateTeamData(player, team));
    }


    private void fireTeamDisbandEvent(Player player) {
        findTeamByOwner(player).ifPresent(team -> {
            TeamDisbandEvent teamDisbandEvent = new TeamDisbandEvent(player, team.getName());
            Bukkit.getPluginManager().callEvent(teamDisbandEvent);
        });

    }

    private void fireTeamTransferOwnershipEvent(Player originalTeamOwner, Player newTeamOwner, Team newTeam){
        TeamTransferOwnershipEvent teamTransferOwnershipEvent = new TeamTransferOwnershipEvent(originalTeamOwner, originalTeamOwner, newTeamOwner, newTeam);
        Bukkit.getPluginManager().callEvent(teamTransferOwnershipEvent);
    }
}
