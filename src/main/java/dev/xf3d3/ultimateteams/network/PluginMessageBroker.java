package dev.xf3d3.ultimateteams.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.logging.Level;

/**
 * <a href="https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/">Plugin Messaging channel</a> message
 * broker implementation
 */
public class PluginMessageBroker extends Broker {
    
    public static final String BUNGEE_CHANNEL_ID = "BungeeCord";

    public PluginMessageBroker(@NotNull UltimateTeams plugin) {
        super(plugin);
    }

    @Override
    public void initialize() throws RuntimeException {
        plugin.initializePluginChannels();
    }

    public final void onReceive(@NotNull String channel, @NotNull Player user, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL_ID)) {
            return;
        }

        final ByteArrayDataInput inputStream = ByteStreams.newDataInput(message);
        final String subChannelId = inputStream.readUTF();
        if (!subChannelId.equals(getSubChannelId())) {
            return;
        }

        short messageLength = inputStream.readShort();
        byte[] messageBody = new byte[messageLength];
        inputStream.readFully(messageBody);

        try (final DataInputStream messageReader = new DataInputStream(new ByteArrayInputStream(messageBody))) {
            super.handle(user, plugin.getMessageFromJson(messageReader.readUTF()));
        } catch (IOException e) {
            plugin.log(Level.SEVERE, "Failed to fully read plugin message", e);
        }
    }

    @Override
    protected void send(@NotNull Message message, @Nullable Player sender) {
        if (sender == null) return;

        final ByteArrayDataOutput messageWriter = ByteStreams.newDataOutput();
        messageWriter.writeUTF(message.getTargetType().getPluginMessageChannel());
        messageWriter.writeUTF(message.getTarget());
        messageWriter.writeUTF(getSubChannelId());

        // Write the plugin message
        try (final ByteArrayOutputStream messageByteStream = new ByteArrayOutputStream()) {
            try (DataOutputStream messageDataStream = new DataOutputStream(messageByteStream)) {
                messageDataStream.writeUTF(plugin.getGson().toJson(message));
                messageWriter.writeShort(messageByteStream.toByteArray().length);
                messageWriter.write(messageByteStream.toByteArray());
            }
        } catch (IOException e) {
            plugin.log(Level.SEVERE, "Exception dispatching plugin message", e);
            return;
        }

        sender.sendPluginMessage(plugin, BUNGEE_CHANNEL_ID, messageWriter.toByteArray());
    }

    @Override
    public void changeServer(@NotNull Player user, @NotNull String server) {
        final ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();

        outputStream.writeUTF("Connect");
        outputStream.writeUTF(server);

        user.sendPluginMessage(plugin, BUNGEE_CHANNEL_ID, outputStream.toByteArray());
    }

    @Override
    public void close() {
    }

}
