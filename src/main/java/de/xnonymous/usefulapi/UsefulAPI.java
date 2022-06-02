package de.xnonymous.usefulapi;

import de.xnonymous.api.mysql.MySQL;
import de.xnonymous.usefulapi.util.Checks;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Warning;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.function.Consumer;

@SuperBuilder
@Getter
public class UsefulAPI {

    @Setter
    private Consumer<String> log;
    private MySQL mySQL;
    private String mySQLHost;
    private Integer mySQLPort;
    private String mySQLDb;
    private String mySQLUsername;
    private String mySQLPassword;

    private JedisPool jedisPool;
    private Boolean useRedis;
    private String redisHost;
    private String redisUser;
    private String redisPassword;
    private Integer redisPort;
    private String name;
    private String prefix;

    public void startBeingUseful() {
        if (Checks.isNotEmpty(mySQLHost, mySQLDb, mySQLUsername, mySQLPassword)) {
            mySQL = new MySQL();
            mySQL.setHost(mySQLHost);
            mySQL.setPort(mySQLPort == null ? 3306 : mySQLPort);
            mySQL.setDb(mySQLDb);
            mySQL.setUser(mySQLUsername);
            mySQL.setPassword(mySQLPassword);
            mySQL.connect();

            log("Connected to MySQL!");
        }

        if (useRedis != null && useRedis) {
            jedisPool = new JedisPool(Checks.nullOr(redisHost, Protocol.DEFAULT_HOST),
                    Checks.nullOr(redisPort, Protocol.DEFAULT_PORT),
                    redisUser,
                    redisPassword);

            log("Connected to Redis!");
        }

    }

    public void onDisable() {
        if (useRedis != null && useRedis)
            jedisPool.destroy();
        if (mySQL != null)
            mySQL.disconnect();
    }

    public String getRedisData(String key) {
        if (useRedis == null || !useRedis)
            return null;

        try (Jedis resource = jedisPool.getResource()) {
            log("Getting redis data " + key);
            return resource.get(key);
        }
    }

    public void setRedisData(String key, String data) {
        if (useRedis == null || !useRedis)
            return;

        try (Jedis resource = jedisPool.getResource()) {
            log("Setting redis data " + key);
            resource.set(key, data);
        }
    }

    @SuppressWarnings("Blocking main thread, call async")
    public void redisSubscribe(JedisPubSub jedisPubSub, String... channels) {
        if (useRedis == null || !useRedis)
            return;

        try (Jedis resource = jedisPool.getResource()) {
            log("Redis subscribing on " + Arrays.toString(channels));
            resource.subscribe(jedisPubSub, channels);
        }
    }

    public void redisPublish(String channel, String data) {
        if (useRedis == null || !useRedis)
            return;

        try (Jedis resource = jedisPool.getResource()) {
            log("Redis publishing on channel " + channel + " data: " + data);
            resource.publish(channel, data);
        }
    }

    public void log(String text) {
        log.accept(text);
    }

}
