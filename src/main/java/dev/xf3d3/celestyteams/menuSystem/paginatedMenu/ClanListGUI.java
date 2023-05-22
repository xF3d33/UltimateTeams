package dev.xf3d3.celestyteams.menuSystem.paginatedMenu;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.WrappedTask;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.menuSystem.PaginatedMenu;
import dev.xf3d3.celestyteams.menuSystem.PlayerMenuUtility;
import dev.xf3d3.celestyteams.menuSystem.menu.TeamJoinRequestMenu;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
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

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class ClanListGUI extends PaginatedMenu {

    public static WrappedTask task5;

    FileConfiguration guiConfig = CelestyTeams.getPlugin().teamGUIFileManager.getClanGUIConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();

    private CelestyTeams plugin;

    public ClanListGUI(@NotNull CelestyTeams plugin, @NotNull PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return ColorUtils.translateColorCodes(guiConfig.getString("team-list.name"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ArrayList<Team> teams = new ArrayList<>(plugin.getTeamStorageUtil().getClanList());
        if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)){
            Team onlineTeamOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);
            Team onlineTeamPlayer = plugin.getTeamStorageUtil().findClanByPlayer(player);
            UUID target = UUID.fromString(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CelestyTeams.getPlugin(), "uuid"), PersistentDataType.STRING));
            if (onlineTeamOwner != null){
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed-own-team")));
                return;
            }
            if (onlineTeamPlayer != null){
                UUID ownerUUID = UUID.fromString(onlineTeamPlayer.getTeamOwner());
                if (ownerUUID.equals(target)){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed-own-team")));
                    return;
                }
            }
            PlayerMenuUtility playerMenuUtility = CelestyTeams.getPlayerMenuUtility(player);
            playerMenuUtility.setOfflineClanOwner(Bukkit.getOfflinePlayer(target));
            new TeamJoinRequestMenu(CelestyTeams.getPlayerMenuUtility(player)).open();
            if (guiConfig.getBoolean("team-list.icons.auto-refresh-data.enabled")){
                task5.cancel();
                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aAuto refresh task cancelled"));
                }
            }
        }else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
            player.closeInventory();
            if (guiConfig.getBoolean("team-list.icons.auto-refresh-data.enabled")){
                task5.cancel();
                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aAuto refresh task cancelled"));
                }
            }
        }else if(event.getCurrentItem().getType().equals(Material.STONE_BUTTON)){
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.previous-page-icon-name")))){
                if (page == 0){
                    player.sendMessage(ColorUtils.translateColorCodes(guiConfig.getString("team-list.GUI-first-page")));
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.next-page-icon-name")))){
                if (!((index + 1) >= teams.size())){
                    page = page + 1;
                    super.open();
                }else{
                    player.sendMessage(ColorUtils.translateColorCodes(guiConfig.getString("team-list.GUI-last-page")));
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuControls();
        if (guiConfig.getBoolean("team-list.icons.auto-refresh-data.enabled")){
            FoliaLib foliaLib = new FoliaLib(CelestyTeams.getPlugin());
            task5 = foliaLib.getImpl().runTimerAsync(new Runnable() {
                @Override
                public void run() {
                    //The thing you will be looping through to place items
                    ArrayList<Team> teams = new ArrayList<>(plugin.getTeamStorageUtil().getClanList());

                    //Pagination loop template
                    if (teams != null && !teams.isEmpty()){
                        for (int i = 0; i < getMaxItemsPerPage(); i++){
                            index = getMaxItemsPerPage() * page + i;
                            if (index >= teams.size()) break;
                            if (teams.get(index) != null) {

                                //Create an item from our collection and place it into the inventory
                                String teamOwnerUUIDString = teams.get(i).getTeamOwner();
                                UUID ownerUUID = UUID.fromString(teamOwnerUUIDString);
                                OfflinePlayer teamOwnerPlayer = Bukkit.getOfflinePlayer(ownerUUID);
                                Team team = plugin.getTeamStorageUtil().findTeamByOfflineOwner(teamOwnerPlayer);

                                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                                SkullMeta skull = (SkullMeta) playerHead.getItemMeta();
                                skull.setOwningPlayer(getServer().getOfflinePlayer(ownerUUID));
                                playerHead.setItemMeta(skull);
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aRetrieved player head info for UUID: &d" + teamOwnerUUIDString));
                                }

                                ItemMeta meta = playerHead.getItemMeta();
                                if (guiConfig.getBoolean("team-list.icons.icon-display-name.use-team-name")){
                                    String displayName = ColorUtils.translateColorCodes(team.getTeamFinalName());
                                    meta.setDisplayName(displayName);
                                }else {
                                    meta.setDisplayName(" ");
                                }


                                ArrayList<String> lore = new ArrayList<>();
                                ArrayList<String> teamMembersList = team.getClanMembers();
                                ArrayList<String> teamAlliesList = team.getClanAllies();
                                ArrayList<String> teamEnemiesList = team.getClanEnemies();
                                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.header")));
                                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.prefix") + team.getTeamPrefix()));
                                if (teamOwnerPlayer.isOnline()){
                                    lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.owner-online") + teamOwnerPlayer.getName()));
                                }else {
                                    lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.owner-offline") + teamOwnerPlayer.getName()));
                                }
                                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.members")));
                                for (String string : teamMembersList){
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
                                for (String string : teamAlliesList){
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
                                for (String string : teamEnemiesList){
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

                                meta.setLore(lore);
                                meta.getPersistentDataContainer().set(new NamespacedKey(CelestyTeams.getPlugin(), "uuid"), PersistentDataType.STRING, team.getTeamOwner());

                                playerHead.setItemMeta(meta);

                                inventory.setItem(index, playerHead);
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aAuto refresh task running"));
                                }
                            }
                        }
                    }
                }
            }, 0L, 5L, TimeUnit.SECONDS);
        }else {
            //The thing you will be looping through to place items
            ArrayList<Team> teams = new ArrayList<>(plugin.getTeamStorageUtil().getClanList());

            //Pagination loop template
            if (teams != null && !teams.isEmpty()){
                for (int i = 0; i < getMaxItemsPerPage(); i++){
                    index = getMaxItemsPerPage() * page + i;
                    if (index >= teams.size()) break;
                    if (teams.get(index) != null) {

                        //Create an item from our collection and place it into the inventory
                        String teamOwnerUUIDString = teams.get(i).getTeamOwner();
                        UUID ownerUUID = UUID.fromString(teamOwnerUUIDString);
                        OfflinePlayer teamOwnerPlayer = Bukkit.getOfflinePlayer(ownerUUID);
                        Team team = plugin.getTeamStorageUtil().findTeamByOfflineOwner(teamOwnerPlayer);

                        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta skull = (SkullMeta) playerHead.getItemMeta();
                        skull.setOwningPlayer(getServer().getOfflinePlayer(ownerUUID));
                        playerHead.setItemMeta(skull);
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aRetrieved player head info for UUID: &d" + teamOwnerUUIDString));
                        }

                        ItemMeta meta = playerHead.getItemMeta();
                        String displayName = ColorUtils.translateColorCodes(team.getTeamFinalName());
                        meta.setDisplayName(displayName);

                        ArrayList<String> lore = new ArrayList<>();
                        ArrayList<String> teamMembersList = team.getClanMembers();
                        ArrayList<String> teamAlliesList = team.getClanAllies();
                        ArrayList<String> teamEnemiesList = team.getClanEnemies();
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.header")));
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.prefix") + team.getTeamPrefix()));
                        if (teamOwnerPlayer.isOnline()){
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.owner-online") + teamOwnerPlayer.getName()));
                        }else {
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.owner-offline") + teamOwnerPlayer.getName()));
                        }
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("team-list.icons.lore.members")));
                        for (String string : teamMembersList){
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
                        for (String string : teamAlliesList){
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
                        for (String string : teamEnemiesList){
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

                        meta.setLore(lore);
                        meta.getPersistentDataContainer().set(new NamespacedKey(CelestyTeams.getPlugin(), "uuid"), PersistentDataType.STRING, team.getTeamOwner());

                        playerHead.setItemMeta(meta);

                        inventory.addItem(playerHead);
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aAuto refresh task not running"));
                        }
                    }
                }
            }
        }
    }
}
