package de.xnonymous.usefulapi.paper;

import de.xnonymous.usefulapi.UsefulAPI;
import de.xnonymous.usefulapi.paper.command.Command;
import de.xnonymous.usefulapi.paper.command.CommandRegistry;
import de.xnonymous.usefulapi.paper.config.ConfigRegistry;
import de.xnonymous.usefulapi.paper.config.impl.DataConfig;
import de.xnonymous.usefulapi.paper.npc.NPC;
import de.xnonymous.usefulapi.paper.npc.NPCManager;
import de.xnonymous.usefulapi.paper.util.ItemBuilder;
import de.xnonymous.usefulapi.util.Checks;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

@SuperBuilder
@Getter
public class PaperUsefulAPI extends UsefulAPI {

    @Setter
    public JavaPlugin plugin;
    private NPCManager npcManager;
    private String commandPackage;
    private String listenerPackage;
    @Getter
    private CommandRegistry commandRegistry;
    private ConfigRegistry configRegistry;
    private String commandNoPermMessage;
    private String commandCooldownMessage;
    private String commandNoPlayerMessage;
    private String commandSyntaxMessage;

    @Override
    @SneakyThrows
    public void startBeingUseful() {
        super.startBeingUseful();

        configRegistry = new ConfigRegistry(this);

        if (Checks.isNotEmpty(commandPackage))
            commandRegistry = new CommandRegistry(this, commandPackage, commandNoPermMessage, commandCooldownMessage, commandNoPlayerMessage, commandSyntaxMessage);
        if (Checks.isNotEmpty(listenerPackage)) {
            Reflections reflections = new Reflections(listenerPackage);
            Set<Class<?>> classes = reflections.get(SubTypes.of(Listener.class).asClass());

            for (Class<?> aClass : classes) {
                Bukkit.getPluginManager().registerEvents((Listener) aClass.getDeclaredConstructor().newInstance(), plugin);
            }
        }

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

    public DataConfig getDataConfig() {
        if (configRegistry == null)
            return null;
        return ((DataConfig) configRegistry.getByClass(DataConfig.class));
    }

}
