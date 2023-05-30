package dev.xf3d3.celestyteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("chatspy")
public class TeamChatSpyCommand extends BaseCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final CelestyTeams plugin;

    public TeamChatSpyCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.messagesFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
    }

    @CommandCompletion("@nothing")
    @Default
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof final Player player) {
            if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                if (player.hasPermission("celestyteams.chat.spy")){
                    if (plugin.getUsersStorageUtil().toggleChatSpy(player)){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chatspy-toggle-on")));
                    } else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chatspy-toggle-off")));
                    }
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("no-permission")));
                }
            } else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }
        }
    }
}
