package dev.xf3d3.ultimateteams.files;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class TeamGUIFileManager {
    private final UltimateTeams plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final Logger logger;

    public TeamGUIFileManager(UltimateTeams plugin){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        saveDefaultClanGUIConfig();
    }

    public void reloadClanGUIConfig() {
        if (this.configFile == null){
            this.configFile = new File(plugin.getDataFolder(), "teamgui.yml");
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource("teamgui.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getClanGUIConfig() {
        if (this.dataConfig == null) {
            this.reloadClanGUIConfig();
        }
        return this.dataConfig;
    }

    public void saveClanGUIConfig() {
        if (this.dataConfig == null || this.configFile == null){
            return;
        }
        try {
            this.getClanGUIConfig().save(this.configFile);
        } catch (IOException e) {
            logger.severe(Utils.Color("&6UltimateTeams: &4Could not save teamgui.yml"));
            logger.severe(Utils.Color("&6UltimateTeams: &4Check the below message for the reasons!"));
            e.printStackTrace();
        }
    }

    public void saveDefaultClanGUIConfig(){
        if (this.configFile == null) {
            this.configFile = new File(plugin.getDataFolder(), "teamgui.yml");
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource("teamgui.yml", false);
        }
    }
}
