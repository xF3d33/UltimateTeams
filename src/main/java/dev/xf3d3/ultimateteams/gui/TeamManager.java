package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamManager {

    private final UltimateTeams plugin;
    private final Player player;

    public TeamManager(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getName())), plugin.getTeamsGui().getTeamManager().getLayout().toArray(new String[0]));

        Optional<Team> OptionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (OptionalTeam.isEmpty())
            throw new IllegalStateException("Team not found");

        Team team = OptionalTeam.get();

        // Close
        gui.addElement(
                new StaticGuiElement('y',
                        new ItemStack(plugin.getTeamsGui().getGui().getCloseButton().getMaterial()),
                        1, // Display a number as the item count
                        click -> {
                            click.getGui().close();
                            return true;
                        },
                        plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getCloseButton().getName())
                ));


        // INFO
        gui.addElement(
                new StaticGuiElement('x',
                        new ItemStack(plugin.getTeamsGui().getTeamManager().getInfo().getMaterial()),
                        1, // Display a number as the item count
                        click -> true,
                        TeamList.getTeamInfo(plugin, team).toArray(new String[0])
                ));


        // HOME
        DynamicGuiElement home = new DynamicGuiElement('a', (viewer) -> new StaticGuiElement('a',
                new ItemStack(plugin.getTeamsGui().getTeamManager().getHome().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    // LEFT CLICK, TP TO HOME
                    if (click.getType().isLeftClick()) {
                        if (team.getHome() != null) {
                            plugin.getUtils().teleportPlayer(player, team.getHome().getLocation(), team.getHome().getServer(), Utils.TeleportType.HOME, null);
                        }

                        click.getGui().close();
                    }

                    // RIGHT CLICK, DELETE HOME
                    if (click.getType().isRightClick()) {
                        if (plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.HOME))) {

                            plugin.getTeamStorageUtil().deleteHome(player, team);
                            player.sendMessage(MineDown.parse(plugin.getMessages().getSuccessfullyDeletedTeamHome()));

                            click.getGui().draw();
                        } else player.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getHome().getText())
                        .stream()
                        .map(s -> s.replaceAll("%HOMESET%", plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).map(t -> team.getHome() != null ? "TRUE" : "FALSE").orElse("Team Not Found")))
                        .toArray(String[]::new)
        ));

        if (plugin.getSettings().getTeam().getHome().isEnabled()) {
            gui.addElement(home);
        }


        // TEAM WARPS
        StaticGuiElement warps = new StaticGuiElement('b',
                new ItemStack(plugin.getTeamsGui().getTeamManager().getWarps().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new WarpsManager(plugin, player);
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getWarps().getText()).toArray(new String[0])
        );

        if (plugin.getSettings().getTeam().getWarp().isEnable()) {
            gui.addElement(warps);
        }

        // TEAM MEMBERS
        gui.addElement(new StaticGuiElement('c',
                new ItemStack(plugin.getTeamsGui().getTeamManager().getMembers().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new MembersManager(plugin, player);
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getMembers().getText()).toArray(new String[0])
        ));

        // TEAM PVP
        DynamicGuiElement pvp = new DynamicGuiElement('d', (viewer) -> new StaticGuiElement('d',
                new ItemStack(plugin.getTeamsGui().getTeamManager().getPvp().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {
                        if (plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.PVP))) {

                            if (team.isFriendlyFireAllowed()){
                                team.setFriendlyFire(false);

                                player.sendMessage(MineDown.parse(plugin.getMessages().getDisabledFriendlyFire()));
                            } else {
                                team.setFriendlyFire(true);
                                player.sendMessage(MineDown.parse(plugin.getMessages().getEnabledFriendlyFire()));
                            }

                            plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                            click.getGui().draw();

                        } else player.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getPvp().getText())
                        .stream()
                        .map(s -> s.replaceAll("%ENABLED%", String.valueOf(plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).map(Team::isFriendlyFire).orElse(null)).toUpperCase()))
                        .toArray(String[]::new)
        ));

        if (plugin.getSettings().getTeam().getPvp().isEnabled()) {
            gui.addElement(pvp);
        }

        // TEAM ALLIES
        StaticGuiElement allies = new StaticGuiElement('e',
                new ItemStack(plugin.getTeamsGui().getTeamManager().getAllies().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new AlliesManager(plugin, player);
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getAllies().getText()).toArray(new String[0])
        );

        if (plugin.getSettings().getTeam().getAllies().isEnabled()) {
            gui.addElement(allies);
        }

        // TEAM ENEMIES
        StaticGuiElement enemies = new StaticGuiElement('h',
                new ItemStack(plugin.getTeamsGui().getTeamManager().getEnemies().getMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new EnemiesManager(plugin, player);
                    }

                    return true;
                },
                plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamManager().getEnemies().getText()).toArray(new String[0])
        );
        
        if (plugin.getSettings().getTeam().getEnemies().isEnabled()) {
            gui.addElement(enemies);
        }

        gui.show(player);
    }
}