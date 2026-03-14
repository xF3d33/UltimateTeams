package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TeamList {
    private final UltimateTeams plugin;
    private final Player player;

    public TeamList(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getName())), plugin.getTeamsGui().getTeamList().getLayout().toArray(new String[0]));


        // Previous page
        gui.addElement(new GuiPageElement('f', new ItemStack(plugin.getTeamsGui().getGui().getPreviousPage().getMaterial()), GuiPageElement.PageAction.PREVIOUS, plugin.replacePlaceholders(player, plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getPreviousPage().getName()))));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(plugin.getTeamsGui().getGui().getNextPage().getMaterial()), GuiPageElement.PageAction.NEXT, plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getNextPage().getName())));

        // Close
        gui.addElement(
                new StaticGuiElement('c',
                        new ItemStack(plugin.getTeamsGui().getGui().getCloseButton().getMaterial()),
                        1, // Display a number as the item count
                        click -> {
                            click.getGui().close();
                            return true;
                        },
                        plugin.replacePlaceholders(player, plugin.getTeamsGui().getGui().getCloseButton().getName())
                ));



        // Teams
        GuiElementGroup group = new GuiElementGroup('g');
        for (Team team : plugin.getTeamStorageUtil().getTeams()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(team.getOwner());

            group.addElement((new StaticGuiElement('e', createPlayerSkull(player), getTeamInfo(plugin, team).toArray(new String[0]))));

        }
        gui.addElement(group);

        gui.show(player);
    }

    public static ArrayList<String> getTeamInfo(UltimateTeams plugin, Team team) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(team.getOwner());

        final Map<Team, Team.Relation> relations = team.getRelations(plugin);

        final Map<Team, Team.Relation> allies = relations.entrySet().stream()
                .filter(entry -> entry.getValue() == Team.Relation.ALLY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Map<Team, Team.Relation> enemies = relations.entrySet().stream()
                .filter(entry -> entry.getValue() == Team.Relation.ENEMY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Set<UUID> teamMembers = team.getMembers().entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        final Set<UUID> teamManagers = team.getMembers().entrySet().stream()
                .filter(entry -> entry.getValue() == 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        ArrayList<String> lore = new ArrayList<>();

        lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("header"))));

        // Team name
        lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("team-name") + team.getName())));

        // Owner
        if (player.isOnline()) {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("owner-online") + player.getName())));
        } else {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("owner-offline") + player.getName())));
        }

        // Members
        if (!team.getMembers().isEmpty()) {

            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("members"))));
            for (UUID teamMember : teamMembers) {
                String offlinePlayer = Bukkit.getOfflinePlayer(teamMember).getName();

                lore.add(Utils.Color(plugin.replacePlaceholders(player, offlinePlayer != null ? (plugin.getTeamsGui().getTeamList().getIcons().getLore().get("members-color") +  offlinePlayer) : (plugin.getTeamsGui().getTeamList().getIcons().getLore().get("members-color") + "&rplayer not found" ))));
            }

            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("managers"))));
            for (UUID teamMember : teamManagers) {
                String offlinePlayer = Bukkit.getOfflinePlayer(teamMember).getName();

                lore.add(Utils.Color(plugin.replacePlaceholders(player, offlinePlayer != null ? (plugin.getTeamsGui().getTeamList().getIcons().getLore().get("managers-color") +  offlinePlayer) : (plugin.getTeamsGui().getTeamList().getIcons().getLore().get("managers-color") + "&rplayer not found" ))));
            }
        }

        // Allies
        if (!allies.isEmpty()) {

            lore.add(" ");
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("allies"))));

            allies.keySet().forEach(
                    t -> lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("allies-color") + t.getName())))
            );

        }

        if (!enemies.isEmpty()) {
            lore.add(" ");
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("enemies"))));

            enemies.keySet().forEach(
                    t -> lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("enemies-color") + t.getName())))
            );
        }

        lore.add(" ");
        lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("prefix") + "&r&f" + (team.getPrefix() != null ? team.getPrefix() : ""))));

        if (plugin.getSettings().getEconomy().isEnable()) {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getMessages().getTeam().getInfo().getBankAmount()
                    .replace("%AMOUNT%", String.format("%.2f", team.getBalance())))
            ));

            if (plugin.getSettings().getEconomy().getTeamJoinFee().isEnabled()) {
                lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getMessages().getTeam().getInfo().getMotd().replace("%MOTD%", plugin.getMessages().getTeam().getInfo().getJoinFee()
                        .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                ))));
            }
        }

        if (plugin.getSettings().getTeam().getMotd().isEnable()) {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getMessages().getTeam().getInfo().getMotd().replace("%MOTD%", Objects.requireNonNullElse(Utils.Color(team.getMotd()), plugin.getMessages().getTeam().getMotd().getNotSet())))));
        }

        if (team.isFriendlyFire()) {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("pvp") + " &cTRUE")));
        } else {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("pvp") + " &cFALSE")));
        }

        if (plugin.getTeamStorageUtil().isHomeSet(team)) {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("home") + " &cTRUE")));
        } else {
            lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("home") + " &cFALSE")));
        }

        lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("footer-1"))));
        lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("action"))));
        lore.add(Utils.Color(plugin.replacePlaceholders(player, plugin.getTeamsGui().getTeamList().getIcons().getLore().get("footer-2"))));

        return lore;
    }

    public static ItemStack createPlayerSkull(OfflinePlayer owner) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setOwningPlayer(owner);
            skull.setItemMeta(skullMeta);
        }

        return skull;
    }
}