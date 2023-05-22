package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TeamTransferOwnerSubCommand {

    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private final CelestyTeams plugin;

    public TeamTransferOwnerSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean transferClanOwnerSubCommand(CommandSender sender, String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length > 1){
                String PLAYER_PLACEHOLDER = "%PLAYER%";
                Player newClanOwner = Bukkit.getPlayerExact(args[1]);
                if (newClanOwner != null){
                    if (newClanOwner != player){
                        if (plugin.getTeamStorageUtil().isClanOwner(player)){
                            Team originalTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);
                            if (originalTeam != null){
                                try {
                                    Team newTeam = plugin.getTeamStorageUtil().transferClanOwner(originalTeam, player, newClanOwner);
                                    if (newTeam != null){
                                        String OLD_OWNER_PLACEHOLDER = "%OLDOWNER%";
                                        String NEW_CLAN_NAME = "%CLAN%";
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ownership-transfer-successful")
                                                .replace(PLAYER_PLACEHOLDER, newClanOwner.getName())));
                                        newClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ownership-transfer-new-owner")
                                                .replace(OLD_OWNER_PLACEHOLDER, player.getName()).replace(NEW_CLAN_NAME, newTeam.getTeamFinalName())));
                                        return true;
                                    }
                                }catch (IOException e) {
                                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-2")));
                                    e.printStackTrace();
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ownership-transfer-failed-cannot-transfer-to-self")
                                .replace(PLAYER_PLACEHOLDER, args[1])));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ownership-transfer-failure-owner-offline")
                            .replace(PLAYER_PLACEHOLDER, args[1])));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-team-transfer-ownership-command-usage")));
            }
        }
        return true;
    }
}
