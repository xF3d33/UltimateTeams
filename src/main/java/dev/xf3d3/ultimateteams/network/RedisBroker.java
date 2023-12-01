package dev.xf3d3.ultimateteams.network;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

public class RedisBroker extends PluginMessages {
    private JedisPool jedisPool;

    public RedisBroker(@NotNull UltimateTeams plugin) {
        super(plugin);
    }

    @Override
    public void initialize() throws RuntimeException {
        super.initialize();

        final String password = plugin.getSettings().getRedisPassword();
        final String host = plugin.getSettings().getRedisHost();
        final int port = plugin.getSettings().getRedisPort();
        final boolean useSSL = plugin.getSettings().useRedisSsl();

        this.jedisPool = password.isEmpty() ? new JedisPool(new JedisPoolConfig(), host, port, 0, useSSL)
                : new JedisPool(new JedisPoolConfig(), host, port, 0, password, useSSL);

        new Thread(getSubscriber(), plugin.getKey("redis_subscriber").toString()).start();

        plugin.log(Level.INFO, "Initialized Redis connection pool");
    }

    @NotNull
    private Runnable getSubscriber() {
        return () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(@NotNull String channel, @NotNull String encodedMessage) {
                        if (!channel.equals(getSubChannelId())) {
                            return;
                        }

                        final Message message = plugin.getMessageFromJson(encodedMessage);
                        if (message.getTargetType() == Message.TargetType.PLAYER) {
                            plugin.getOnlineUsers().stream()
                                    .filter(online -> online.getUsername().equalsIgnoreCase(message.getTarget()))
                                    .findFirst()
                                    .ifPresent(receiver -> handle(receiver, message));
                            return;
                        }
                        handle(plugin.getOnlineUsers().stream().findAny().orElse(null), message);
                    }
                }, getSubChannelId());
            }
        };
    }

    @Override
    protected void send(@NotNull Message message, @NotNull Player sender) {
        plugin.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(getSubChannelId(), plugin.getGson().toJson(message));
            }
        });
    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }


}
