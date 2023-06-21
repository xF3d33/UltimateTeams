package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@CommandAlias("chatspy")
public class TeamChatSpyCommand extends BaseCommand {
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamChatSpyCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    @CommandCompletion("@nothing")
    @Default
    public void onCommand(CommandSender sender) {
        if (sender instanceof final Player player) {
            if (plugin.getSettings().teamChatSpyEnabled()){
                if (player.hasPermission("ultimateteams.chat.spy")){
                    if (plugin.getUsersStorageUtil().toggleChatSpy(player)){
                        player.sendMessage(Utils.Color(messagesConfig.getString("chatspy-toggle-on")));
                    } else {
                        player.sendMessage(Utils.Color(messagesConfig.getString("chatspy-toggle-off")));
                    }
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            }
        }
    }
}
