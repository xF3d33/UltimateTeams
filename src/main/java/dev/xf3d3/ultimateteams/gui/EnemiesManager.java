package dev.xf3d3.ultimateteams.gui;

import de.themoep.inventorygui.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EnemiesManager {
    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private final UltimateTeams plugin;
    private final Player player;

    public EnemiesManager(@NotNull UltimateTeams plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        open();
    }

    private void open() {
        final InventoryGui gui = new InventoryGui(plugin, player, Utils.Color(plugin.getTeamsGui().getEnemiesManagerGuiName()), plugin.getTeamsGui().getTeamsManagerEnemiesguiSetup().toArray(new String[0]));

        // Previous page
        gui.addElement(new GuiPageElement('f', new ItemStack(plugin.getTeamsGui().getPreviousPageMaterial()), GuiPageElement.PageAction.PREVIOUS, plugin.getTeamsGui().getPreviousPage()));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(plugin.getTeamsGui().getNextPageMaterial()), GuiPageElement.PageAction.NEXT, plugin.getTeamsGui().getNextPage()));

        // Back
        gui.addElement(
                new StaticGuiElement('b',
                        new ItemStack(plugin.getTeamsGui().getBackButtonMaterial()),
                        1, // Display a number as the item count
                        click -> {
                            click.getGui().close();
                            new TeamManager(plugin, player);

                            return true;
                        },
                        plugin.getTeamsGui().getBackButtonName()
                ));



        GuiElementGroup group = new GuiElementGroup('g');
        Optional<Team> OptionalTeam = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());

        if (OptionalTeam.isEmpty())
            throw new IllegalStateException("Team not found");

        Team team = OptionalTeam.get();
        final Set<Team> enemies = team.getRelations(plugin).entrySet().stream()
                .filter(entry -> entry.getValue() == Team.Relation.ENEMY)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (Team enemy : enemies) {
            OfflinePlayer offlineMember = Bukkit.getOfflinePlayer(enemy.getOwner());

            group.addElement(
                    new DynamicGuiElement('g', (viewer) -> new StaticGuiElement('g',
                            TeamList.createPlayerSkull(offlineMember),
                            1, // Display a number as the item count
                            click -> {
                                // RIGHT CLICK, REMOVE ALLIE
                                if (click.getType().isRightClick()) {
                                    if (plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS))) {
                                        plugin.getTeamStorageUtil().removeTeamEnemy(team, enemy, player);

                                        team.sendTeamMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace("%ENEMYTEAM%", enemy.getName())));
                                        enemy.sendTeamMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-enemies").replace("%TEAMOWNER%", team.getName())));

                                        click.getGui().close();
                                        new EnemiesManager(plugin, player);
                                    } else {
                                        player.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                                    }
                                }

                                return true;
                            },
                            plugin.getTeamsGui().getEnemiesManagerGuiText()
                                    .stream()
                                    .map(s -> s.replaceAll("%NAME%", enemy.getName()))
                                    .toArray(String[]::new)
                    )
            ));
        }

        gui.addElement(group);
        gui.show(player);
    }
}