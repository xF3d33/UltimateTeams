package dev.xf3d3.ultimateteams.listeners;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import dev.xf3d3.ultimateteams.utils.UsersStorage;
import dev.xf3d3.ultimateteams.utils.Utils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerChatEvent implements Listener {
    private final UltimateTeams plugin;

    public PlayerChatEvent(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        boolean teamChatTalking = UsersStorage.ChatType.TEAM_CHAT.equals(
                plugin.getUsersStorageUtil()
                        .getChatPlayers()
                        .get(player.getUniqueId())
        );
        boolean allyChatTalking = UsersStorage.ChatType.ALLY_CHAT.equals(
                plugin.getUsersStorageUtil()
                        .getChatPlayers()
                        .get(player.getUniqueId())
        );


        if (teamChatTalking) {
            event.setCancelled(true);
            sendToTeamChat(player, event.message());
        }

        if (allyChatTalking) {
            event.setCancelled(true);
            sendToAllyChat(player, event.message());
        }
    }

    private void sendToTeamChat(Player player, Component message) {
        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    String chatSpyPrefix = Utils.Color(plugin.getSettings().getTeamChatSpyPrefix());
                    final String prefix = Utils.Color(plugin.getSettings().getTeamChatPrefix()
                            .replace("%TEAM%", team.getName())
                            .replace("%PLAYER%", player.getName()));

                    Component newMessage = Component
                            .text(prefix + " " + "&d" + player.getName() + ":&r" + " ")
                            .append(message);


                    // Send message to team members
                    team.sendTeamMessage(newMessage);

                    // Send spy message directly to players with permission (hidden from Discord)
                    if (plugin.getSettings().teamChatSpyEnabled()) {
                        Component spyMessage = Component
                                .text(chatSpyPrefix)
                                .append(message);

                        Bukkit.getOnlinePlayers().stream()
                                .filter(p -> p.hasPermission("ultimateteams.chat.spy"))
                                .forEach(p -> p.sendMessage(spyMessage));
                    }

                    String msg = PlainTextComponentSerializer.plainText().serialize(message);

                    // Send globally via a message
                    plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                            .type(Message.Type.TEAM_CHAT_MESSAGE)
                            .payload(Payload.string(msg))
                            .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                            .build()
                            .send(broker, player));
                },
                () -> plugin.getUsersStorageUtil().getChatPlayers().remove(player.getUniqueId())
        );
    }

    private void sendToAllyChat(Player player, Component message) {
        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Map<Team, Team.Relation> relations = team.getRelations(plugin);

                    final Set<Team> allies = relations.entrySet().stream()
                            .filter(entry -> entry.getValue() == Team.Relation.ALLY)
                            .map(Map.Entry::getKey)
                            .filter(otherTeam -> team.areRelationsBilateral(otherTeam, Team.Relation.ALLY))
                            .collect(Collectors.toSet());

                    String chatSpyPrefix = Utils.Color(plugin.getSettings().getTeamChatSpyPrefix());
                    final String prefix = Utils.Color(plugin.getSettings().getTeamAllyChatPrefix()
                            .replace("%TEAM%", team.getName())
                            .replace("%PLAYER%", player.getName()));

                    Component newMessage = Component
                            .text(prefix + " " + "&d" + player.getName() + ":&r" + " ")
                            .append(message);


                    // Send message to team members
                    team.sendTeamMessage(newMessage);

                    // Send message to allied team Members
                    allies.forEach(
                            alliedTeam -> alliedTeam.sendTeamMessage(newMessage)
                    );


                    // Send spy message directly to players with permission (hidden from Discord)
                    if (plugin.getSettings().teamChatSpyEnabled()) {
                        Component spyMessage = Component
                                .text(chatSpyPrefix)
                                .append(message);

                        Bukkit.getOnlinePlayers().stream()
                                .filter(p -> p.hasPermission("ultimateteams.chat.spy"))
                                .forEach(p -> p.sendMessage(spyMessage));
                    }

                    String msg = PlainTextComponentSerializer.plainText().serialize(message);
                    // Send globally via a message
                    plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                            .type(Message.Type.TEAM_ALLY_CHAT_MESSAGE)
                            .payload(Payload.string(msg))
                            .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                            .build()
                            .send(broker, player));
                },
                () -> plugin.getUsersStorageUtil().getChatPlayers().remove(player.getUniqueId())
        );
    }
}
