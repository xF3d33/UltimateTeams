package dev.xf3d3.celestyteams.listeners;

import com.tcoded.folialib.wrapper.WrappedTask;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.menuSystem.Menu;
import dev.xf3d3.celestyteams.menuSystem.paginatedMenu.ClanListGUI;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.logging.Logger;

public class MenuEvent implements Listener {

    FileConfiguration guiConfig = CelestyTeams.getPlugin().teamGUIFileManager.getClanGUIConfig();
    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu){
            if (((Menu) holder).getMenuName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("team-list.name")))){
                WrappedTask wrappedTask = ClanListGUI.task5;
                if (!wrappedTask.isCancelled()){
                    wrappedTask.cancel();
                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aAuto refresh task cancelled"));
                    }
                }
            }
        }
    }
}
