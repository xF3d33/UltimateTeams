package dev.xf3d3.celestyteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanChatMessageSendEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.models.TeamPlayer;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

@CommandAlias("tc|teamchat")
public class TeamChatCommand extends BaseCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String TIME_LEFT = "%TIMELEFT%";

    Logger logger = CelestyTeams.getPlugin().getLogger();

    HashMap<UUID, Long> chatCoolDownTimer = new HashMap<>();

    private final CelestyTeams plugin;

    public TeamChatCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandCompletion("<message>")
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (!(teamsConfig.getBoolean("team-chat.enabled"))){
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }

            if (args.length < 1) {
                player.sendMessage(ColorUtils.translateColorCodes(
                        "&6CelestyTeams team chat usage:&3" +
                                "\n/cc <message>"
                ));

            }else {
                ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
                ArrayList<Player> playersWithSpyPerms = new ArrayList<>();
                for (Player p : onlinePlayers){
                    TeamPlayer teamPlayer = plugin.getUsersStorageUtil().getClanPlayerByBukkitPlayer(p);
                    if (teamPlayer.getCanChatSpy() && p.hasPermission("celestyteams.chat.spy")){
                        playersWithSpyPerms.add(p);
                    }
                }

                Team teamByMember = plugin.getTeamStorageUtil().findClanByPlayer(player);
                Team teamByOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);

                String chatSpyPrefix = teamsConfig.getString("team-chat.chat-spy.chat-spy-prefix");
                StringBuilder messageString = new StringBuilder();
                messageString.append(teamsConfig.getString("team-chat.chat-prefix")).append(" ");
                messageString.append("&d").append(player.getName()).append(":&r").append(" ");
                for (String arg : args){
                    messageString.append(arg).append(" ");
                }

                if (teamsConfig.getBoolean("team-chat.cool-down.enabled")){
                    if (chatCoolDownTimer.containsKey(uuid)){
                        if (!(player.hasPermission("celestyteams.bypass.chatcooldown")||player.hasPermission("celestyteams.bypass.*")
                                ||player.hasPermission("celestyteams.bypass")||player.hasPermission("celestyteams.*")||player.isOp())){
                            if (chatCoolDownTimer.get(uuid) > System.currentTimeMillis()) {

                                //If player still has time left on cool down
                                Long timeLeft = (chatCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-cool-down-timer-wait")
                                        .replace(TIME_LEFT, timeLeft.toString())));
                            }else {

                                //Add player to cool down and run message
                                chatCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-chat.cool-down.time") * 1000));
                                if (teamByMember != null) {
                                    ArrayList<String> playerClanMembers = teamByMember.getClanMembers();
                                    fireClanChatMessageSendEvent(player, teamByMember, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                                    }
                                    for (String playerClanMember : playerClanMembers) {
                                        if (playerClanMember != null) {
                                            UUID memberUUID = UUID.fromString(playerClanMember);
                                            UUID ownerUUID = UUID.fromString(teamByMember.getTeamOwner());
                                            Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                            Player playerClanOwner = Bukkit.getPlayer(ownerUUID);
                                            if (playerClanPlayer != null) {
                                                if (playerClanOwner != null) {
                                                    playerClanOwner.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                                }
                                                playerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                                if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                                    for (Player p : playersWithSpyPerms){
                                                        p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (teamByOwner != null){
                                    ArrayList<String> ownerClanMembers = teamByOwner.getClanMembers();
                                    fireClanChatMessageSendEvent(player, teamByOwner, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                                    }
                                    for (String ownerClanMember : ownerClanMembers){
                                        if (ownerClanMember != null){
                                            UUID memberUUID = UUID.fromString(ownerClanMember);
                                            Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);
                                            if (ownerClanPlayer != null){
                                                ownerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                                player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                                if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                                    for (Player p : playersWithSpyPerms){
                                                        p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-must-be-in-team")));
                                }
                            }
                        }else {

                            //If player has cool down bypass
                            if (teamByMember != null){
                                ArrayList<String> playerClanMembers = teamByMember.getClanMembers();
                                fireClanChatMessageSendEvent(player, teamByMember, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                                }
                                for (String playerClanMember : playerClanMembers){
                                    if (playerClanMember != null){
                                        UUID memberUUID = UUID.fromString(playerClanMember);
                                        UUID ownerUUID = UUID.fromString(teamByMember.getTeamOwner());
                                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                        Player playerClanOwner = Bukkit.getPlayer(ownerUUID);
                                        if (playerClanPlayer != null){
                                            if (playerClanOwner != null){
                                                playerClanOwner.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                            }
                                            playerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                            if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                                for (Player p : playersWithSpyPerms){
                                                    p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (teamByOwner != null){
                                ArrayList<String> ownerClanMembers = teamByOwner.getClanMembers();
                                fireClanChatMessageSendEvent(player, teamByOwner, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                                }
                                for (String ownerClanMember : ownerClanMembers){
                                    if (ownerClanMember != null){
                                        UUID memberUUID = UUID.fromString(ownerClanMember);
                                        Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);
                                        if (ownerClanPlayer != null){
                                            ownerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                            player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                            if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                                for (Player p : playersWithSpyPerms){
                                                    p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                                }
                                            }
                                        }
                                    }
                                }
                                player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-must-be-in-team")));
                            }
                        }
                    }else {

                        //If player not in cool down hashmap
                        chatCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-chat.cool-down.time") * 1000));
                        if (teamByMember != null){
                            ArrayList<String> playerClanMembers = teamByMember.getClanMembers();
                            fireClanChatMessageSendEvent(player, teamByMember, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                            }
                            for (String playerClanMember : playerClanMembers){
                                if (playerClanMember != null){
                                    UUID memberUUID = UUID.fromString(playerClanMember);
                                    UUID ownerUUID = UUID.fromString(teamByMember.getTeamOwner());
                                    Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                    Player playerClanOwner = Bukkit.getPlayer(ownerUUID);
                                    if (playerClanPlayer != null){
                                        if (playerClanOwner != null){
                                            playerClanOwner.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                        }
                                        playerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                        if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                            for (Player p : playersWithSpyPerms){
                                                p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (teamByOwner != null){
                            ArrayList<String> ownerClanMembers = teamByOwner.getClanMembers();
                            fireClanChatMessageSendEvent(player, teamByOwner, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                            }
                            for (String ownerClanMember : ownerClanMembers){
                                if (ownerClanMember != null){
                                    UUID memberUUID = UUID.fromString(ownerClanMember);
                                    Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);
                                    if (ownerClanPlayer != null){
                                        ownerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                        player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                        if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                            for (Player p : playersWithSpyPerms){
                                                p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                            }
                                        }
                                    }
                                }
                            }
                            player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-must-be-in-team")));
                        }
                    }
                }else {

                    //If cool down disabled
                    if (teamByMember != null){
                        ArrayList<String> playerClanMembers = teamByMember.getClanMembers();
                        fireClanChatMessageSendEvent(player, teamByMember, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                        }
                        for (String playerClanMember : playerClanMembers){
                            if (playerClanMember != null){
                                UUID memberUUID = UUID.fromString(playerClanMember);
                                UUID ownerUUID = UUID.fromString(teamByMember.getTeamOwner());
                                Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                Player playerClanOwner = Bukkit.getPlayer(ownerUUID);
                                if (playerClanPlayer != null){
                                    if (playerClanOwner != null){
                                        playerClanOwner.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                    }
                                    playerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                    if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                        for (Player p : playersWithSpyPerms){
                                            p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (teamByOwner != null){
                        ArrayList<String> ownerClanMembers = teamByOwner.getClanMembers();
                        fireClanChatMessageSendEvent(player, teamByOwner, teamsConfig.getString("team-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatMessageSendEvent"));
                        }
                        for (String ownerClanMember : ownerClanMembers){
                            if (ownerClanMember != null){
                                UUID memberUUID = UUID.fromString(ownerClanMember);
                                Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);
                                if (ownerClanPlayer != null){
                                    ownerClanPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                    player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                                    if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")){
                                        for (Player p : playersWithSpyPerms){
                                            p.sendMessage(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString.toString()));
                                        }
                                    }
                                }
                            }
                        }
                        player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-must-be-in-team")));
                    }
                }
            }

        }else {
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("player-only-command")));
        }
    }

    private static void fireClanChatMessageSendEvent(Player player, Team team, String prefix, String message, ArrayList<String> recipients) {
        ClanChatMessageSendEvent teamChatMessageSendEvent = new ClanChatMessageSendEvent(player, team, prefix, message, recipients);
        Bukkit.getPluginManager().callEvent(teamChatMessageSendEvent);
    }
}
