package dev.xf3d3.ultimateteams.config;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class MessagesFileManager {

    private final UltimateTeams plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public MessagesFileManager(UltimateTeams plugin) {
        this.plugin = plugin;
        saveDefaultMessagesConfig();
    }

    public void reloadMessagesConfig() {
        if (this.configFile == null) {
            this.configFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getMessagesConfig() {
        if (this.dataConfig == null) {
            this.reloadMessagesConfig();
        }
        return this.dataConfig;
    }

    public void saveMessagesConfig() {
        if (this.dataConfig == null || this.configFile == null) {
            return;
        }
        try {
            this.getMessagesConfig().save(this.configFile);
        } catch (IOException ex) {
            plugin.log(Level.SEVERE,"UltimateTeams: Could not save messages.yml");
            plugin.log(Level.SEVERE,"UltimateTeams: Check the below message for the reasons!", ex);
        }
    }

    public void saveDefaultMessagesConfig() {
        if (this.configFile == null) {
            this.configFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }
    }
}