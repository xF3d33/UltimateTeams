package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultHook {

    private final Economy economyAPI;
    private final UltimateTeams plugin;

    public VaultHook(@NotNull UltimateTeams plugin) {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new IllegalStateException("Could not resolve economy provider");
        }

        this.economyAPI = economyProvider.getProvider();
        this.plugin = plugin;
        sendMessages();
    }

    public boolean hasMoney(@NotNull Player player, @NotNull Double amount) {
        return economyAPI.has(player, amount);
    }

    public boolean takeMoney(@NotNull Player player, @NotNull Double amount) {
        return economyAPI.withdrawPlayer(player, amount).transactionSuccess();
    }

    public void giveMoney(@NotNull Player player, @NotNull Double amount) {
        economyAPI.depositPlayer(player, amount);
    }

    private void sendMessages() {
        plugin.sendConsole("-------------------------------------------");
        plugin.sendConsole("&6UltimateTeams: &3Hooked into Vault");
        plugin.sendConsole("&6UltimateTeams: &3Economy features enabled!");
        plugin.sendConsole("-------------------------------------------");
    }
}