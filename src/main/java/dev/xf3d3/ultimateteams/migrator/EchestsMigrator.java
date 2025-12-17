package dev.xf3d3.ultimateteams.migrator;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Level;

public class EchestsMigrator {
    private final UltimateTeams plugin;
    private final CommandSender sender;
    private int convertedTeams = 0;

    @Nullable Player randomPlayer;

    public EchestsMigrator(@NotNull UltimateTeams plugin, @NotNull CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.randomPlayer = (sender instanceof Player) ? (Player) sender : Bukkit.getOnlinePlayers().stream().findAny().orElse(null);
        startMigration();
    }

    public void startMigration() {
        sender.sendMessage(Utils.Color("&7Starting team echests migration..."));

        plugin.runAsync(task -> {
            try {
                plugin.getLogger().info("DEBUG: Async thread started. Fetching teams...");
                ArrayList<Team> safeTeamList = new ArrayList<>(plugin.getTeamStorageUtil().getTeams());

                plugin.getLogger().info("DEBUG: Found " + safeTeamList.size() + " teams. Starting migration...");

                int errors = 0;

                for (Team team : safeTeamList) {
                    boolean needsSave = false;

                    for (TeamEnderChest chest : team.getEnderChests().values()) {
                        try {
                            ItemStack[] items = chest.getContents();


                            chest.setContents(items);
                            needsSave = true;
                        } catch (Throwable t) {

                            plugin.log(Level.WARNING, "Corrupted chest in Team '" + team.getName() + "'.");
                            try {
                                chest.setContents(new ItemStack[0]);
                                needsSave = true;
                                errors++;
                            } catch (Exception ignored) {}
                        }
                    }

                    if (needsSave) {
                        try {
                            plugin.getTeamStorageUtil().updateTeamData(randomPlayer, team);
                            convertedTeams++;
                        } catch (Throwable t) {

                            plugin.getLogger().warning("Could not save Team '" + team.getName() + "': " + t.getMessage());
                            errors++;
                        }
                    }
                }

                sender.sendMessage(Utils.Color("&aMigration Complete! Updated: " + convertedTeams + " | Errors: " + errors));
                plugin.getLogger().info("DEBUG: Migration finished successfully.");

            } catch (Throwable fatal) {
                plugin.log(Level.SEVERE, "!!!!! FATAL CRASH IN MIGRATION THREAD !!!!!", fatal);
                sender.sendMessage(Utils.Color("&cMigration crashed! Check console for the 'FATAL CRASH' error."));
            }
        });
    }
}