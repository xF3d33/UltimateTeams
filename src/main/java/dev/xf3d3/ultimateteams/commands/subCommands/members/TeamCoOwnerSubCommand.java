package dev.xf3d3.ultimateteams.commands.subCommands.members;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamRank;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamCoOwnerSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";

    private final UltimateTeams plugin;

    public TeamCoOwnerSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Promote a member to co-owner
     */
    public void promoteToCoOwner(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player")
                .replace(PLAYER_PLACEHOLDER, offlinePlayer.toString())));
            return;
        }

        // Only owners can promote to co-owner
        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                        player.sendMessage(Utils.Color("&cYou cannot promote yourself!"));
                        return;
                    }

                    if (!team.getMembers().containsKey(offlinePlayer.getUniqueId())) {
                        String message = Utils.Color(messagesConfig.getString("targeted-player-is-not-in-your-team")
                            .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName()));
                        player.sendMessage(message);
                        return;
                    }

                    TeamRank currentRank = team.getMemberRank(offlinePlayer.getUniqueId());
                    
                    if (currentRank == TeamRank.OWNER) {
                        player.sendMessage(Utils.Color("&cThat player is already the owner!"));
                        return;
                    }
                    
                    if (currentRank == TeamRank.CO_OWNER) {
                        player.sendMessage(Utils.Color("&c" + offlinePlayer.getName() + " is already a Co-Owner!"));
                        return;
                    }

                    // Promote to co-owner
                    team.promoteToCoOwner(offlinePlayer.getUniqueId());
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    String message = Utils.Color("&a" + offlinePlayer.getName() + " has been promoted to &6Co-Owner&a!");
                    player.sendMessage(message);
                    team.sendTeamMessage(Utils.Color("&e" + offlinePlayer.getName() + " &ahas been promoted to &6Co-Owner&a!"));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    /**
     * Demote a co-owner to manager
     */
    public void demoteFromCoOwner(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player")
                .replace(PLAYER_PLACEHOLDER, offlinePlayer.toString())));
            return;
        }

        // Only owners can demote co-owners
        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                        player.sendMessage(Utils.Color("&cYou cannot demote yourself!"));
                        return;
                    }

                    if (!team.getMembers().containsKey(offlinePlayer.getUniqueId())) {
                        String message = Utils.Color(messagesConfig.getString("targeted-player-is-not-in-your-team")
                            .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName()));
                        player.sendMessage(message);
                        return;
                    }

                    TeamRank currentRank = team.getMemberRank(offlinePlayer.getUniqueId());
                    
                    if (currentRank != TeamRank.CO_OWNER) {
                        player.sendMessage(Utils.Color("&c" + offlinePlayer.getName() + " is not a Co-Owner!"));
                        return;
                    }

                    // Demote to manager
                    team.demoteFromCoOwner(offlinePlayer.getUniqueId());
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    String message = Utils.Color("&e" + offlinePlayer.getName() + " has been demoted from Co-Owner to Manager.");
                    player.sendMessage(message);
                    team.sendTeamMessage(Utils.Color("&e" + offlinePlayer.getName() + " &chas been demoted from Co-Owner to Manager."));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
}
