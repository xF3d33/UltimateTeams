package dev.xf3d3.ultimateteams.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@CommandAlias("chatspy|teamchatspy|tchatspy")
public class TeamChatSpyCommand extends BaseCommand {
    private final UltimateTeams plugin;

    public TeamChatSpyCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @CommandCompletion("@nothing")
    @Default
    public void onCommand(CommandSender sender) {
        if (sender instanceof final Player player) {
            if (plugin.getSettings().teamChatSpyEnabled()){
                if (player.hasPermission("ultimateteams.chat.spy")) {
                    if (plugin.getUsersStorageUtil().toggleChatSpy(player)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getChatspyToggleOn()));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getChatspyToggleOff()));
                    }
                } else {
                    player.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                }
            } else {
                player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            }
        }
    }
}
