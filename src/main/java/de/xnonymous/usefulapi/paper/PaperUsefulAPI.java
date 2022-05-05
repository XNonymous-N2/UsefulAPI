package de.xnonymous.usefulapi.paper;

import de.xnonymous.usefulapi.UsefulAPI;
import de.xnonymous.usefulapi.paper.command.CommandRegistry;
import de.xnonymous.usefulapi.paper.config.ConfigRegistry;
import de.xnonymous.usefulapi.paper.config.impl.DataConfig;
import de.xnonymous.usefulapi.paper.npc.NPC;
import de.xnonymous.usefulapi.paper.npc.NPCManager;
import de.xnonymous.usefulapi.paper.util.ItemBuilder;
import de.xnonymous.usefulapi.util.Checks;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;

@SuperBuilder
@Getter
public class PaperUsefulAPI extends UsefulAPI {

    @NonNull
    public JavaPlugin plugin;
    private NPCManager npcManager;
    private String commandPackage;
    private CommandRegistry commandRegistry;
    private ConfigRegistry configRegistry;

    @Override
    public void startBeingUseful() {
        super.startBeingUseful();

        configRegistry = new ConfigRegistry(this);

        if (Checks.isNotEmpty(commandPackage))
            commandRegistry = new CommandRegistry(this, commandPackage);

    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (npcManager != null)
            npcManager.onDisable();
    }

    public NPC createNPC(Location location, String displayName, String skinOwner) {
        if (npcManager == null)
            npcManager = new NPCManager(plugin);

        return npcManager.registerAndSpawnNPC(location, displayName, skinOwner);
    }

    public NPC createNPC(Location location, String displayName, String skinValue, String skinSignature) {
        if (npcManager == null)
            npcManager = new NPCManager(plugin);

        return npcManager.registerAndSpawnNPC(location, displayName, skinValue, skinSignature);
    }

    public ItemBuilder.ItemBuilderBuilder buildItem() {
        return ItemBuilder.builder();
    }

    @Override
    public void redisSubscribe(JedisPubSub jedisPubSub, String... channels) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> super.redisSubscribe(jedisPubSub, channels));
    }
}
