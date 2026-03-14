package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.*;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamMemberLeaveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MembersManager {

    private final UltimateTeams plugin;
    private final Player player;

    public MembersManager(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getMembers().getManager().getName())), plugin.getTeamsGui().getTeamManager().getMembers().getLayout().toArray(new String[0]));

        // Previous page
        gui.addElement(new GuiPageElement('f', new ItemStack(plugin.getTeamsGui().getGui().getPreviousPage().getMaterial()), GuiPageElement.PageAction.PREVIOUS, plugin.replacePlaceholders(player, plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getPreviousPage().getName()))));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(plugin.getTeamsGui().getGui().getNextPage().getMaterial()), GuiPageElement.PageAction.NEXT, plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getNextPage().getName())));

        // Back
        gui.addElement(
                new StaticGuiElement('b',
                        new ItemStack(plugin.getTeamsGui().getGui().getBackButton().getMaterial()),
                        1, // Display a number as the item count
                        click -> {
                            click.getGui().close();
                            new TeamManager(plugin, player);

                            return true;
                        },
                        plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getBackButton().getName())
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
                                        if (player.getName().equalsIgnoreCase(offlineMember.getName())) {
                                            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getKick().getFailedCannotKickYourself()));

                                            return true;
                                        }

                                        if (!(new TeamMemberLeaveEvent(memberUUID, team, TeamMemberLeaveEvent.LeaveReason.EVICTED).callEvent())) return true;

                                        plugin.getTeamStorageUtil().kickPlayer(player, team, offlineMember);

                                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getKick().getSuccessful().replace("%KICKEDPLAYER%", Objects.requireNonNullElse(offlineMember.getName(), "Player Not Found"))));

                                        click.getGui().close();
                                        new MembersManager(plugin, player);
                                    } else {
                                        player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                                    }
                                }

                                return true;
                            },
                            plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getMembers().getManager().getText())
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