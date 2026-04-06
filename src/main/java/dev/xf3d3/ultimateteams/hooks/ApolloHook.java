package dev.xf3d3.ultimateteams.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamDisbandEvent;
import dev.xf3d3.ultimateteams.api.events.TeamMemberJoinEvent;
import dev.xf3d3.ultimateteams.api.events.TeamMemberLeaveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ApolloHook implements Listener {

    private static final String APOLLO_CHANNEL = "apollo:json";
    private static final String LUNAR_CHANNEL = "lunar:apollo";
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final UltimateTeams plugin;
    private final Set<UUID> apolloPlayers = ConcurrentHashMap.newKeySet();

    public ApolloHook(UltimateTeams plugin) {
        this.plugin = plugin;

        final var messenger = Bukkit.getServer().getMessenger();
        messenger.registerIncomingPluginChannel(plugin, LUNAR_CHANNEL, (channel, player, bytes) -> {});
        messenger.registerIncomingPluginChannel(plugin, APOLLO_CHANNEL, (channel, player, bytes) -> {});
        messenger.registerOutgoingPluginChannel(plugin, APOLLO_CHANNEL);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getScheduler().runTimerAsync(task -> refreshAllTeams(), 0L, 20L);

        plugin.sendConsole("-------------------------------------------");
        plugin.sendConsole("&6UltimateTeams: &3Apollo Teamview integration enabled!");
        plugin.sendConsole("-------------------------------------------");
    }

    @EventHandler
    public void onRegisterChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equalsIgnoreCase(LUNAR_CHANNEL)) {
            return;
        }

        final Player player = event.getPlayer();
        apolloPlayers.add(player.getUniqueId());

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresent(this::refreshTeam);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        apolloPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinTeam(TeamMemberJoinEvent event) {
        refreshTeam(event.getTeam());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeaveTeam(TeamMemberLeaveEvent event) {
        final Player leaving = Bukkit.getPlayer(event.getOldMember());
        if (leaving != null && apolloPlayers.contains(leaving.getUniqueId())) {
            sendResetTeamMembers(leaving);
        }

        refreshTeam(event.getTeam());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisbandTeam(TeamDisbandEvent event) {
        for (final Player member : event.getTeam().getOnlineMembers()) {
            if (apolloPlayers.contains(member.getUniqueId())) {
                sendResetTeamMembers(member);
            }
        }
    }

    private void refreshAllTeams() {
        plugin.getTeamStorageUtil().getTeams().forEach(this::refreshTeam);
    }

    private void refreshTeam(Team team) {
        final JsonArray members = new JsonArray();
        for (final Player member : team.getOnlineMembers()) {
            members.add(createTeamMemberObject(member));
        }

        if (members.isEmpty()) {
            return;
        }

        final JsonObject message = new JsonObject();
        message.addProperty("@type", "type.googleapis.com/lunarclient.apollo.team.v1.UpdateTeamMembersMessage");
        message.add("members", members);

        for (final Player member : team.getOnlineMembers()) {
            if (apolloPlayers.contains(member.getUniqueId())) {
                sendPacket(member, message);
            }
        }
    }

    private JsonObject createTeamMemberObject(Player member) {
        final JsonObject obj = new JsonObject();
        obj.add("player_uuid", createUuidObject(member.getUniqueId()));
        obj.addProperty("adventure_json_player_name", toJson(
            Component.text(member.getName()).color(TextColor.color(DEFAULT_COLOR.getRGB()))
        ));
        obj.add("marker_color", createColorObject(DEFAULT_COLOR));
        obj.add("location", createLocationObject(member.getLocation()));
        return obj;
    }

    private void sendResetTeamMembers(Player player) {
        final JsonObject message = new JsonObject();
        message.addProperty("@type", "type.googleapis.com/lunarclient.apollo.team.v1.ResetTeamMembersMessage");
        sendPacket(player, message);
    }

    private void sendPacket(Player player, JsonObject message) {
        final byte[] bytes = message.toString().getBytes(StandardCharsets.UTF_8);
        player.sendPluginMessage(plugin, APOLLO_CHANNEL, bytes);
    }

    private static JsonObject createUuidObject(UUID uuid) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("high64", Long.toUnsignedString(uuid.getMostSignificantBits()));
        obj.addProperty("low64", Long.toUnsignedString(uuid.getLeastSignificantBits()));
        return obj;
    }

    private static JsonObject createColorObject(Color color) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("color", color.getRGB());
        return obj;
    }

    private static JsonObject createLocationObject(Location location) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("world", location.getWorld().getName());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        return obj;
    }

    private static String toJson(Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }
}
