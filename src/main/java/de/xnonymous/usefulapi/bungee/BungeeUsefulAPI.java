package de.xnonymous.usefulapi.bungee;

import de.xnonymous.usefulapi.UsefulAPI;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.JedisPubSub;

@SuperBuilder
public class BungeeUsefulAPI extends UsefulAPI {

    @NonNull
    public Plugin plugin;

    @Override
    public void startBeingUseful() {
        super.startBeingUseful();

    }

    @Override
    public void redisSubscribe(JedisPubSub jedisPubSub, String... channels) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> super.redisSubscribe(jedisPubSub, channels));
    }
}
