package dev.xf3d3.celestyteams.menuSystem.paginatedMenu;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.menuSystem.PlayerMenuUtility;
import dev.xf3d3.celestyteams.menuSystem.menu.TeamJoinRequestMenu;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class ClanListGUI {
    FileConfiguration guiConfig = CelestyTeams.getPlugin().teamGUIFileManager.getClanGUIConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();

    private final CelestyTeams plugin;

    public ClanListGUI(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        createGui(player);
    }

    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Team onlineTeamOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);
        Team onlineTeamPlayer = plugin.getTeamStorageUtil().findClanByPlayer(player);
        UUID target = UUID.fromString(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CelestyTeams.getPlugin(), "uuid"), PersistentDataType.STRING));
        if (onlineTeamOwner != null) {
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed-own-team")));
            return;
        }
        if (onlineTeamPlayer != null) {
            UUID ownerUUID = UUID.fromString(onlineTeamPlayer.getTeamOwner());
            if (ownerUUID.equals(target)) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed-own-team")));
                return;
            }
        }

        PlayerMenuUtility playerMenuUtility = CelestyTeams.getPlayerMenuUtility(player);
        playerMenuUtility.setOfflineClanOwner(Bukkit.getOfflinePlayer(target));
        new TeamJoinRequestMenu(CelestyTeams.getPlayerMenuUtility(player)).open();
    }

    public void createGui(Player player) {
        plugin.runAsync(() -> {

            PaginatedGui teams_gui = Gui.paginated()
                    .title(Component.text(ColorUtils.translateColorCodes(guiConfig.getString("team-join.name"))))
                    .rows(6)
                    .pageSize(45)
                    .create();

            // Previous item
            teams_gui.setItem(48, ItemBuilder.from(Material.STONE_BUTTON).setName(ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.previous-page-icon-name"))).asGuiItem(event -> teams_gui.previous()));
            // Next item
            teams_gui.setItem(50, ItemBuilder.from(Material.STONE_BUTTON).setName(ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.next-page-icon-name"))).asGuiItem(event -> teams_gui.next()));
            // Close item
            teams_gui.setItem(49, ItemBuilder.from(Material.BARRIER).setName(ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.close-go-back-icon-name"))).asGuiItem(event -> teams_gui.close(player)));

            //Pagination loop template
            for (Team team : plugin.getTeamStorageUtil().getClanList()) {
                String teamOwnerUUIDString = team.getTeamOwner();
                UUID ownerUUID = UUID.fromString(teamOwnerUUIDString);
                OfflinePlayer teamOwnerPlayer = Bukkit.getOfflinePlayer(ownerUUID);

                SkullBuilder ownerHead = ItemBuilder.skull()
                        .owner(getServer().getOfflinePlayer(ownerUUID))
                        .name(Component.text(ColorUtils.translateColorCodes(team.getTeamFinalName())));

                ArrayList<String> lore = new ArrayList<>();
                ArrayList<String> teamMembersList = team.getClanMembers();
                ArrayList<String> teamAlliesList = team.getClanAllies();
                ArrayList<String> teamEnemiesList = team.getClanEnemies();
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.header")));
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.prefix") + team.getTeamPrefix()));
                if (teamOwnerPlayer.isOnline()) {
                    lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.owner-online") + teamOwnerPlayer.getName()));
                } else {
                    lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.owner-offline") + teamOwnerPlayer.getName()));
                }
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.members")));
                for (String string : teamMembersList) {
                    UUID memberUUID = UUID.fromString(string);
                    OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
                    String offlineMemberName = member.getName();
                    lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineMemberName));
                    if (teamMembersList.size() >= 10){
                        int membersSize = teamMembersList.size() - 10;
                        lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + membersSize + "&r &3&omore!"));
                        break;
                    }
                }
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.allies")));
                for (String string : teamAlliesList) {
                    UUID allyUUID = UUID.fromString(string);
                    OfflinePlayer ally = Bukkit.getOfflinePlayer(allyUUID);
                    String offlineAllyName = ally.getName();
                    lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineAllyName));
                    if (teamAlliesList.size() >= 10){
                        int allySize = teamAlliesList.size() - 10;
                        lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + allySize + "&r &3&omore!"));
                        break;
                    }
                }
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.enemies")));
                for (String string : teamEnemiesList) {
                    UUID enemyUUID = UUID.fromString(string);
                    OfflinePlayer enemy = Bukkit.getOfflinePlayer(enemyUUID);
                    String offlineEnemyName = enemy.getName();
                    lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineEnemyName));
                    if (teamEnemiesList.size() >= 10){
                        int enemySize = teamEnemiesList.size() - 10;
                        lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + enemySize + "&r &3&omore!"));
                        break;
                    }
                }
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.footer-1")));
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.action")));
                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.footer-2")));

                ownerHead.setLore(lore);
                ownerHead.setNbt("uuid", team.getTeamOwner());

                teams_gui.addItem(ownerHead.asGuiItem(this::handleMenu));

                plugin.runSync(() -> teams_gui.open(player));
            }
        });
    }
}