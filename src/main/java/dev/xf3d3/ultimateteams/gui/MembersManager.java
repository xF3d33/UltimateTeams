package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MembersManager {
    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private final UltimateTeams plugin;
    private final Player player;

    public MembersManager(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        String[] guiSetup = {
                "         ",
                "  ggggg  ",
                "  ggggg  ",
                "  ggggg  ",
                "         ",
                "f   b   n"
        };
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.getTeamsGui().getMembersManagerGuiName()), guiSetup);

        // Previous page
        gui.addElement(new GuiPageElement('f', new ItemStack(plugin.getTeamsGui().getPreviousPageMaterial()), GuiPageElement.PageAction.PREVIOUS, plugin.getTeamsGui().getPreviousPage()));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(plugin.getTeamsGui().getNextPageMaterial()), GuiPageElement.PageAction.NEXT, plugin.getTeamsGui().getNextPage()));

        // Back
        gui.addElement(
                new StaticGuiElement('b',
                        new ItemStack(plugin.getTeamsGui().getBackButtonMaterial()),
                        1, // Display a number as the item count
                        click -> {
                            click.getGui().close();
                            new TeamManager(plugin, player);

                            return true;
                        },
                        plugin.getTeamsGui().getBackButtonName()
                ));



        GuiElementGroup group = new GuiElementGroup('g');
        Optional<Team> OptionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (OptionalTeam.isEmpty())
            throw new IllegalStateException("Team not found");

        Team team = OptionalTeam.get();
        Set<UUID> members = team.getMembers().keySet().stream().filter(member -> !member.equals(team.getOwner())).collect(Collectors.toSet());

        for (UUID memberUUID : members) {
            OfflinePlayer offlineMember = Bukkit.getOfflinePlayer(memberUUID);

            group.addElement(
                    new DynamicGuiElement('g', (viewer) -> new StaticGuiElement('g',
                            TeamList.createPlayerSkull(offlineMember),
                            1, // Display a number as the item count
                            click -> {
                                // RIGHT CLICK, KICK MEMBER
                                if (click.getType().isRightClick()) {
                                    if (plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.KICK))) {
                                        plugin.getTeamStorageUtil().kickPlayer(player, team, offlineMember);

                                        String playerKickedMessage = Utils.Color(messagesConfig.getString("team-member-kick-successful")).replace("%KICKEDPLAYER%", Objects.requireNonNullElse(offlineMember.getName(), "Player Not Found"));
                                        player.sendMessage(playerKickedMessage);

                                        click.getGui().close();
                                        new MembersManager(plugin, player);
                                    } else {
                                        player.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                                    }
                                }

                                return true;
                            },
                            plugin.getTeamsGui().getMembersManagerGuiText()
                                    .stream()
                                    .map(s -> s.replaceAll("%NAME%", Objects.requireNonNullElse(offlineMember.getName(), "Player Not Found")))
                                    .toArray(String[]::new)
                    )
            ));
        }

        gui.addElement(group);
        gui.show(player);
    }
}