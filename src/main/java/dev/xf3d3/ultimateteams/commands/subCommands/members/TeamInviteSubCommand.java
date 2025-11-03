package dev.xf3d3.ultimateteams.commands.subCommands.members;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamInviteSubCommand {

    private final FileConfiguration messagesConfig;
    private static final String INVITED_PLAYER = "%INVITED%";
    public static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    public static final String TEAM_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamInviteSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teamInviteSendSubCommand(CommandSender sender, String inviteeName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(inviteeName);

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {

                    // Check permission - Co-owners can invite without needing the INVITE permission
                    boolean canInvite = plugin.getTeamStorageUtil().isTeamOwner(player) 
                        || plugin.getTeamStorageUtil().isTeamCoOwner(player)
                        || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.INVITE));
                    
                    if (!canInvite) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }

                    if (offlinePlayer.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("team-invite-self-error")));
                        return;
                    }

                    // Check if the player is already in a team
                    if (plugin.getTeamStorageUtil().findTeamByMember(offlinePlayer.getUniqueId()).isPresent()) {
                        String playerAlreadyInTeam = Utils.Color(messagesConfig.getString("team-invite-invited-already-in-team")).replace(INVITED_PLAYER, inviteeName);
                        sender.sendMessage(playerAlreadyInTeam);
                        return;
                    }

                    // Check if the player has another active invite
                    if (plugin.getTeamInviteUtil().hasInvitee(offlinePlayer.getUniqueId())) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-failed").replaceAll("%INVITED%", inviteeName)));
                        return;
                    }


                    plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
                        // Check if team has reached max members limit
                        if (team.hasReachedMaxMembers()) {
                            player.sendMessage(Utils.Color("&cYour team has reached the maximum member limit of &e" + team.getMaxMembers() + "&c!"));
                            player.sendMessage(Utils.Color("&7Use &e/team upgrade members &7to increase this limit."));
                            return;
                        }

                        boolean invited = plugin.getTeamInviteUtil().createInvite(team.getId(), player, offlinePlayer.getUniqueId());

                        if (invited) {
                            String confirmationString = Utils.Color(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, inviteeName);
                            player.sendMessage(confirmationString);

                            String invitationString = Utils.Color(messagesConfig.getString("team-invited-player-invite-pending")).replace("%TEAMOWNER%", player.getName());

                            Optional.ofNullable(offlinePlayer.getPlayer())
                                    .ifPresent(invitedPlayer -> {
                                        invitedPlayer.sendMessage(invitationString);
                                        
                                        // Send clickable accept/decline buttons
                                        sendClickableInvite(invitedPlayer, team.getName(), player.getName());
                                    });
                        } else {
                            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, inviteeName));
                        }

                    });
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );


            /*if (plugin.getSettings().FloodGateHook()) {
                if (plugin.getFloodgateApi() != null) {
                    if (plugin.getBedrockPlayers().containsKey(invitedPlayer)) {
                        String bedrockInvitedPlayerUUIDString = plugin.getBedrockPlayers().get(String.valueOf(invitedPlayer.getUniqueId()));


                        if (plugin.getTeamInviteUtil().createInvite(player.getUniqueId().toString(), bedrockInvitedPlayerUUIDString) != null) {
                            String confirmationString = Utils.Color(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(confirmationString);
                            String invitationString = Utils.Color(messagesConfig.getString("team-invited-player-invite-pending")).replace("%TEAMOWNER%", player.getName());
                            invitedPlayer.sendMessage(invitationString);
                            return;
                        } else {
                            String failureString = Utils.Color(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(failureString);
                            return;
                        }
                    } else {
                        if (plugin.getTeamInviteUtil().createInvite(player.getUniqueId().toString(), invitedPlayer.getUniqueId().toString()) != null) {
                            String confirmationString = Utils.Color(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(confirmationString);
                            String invitationString = Utils.Color(messagesConfig.getString("team-invited-player-invite-pending")).replace("%TEAMOWNER%", player.getName());
                            invitedPlayer.sendMessage(invitationString);
                            return;
                        } else {
                            String failureString = Utils.Color(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(failureString);
                            return;
                        }
                    }
                }
            }*/
    }

    public void teamInviteDenySubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamInviteUtil().hasInvitee(player.getUniqueId())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-deny-failed-no-invite")));
            return;
        }

        if (plugin.getTeamInviteUtil().declineInvite(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-denied")));
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-deny-fail")));
        }
    }


    // ACCEPT
    public void teamInviteAcceptSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamInviteUtil().hasInvitee(player.getUniqueId())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed-no-invite")));
            return;
        }

        plugin.getTeamInviteUtil().getInvite(player.getUniqueId()).ifPresentOrElse(
                invite -> plugin.getTeamStorageUtil().findTeamByMember(invite.getInviter()).ifPresentOrElse(
                        team -> {
                            plugin.getTeamStorageUtil().addTeamMember(team, player);
                            plugin.getTeamInviteUtil().acceptInvite(invite, player);

                            String joinMessage = Utils.Color(messagesConfig.getString("team-join-successful")).replace(TEAM_PLACEHOLDER, team.getName());
                            player.sendMessage(joinMessage);

                            if (!plugin.getSettings().teamJoinAnnounce())
                                return;

                            // Send message to team members
                            team.sendTeamMessage(Utils.Color(messagesConfig.getString("team-join-broadcast-chat")
                                    .replace(PLAYER_PLACEHOLDER, player.getName())
                                    .replace(TEAM_PLACEHOLDER, Utils.Color(team.getName()))));
                        },
                        () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed-no-valid-team")))
                ),
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed-no-invite")))
        );
    }

    /**
     * Send a clickable invite message with Accept and Decline buttons
     */
    private void sendClickableInvite(Player invitedPlayer, String teamName, String inviterName) {
        // Create the main message
        TextComponent message = new TextComponent(Utils.Color("&7&m                                                    "));
        invitedPlayer.spigot().sendMessage(message);
        
        TextComponent header = new TextComponent(Utils.Color("&6&lTeam Invitation"));
        invitedPlayer.spigot().sendMessage(header);
        
        TextComponent info = new TextComponent(Utils.Color("&eYou've been invited to join &6" + teamName + " &eby &6" + inviterName));
        invitedPlayer.spigot().sendMessage(info);
        
        // Create clickable buttons
        TextComponent buttons = new TextComponent("");
        
        // Accept button (green)
        TextComponent acceptButton = new TextComponent(Utils.Color("&a&l[✔ ACCEPT]"));
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team invite accept"));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(Utils.Color("&aClick to accept the invitation")).create()));
        
        // Space between buttons
        TextComponent space = new TextComponent("  ");
        
        // Decline button (red)
        TextComponent declineButton = new TextComponent(Utils.Color("&c&l[✖ DECLINE]"));
        declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team invite deny"));
        declineButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(Utils.Color("&cClick to decline the invitation")).create()));
        
        buttons.addExtra(acceptButton);
        buttons.addExtra(space);
        buttons.addExtra(declineButton);
        
        invitedPlayer.spigot().sendMessage(buttons);
        
        TextComponent footer = new TextComponent(Utils.Color("&7&m                                                    "));
        invitedPlayer.spigot().sendMessage(footer);
    }
}
