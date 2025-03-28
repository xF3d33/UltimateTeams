package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.LuckPermsContexts.TeamContext;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.*;
import net.william278.huskhomes.api.HuskHomesAPI;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

public class LuckPermsHook {
    private final UltimateTeams plugin;

    private ContextManager contexts;


    public LuckPermsHook(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;

        registerServiceProvider();
    }

    private void registerServiceProvider() {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            throw new IllegalStateException("Could not resolve LuckPerms provider");
        }

        final LuckPerms api = provider.getProvider();
        this.contexts = api.getContextManager();

        this.registerCalculator(() -> new TeamContext(plugin));

        sendMessages();
    }

    private void registerCalculator(final Supplier<ContextCalculator<Player>> calculatorSupplier) {
        final ContextCalculator<Player> contextCalculator = calculatorSupplier.get();
        this.contexts.registerCalculator(contextCalculator);
    }




    private void sendMessages() {
        plugin.log(Level.INFO, "-------------------------------------------");
        plugin.log(Level.INFO, "&6UltimateTeams: &3Hooked into LuckPerms");
        plugin.log(Level.INFO, "&6UltimateTeams: &3Contexts registered!");
        plugin.log(Level.INFO, "-------------------------------------------");
    }

}