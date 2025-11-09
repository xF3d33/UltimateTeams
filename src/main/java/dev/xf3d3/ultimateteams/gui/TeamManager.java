package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
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
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.getTeamsGui().getTeamsManagerGuiName()), plugin.getTeamsGui().getTeamsManagerguiSetup().toArray(new String[0]));

        Optional<Team> OptionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (OptionalTeam.isEmpty())
            throw new IllegalStateException("Team not found");

        Team team = OptionalTeam.get();

        // Close
        gui.addElement(
                new StaticGuiElement('y',
                        new ItemStack(plugin.getTeamsGui().getCloseButtonMaterial()),
                        1, // Display a number as the item count
                        click -> {
                            click.getGui().close();
                            return true;
                        },
                        plugin.getTeamsGui().getCloseButtonName()
                ));


        // INFO
        gui.addElement(
                new StaticGuiElement('x',
                        new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiInfoMaterial()),
                        1, // Display a number as the item count
                        click -> true,
                        TeamList.getTeamInfo(plugin, team).toArray(new String[0])
                ));


        // HOME
        DynamicGuiElement home = new DynamicGuiElement('a', (viewer) -> new StaticGuiElement('a',
                new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiHomeMaterial()),
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
                plugin.getTeamsGui().getTeamsManagerGuiHomeText()
                        .stream()
                        .map(s -> s.replaceAll("%HOMESET%", plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).map(t -> team.getHome() != null ? "TRUE" : "FALSE").orElse("Team Not Found")))
                        .toArray(String[]::new)
        ));

        if (plugin.getSettings().teamHomeEnabled()) {
            gui.addElement(home);
        }


        // TEAM WARPS
        StaticGuiElement warps = new StaticGuiElement('b',
                new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiWarpsMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new WarpsManager(plugin, player);
                    }

                    return true;
                },
                plugin.getTeamsGui().getTeamsManagerGuiWarpsText().toArray(new String[0])
        );

        if (plugin.getSettings().teamWarpEnabled()) {
            gui.addElement(warps);
        }

        // TEAM MEMBERS
        gui.addElement(new StaticGuiElement('c',
                new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiMembersMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new MembersManager(plugin, player);
                    }

                    return true;
                },
                plugin.getTeamsGui().getTeamsManagerGuiMembersText().toArray(new String[0])
        ));

        // TEAM PVP
        DynamicGuiElement pvp = new DynamicGuiElement('d', (viewer) -> new StaticGuiElement('d',
                new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiPvpMaterial()),
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
                plugin.getTeamsGui().getTeamsManagerGuiPvpText()
                        .stream()
                        .map(s -> s.replaceAll("%ENABLED%", String.valueOf(plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).map(Team::isFriendlyFire).orElse(null)).toUpperCase()))
                        .toArray(String[]::new)
        ));

        if (plugin.getSettings().isPvpCommandEnabled()) {
            gui.addElement(pvp);
        }

        // TEAM ALLIES
        gui.addElement(new StaticGuiElement('e',
                new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiAlliesMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new AlliesManager(plugin, player);
                    }

                    return true;
                },
                plugin.getTeamsGui().getTeamsManagerGuiAlliesText().toArray(new String[0])
        ));

        // TEAM ENEMIES
        gui.addElement(new StaticGuiElement('h',
                new ItemStack(plugin.getTeamsGui().getTeamsManagerGuiEnemiesMaterial()),
                1, // Display a number as the item count
                click -> {
                    if (click.getType().isLeftClick()) {

                        click.getGui().close();
                        new EnemiesManager(plugin, player);
                    }

                    return true;
                },
                plugin.getTeamsGui().getTeamsManagerGuiEnemiesText().toArray(new String[0])
        ));

        gui.show(player);
    }
}