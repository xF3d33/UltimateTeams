package dev.xf3d3.celestyteams.menuSystem;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class PaginatedMenu extends Menu {

    FileConfiguration guiConfig = CelestyTeams.getPlugin().teamGUIFileManager.getClanGUIConfig();

    protected int page = 0;
    protected int maxItemsPerPage = 45;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuControls(){
        inventory.setItem(48, makeItem(Material.STONE_BUTTON, ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.previous-page-icon-name"))));
        inventory.setItem(49, makeItem(Material.BARRIER, ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.close-go-back-icon-name"))));
        inventory.setItem(50, makeItem(Material.STONE_BUTTON, ColorUtils.translateColorCodes(guiConfig.getString("team-list.menu-controls.next-page-icon-name"))));
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
