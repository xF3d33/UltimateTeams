package dev.xf3d3.ultimateteams.config;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

// todo: use settings lib
public class TeamGUIFileManager {
    private final UltimateTeams plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public TeamGUIFileManager(UltimateTeams plugin){
        this.plugin = plugin;
        saveDefaultTeamGUIConfig();
    }

    public void reloadTeamGUIConfig() {
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

    public FileConfiguration getTeamGUIConfig() {
        if (this.dataConfig == null) {
            this.reloadTeamGUIConfig();
        }
        return this.dataConfig;
    }

    public void saveTeamGUIConfig() {
        if (this.dataConfig == null || this.configFile == null){
            return;
        }
        try {
            this.getTeamGUIConfig().save(this.configFile);
        } catch (IOException ex) {
            plugin.log(Level.SEVERE,"UltimateTeams: Could not save teamgui.yml");
            plugin.log(Level.SEVERE, "UltimateTeams: Check the below message for the reasons!", ex);

        }
    }

    public void saveDefaultTeamGUIConfig(){
        if (this.configFile == null) {
            this.configFile = new File(plugin.getDataFolder(), "teamgui.yml");
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource("teamgui.yml", false);
        }
    }
}
