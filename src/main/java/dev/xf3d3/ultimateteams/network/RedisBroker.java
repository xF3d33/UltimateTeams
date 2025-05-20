package dev.xf3d3.ultimateteams.network;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.util.Pool;

import java.util.logging.Level;

/**
 * Redis message broker implementation
 */
public class RedisBroker extends PluginMessageBroker {
    private Pool<Jedis> jedisPool;

    public RedisBroker(@NotNull UltimateTeams plugin) {
        super(plugin);
    }

    @Override
    public void initialize() throws RuntimeException {
        super.initialize();

        this.jedisPool = establishJedisPool();
        new Thread(getSubscriber(), "UltimateTeams_redis_subscriber").start();

        plugin.log(Level.INFO, "Initialized Redis connection pool");
    }

    @NotNull
    private Pool<Jedis> establishJedisPool() {
        final Pool<Jedis> pool = new JedisPool(
            new JedisPoolConfig(),
            plugin.getSettings().getRedisHost(),
            plugin.getSettings().getRedisPort(),
            0,
            plugin.getSettings().getRedisPassword().isEmpty() ? null : plugin.getSettings().getRedisPassword(),
            plugin.getSettings().isRedisUseSSL()
        );

        plugin.log(Level.INFO, "Using Redis pool");
        return pool;
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

                        //plugin.log(Level.INFO, "received message with redis: " + message.getType());

                        if (message.getTargetType() == Message.TargetType.PLAYER) {

                            Bukkit.getOnlinePlayers().stream()
                                .filter(player -> player.getName().equalsIgnoreCase(message.getTarget()))
                                .findFirst()
                                .ifPresent(receiver -> handle(receiver, message));
                            return;
                        }
                        handle(plugin.getUsersStorageUtil().getOnlineUsers().stream().findAny().orElse(null), message);
                    }
                }, getSubChannelId());
            }
        };
    }

    @Override
    protected void send(@NotNull Message message, @NotNull Player sender) {
        plugin.runAsync(task -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(getSubChannelId(), plugin.getGson().toJson(message));
            }
        });

        //plugin.log(Level.INFO, "sent message with redis: " + message.getType());
    }

    @Override
    public void close() {
        super.close();
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

}
