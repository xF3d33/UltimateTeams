package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NotInTeamManager {

    private final UltimateTeams plugin;
    private final Player player;

    public NotInTeamManager(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getNotInTeam().getName())), plugin.getTeamsGui().getNotInTeam().getLayout().toArray(new String[0]));

        Optional<Team> optionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (optionalTeam.isPresent())
            throw new IllegalStateException("Player is already in a team");

        // Close
        gui.addElement(
                new StaticGuiElement('y',
                        new ItemStack(plugin.getTeamsGui().getGui().getCloseButton().getMaterial()),
                        1, // Display a number as the item count
                        click -> {

                            click.getGui().close();
                            return true;
                        },
                        plugin.getTeamsGui().getGui().getCloseButton().getName()
                ));

        // CREATE TEAM
        gui.addElement(new StaticGuiElement('+',
                new ItemStack(plugin.getTeamsGui().getNotInTeam().getCreateTeam().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        player.performCommand("team create");
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getNotInTeam().getCreateTeam().getText()).toArray(new String[0])
        ));

        // TEAM LIST
        gui.addElement(new StaticGuiElement('i',
                new ItemStack(plugin.getTeamsGui().getNotInTeam().getTeamListButton().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new TeamList(plugin, player);
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getNotInTeam().getTeamListButton().getText()).toArray(new String[0])
        ));

        gui.show(player);
    }
}