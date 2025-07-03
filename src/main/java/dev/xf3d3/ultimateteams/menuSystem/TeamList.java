package dev.xf3d3.ultimateteams.menuSystem;

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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
        String[] guiSetup = {
                "         ",
                "  ggggg  ",
                "  ggggg  ",
                "  ggggg  ",
                "         ",
                " f    n  "
        };
        final InventoryGui gui = new InventoryGui(plugin, player, "Teams", guiSetup);


        // Previous page
        gui.addElement(new GuiPageElement('p', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

        // Teams
        GuiElementGroup group = new GuiElementGroup('g');
        for (Team team : plugin.getTeamStorageUtil().getTeams()) {
            OfflinePlayer teamOwnerPlayer = Bukkit.getOfflinePlayer(team.getOwner());

            final Map<Team, Team.Relation> relations = team.getRelations(plugin);

            final Map<Team, Team.Relation> allies = relations.entrySet().stream()
                    .filter(entry -> entry.getValue() == Team.Relation.ALLY)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            final Map<Team, Team.Relation> enemies = relations.entrySet().stream()
                    .filter(entry -> entry.getValue() == Team.Relation.ENEMY)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            ArrayList<String> lore = new ArrayList<>();

            lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("header")));

            // Owner
            if (teamOwnerPlayer.isOnline()) {
                lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("owner-online") + teamOwnerPlayer.getName()));
            } else {
                lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("owner-offline") + teamOwnerPlayer.getName()));
            }

            // Members
            if (!team.getMembers().isEmpty()) {
                Set<UUID> members = team.getMembers().keySet().stream().filter(member -> !member.equals(team.getOwner())).collect(Collectors.toSet());

                for (UUID teamMember : members) {
                    String offlinePlayer = Bukkit.getOfflinePlayer(teamMember).getName();

                    lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("members")));
                    lore.add(offlinePlayer != null ? ("&r" +  offlinePlayer) : "&rplayer not found");
                }
            }

            // Allies
            if (!allies.isEmpty()) {

                lore.add(" ");
                lore.add(Utils.Color(Utils.Color(plugin.getTeamsGui().getLoreMap().get("allies"))));

                allies.keySet().forEach(
                        t -> lore.add("&r" + t.getName())
                );

            }

            if (!enemies.isEmpty()) {
                lore.add(" ");
                lore.add(Utils.Color(Utils.Color(plugin.getTeamsGui().getLoreMap().get("enemies"))));

                enemies.keySet().forEach(
                        t -> lore.add("&r" + t.getName())
                );
            }

            lore.add(" ");
            lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("prefix") + (team.getPrefix() != null ? team.getPrefix() : "")));
            if (team.isFriendlyFire()) {
                lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("pvp") + " &cTRUE"));
            } else {
                lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("pvp") + " &cFALSE"));
            }

            if (plugin.getTeamStorageUtil().isHomeSet(team)) {
                lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("home") + " &cTRUE"));
            } else {
                lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("pvp") + " &cFALSE"));
            }

            lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("footer-1")));
            lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("action")));
            lore.add(Utils.Color(plugin.getTeamsGui().getLoreMap().get("footer-2")));

            group.addElement((new StaticGuiElement('e', createPlayerSkull(teamOwnerPlayer), lore.toArray(new String[0]))));

        }
        gui.addElement(group);

        gui.show(player);
    }

    public ItemStack createPlayerSkull(OfflinePlayer owner) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (skullMeta != null) {

            skullMeta.setOwningPlayer(owner);
            skull.setItemMeta(skullMeta);
        }

        return skull;
    }
}