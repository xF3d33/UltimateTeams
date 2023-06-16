package dev.xf3d3.ultimateteams.menuSystem;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class TeamListGUI {
    private final FileConfiguration guiConfig;
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamListGUI(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.guiConfig = plugin.teamGUIFileManager.getClanGUIConfig();
    }

    public void open(Player player) {
        createGui(player);
    }

    public void handleMenu(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        Team playerTeam;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            playerTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            playerTeam = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        if (playerTeam != null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-failed-already-in-team")));
            return;
        }

        UUID target = UUID.fromString(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "uuid"), PersistentDataType.STRING));

        /*PlayerMenuUtility playerMenuUtility = plugin.getPlayerMenuUtility(player);
        playerMenuUtility.setOfflineClanOwner(Bukkit.getOfflinePlayer(target));
        new TeamJoinRequestMenu(plugin.getPlayerMenuUtility(player)).open();*/
    }

    public void createGui(Player player) {
        plugin.runAsync(() -> {

            PaginatedGui teams_gui = Gui.paginated()
                    .title(Component.text(Utils.Color(guiConfig.getString("team-join.name"))))
                    .rows(6)
                    .pageSize(45)
                    .create();

            // Cancel click event by default
            teams_gui.setDefaultClickAction(event -> event.setCancelled(true));

            // Previous item
            teams_gui.setItem(48, ItemBuilder.from(Material.STONE_BUTTON).setName(Utils.Color(guiConfig.getString("team-list.menu-controls.previous-page-icon-name"))).asGuiItem(event -> teams_gui.previous()));
            // Next item
            teams_gui.setItem(50, ItemBuilder.from(Material.STONE_BUTTON).setName(Utils.Color(guiConfig.getString("team-list.menu-controls.next-page-icon-name"))).asGuiItem(event -> teams_gui.next()));
            // Close item
            teams_gui.setItem(49, ItemBuilder.from(Material.BARRIER).setName(Utils.Color(guiConfig.getString("team-list.menu-controls.close-go-back-icon-name"))).asGuiItem(event -> teams_gui.close(event.getWhoClicked())));

            //Pagination loop template
            for (Team team : plugin.getTeamStorageUtil().getTeamsList()) {
                String teamOwnerUUIDString = team.getTeamOwner();
                UUID ownerUUID = UUID.fromString(teamOwnerUUIDString);
                OfflinePlayer teamOwnerPlayer = Bukkit.getOfflinePlayer(ownerUUID);

                SkullBuilder ownerHead = ItemBuilder.skull()
                        .owner(getServer().getOfflinePlayer(ownerUUID))
                        .name(Component.text(Utils.Color(team.getTeamFinalName())));

                ArrayList<String> teamMembersList = team.getTeamMembers();
                ArrayList<String> teamAlliesList = team.getTeamAllies();
                ArrayList<String> teamEnemiesList = team.getTeamEnemies();

                ArrayList<String> lore = new ArrayList<>();
                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.header")));
                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.prefix") + team.getTeamPrefix()));
                if (teamOwnerPlayer.isOnline()) {
                    lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.owner-online") + teamOwnerPlayer.getName()));
                } else {
                    lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.owner-offline") + teamOwnerPlayer.getName()));
                }

                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.members")));
                for (String string : teamMembersList) {
                    UUID memberUUID = UUID.fromString(string);
                    OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
                    String offlineMemberName = member.getName();
                    lore.add(Utils.Color(" &7- &3" + offlineMemberName));
                    if (teamMembersList.size() >= 10){
                        int membersSize = teamMembersList.size() - 10;
                        lore.add(Utils.Color("&3&o+ &r&6&l" + membersSize + "&r &3&omore!"));
                        break;
                    }
                }

                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.allies")));
                for (String string : teamAlliesList) {
                    UUID allyUUID = UUID.fromString(string);
                    OfflinePlayer ally = Bukkit.getOfflinePlayer(allyUUID);
                    String offlineAllyName = ally.getName();
                    lore.add(Utils.Color(" &7- &3" + offlineAllyName));
                    if (teamAlliesList.size() >= 10){
                        int allySize = teamAlliesList.size() - 10;
                        lore.add(Utils.Color("&3&o+ &r&6&l" + allySize + "&r &3&omore!"));
                        break;
                    }
                }

                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.enemies")));
                for (String string : teamEnemiesList) {
                    UUID enemyUUID = UUID.fromString(string);
                    OfflinePlayer enemy = Bukkit.getOfflinePlayer(enemyUUID);
                    String offlineEnemyName = enemy.getName();
                    lore.add(Utils.Color(" &7- &3" + offlineEnemyName));
                    if (teamEnemiesList.size() >= 10){
                        int enemySize = teamEnemiesList.size() - 10;
                        lore.add(Utils.Color("&3&o+ &r&6&l" + enemySize + "&r &3&omore!"));
                        break;
                    }
                }

                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.footer-1")));
                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.action")));
                lore.add(Utils.Color(guiConfig.getString("team-list.icons.lore.footer-2")));

                ownerHead.setLore(lore);
                ownerHead.setNbt("uuid", team.getTeamOwner());
                ownerHead.asGuiItem(event -> teams_gui.close(event.getWhoClicked()));

                teams_gui.addItem(ownerHead.asGuiItem(this::handleMenu));

                plugin.runSync(() -> teams_gui.open(player));
            }
        });
    }
}