package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.*;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WarpsManager {

    private final UltimateTeams plugin;
    private final Player player;

    public WarpsManager(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getWarps().getManager().getName())), plugin.getTeamsGui().getTeamManager().getWarps().getLayout().toArray(new String[0]));

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

        for (TeamWarp warp : team.getWarps().values()) {
            group.addElement(
                    new DynamicGuiElement('g', (viewer) -> new StaticGuiElement('g',
                            new ItemStack(plugin.getTeamsGui().getTeamManager().getWarps().getMaterial()),
                            1, // Display a number as the item count
                            click -> {
                                // LEFT CLICK, TP TO WARP
                                if (click.getType().isLeftClick()) {
                                    plugin.getUtils().teleportPlayer(player, warp.getLocation(), warp.getServer() == null ? null : warp.getServer(), Utils.TeleportType.WARP, warp.getName());

                                    click.getGui().close();
                                }

                                // RIGHT CLICK, DELETE WARP
                                if (click.getType().isRightClick()) {
                                    if (plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.WARPS))) {
                                        team.removeTeamWarp(warp.getName());
                                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpDeletedSuccessful().replaceAll("%WARP_NAME%", warp.getName())));

                                        click.getGui().close();
                                        new WarpsManager(plugin, player);
                                    } else {
                                        player.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                                    }
                                }

                                return true;
                            },
                            plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getWarps().getManager().getText())
                                    .stream()
                                    .map(s -> s.replaceAll("%WARP%", warp.getName()))
                                    .toArray(String[]::new)
                    )
            ));
        }

        gui.addElement(group);
        gui.show(player);
    }
}