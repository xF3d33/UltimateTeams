package dev.xf3d3.ultimateteams.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@CommandAlias("ac|allychat|achat")
public class TeamAllyChatCommand extends BaseCommand {
    private final Logger logger;
    private final UltimateTeams plugin;

    public TeamAllyChatCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("<message>")
    @Syntax("/allychat <message>")
    @CommandPermission("ultimateteams.allychat")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)){
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        // Check if enabled
        if (!plugin.getSettings().teamAllyChatEnabled()){
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        // Check args
        if (args.length < 1) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getAllychatIncorrectUsage()));
            return;
        }
        
        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Map<Team, Team.Relation> relations = team.getRelations(plugin);

                    final Set<Team> allies = relations.entrySet().stream()
                            .filter(entry -> entry.getValue() == Team.Relation.ALLY)
                            .map(Map.Entry::getKey)
                            .filter(otherTeam -> team.areRelationsBilateral(otherTeam, Team.Relation.ALLY))
                            .collect(Collectors.toSet());

                    String chatSpyPrefix = plugin.getSettings().getTeamChatSpyPrefix();
                    StringBuilder messageString = new StringBuilder();
                    messageString.append(plugin.getSettings().getTeamAllyChatPrefix()).append(" ");
                    messageString.append("&d").append(player.getName()).append(":&r").append(" ");
                    for (String arg : args) {
                        messageString.append(arg).append(" ");
                    }

                    final String msg = messageString.toString()
                            .replace("%TEAM%", team.getName())
                            .replace("%PLAYER%", player.getName());

                    // Send message to team members
                    team.sendTeamMessage(Utils.Color(msg));

                    // Send message to allied team Members
                    allies.forEach(
                            alliedTeam -> alliedTeam.sendTeamMessage(Utils.Color(msg))
                    );

                    // Send spy message
                    if (plugin.getSettings().teamChatSpyEnabled()) {
                        Bukkit.broadcast(Utils.Color(chatSpyPrefix + " " + msg), "ultimateteams.chat.spy");
                    }

                    // Send globally via a message
                    plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                            .type(Message.Type.TEAM_ALLY_CHAT_MESSAGE)
                            .payload(Payload.string(msg))
                            .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                            .build()
                            .send(broker, player));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
