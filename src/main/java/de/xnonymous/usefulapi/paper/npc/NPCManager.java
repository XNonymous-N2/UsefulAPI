package de.xnonymous.usefulapi.paper.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.ImmutableList;
import de.xnonymous.usefulapi.paper.events.NPCInteractEvent;
import de.xnonymous.usefulapi.paper.listener.PlayerListener;
import de.xnonymous.usefulapi.paper.util.PacketUtils;
import de.xnonymous.usefulapi.paper.util.SkinUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Getter
public class NPCManager {

    @Getter
    private static NPCManager instance;

    private final Plugin plugin;
    private final ProtocolManager protocolManager;

    private final ArrayList<NPC> npcs = new ArrayList<>();

    public NPCManager(Plugin plugin) {
        instance = this;
        this.plugin = plugin;

        this.protocolManager = ProtocolLibrary.getProtocolManager();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(), plugin);

        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    PacketContainer packet = event.getPacket();
                    Integer read = packet.getIntegers().read(0);
                    npcs.stream().filter(npc -> npc.getUuid().hashCode() == read).findFirst().ifPresent(npc1 -> Bukkit.getPluginManager().callEvent(new NPCInteractEvent(event.getPlayer(), npc1)));
                } catch (Exception ignored) {

                }
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    NPCManager.getInstance().getNpcs().forEach(npc -> {
                        if (npc.getCanSee().contains(event.getPlayer().getUniqueId())) {
                            npc.getCanSee().remove(event.getPlayer().getUniqueId());
                            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> PacketUtils.broadcastPacket(npc.getPlayerInfoRemove()), 2);
                        }
                    });
                } catch (Exception ignored) {

                }
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
                for (NPC npc : npcs) {
                    if (!Objects.equals(npc.getLocation().getWorld(), player.getLocation().getWorld())) continue;

                    double distance = npc.getLocation().distanceSquared(player.getLocation());
                    boolean inRange = distance <= 50000;

                    if (inRange)
                        npc.inRange(player);
                    else
                        npc.getInRange().remove(player.getUniqueId());
                }
            }
        }, 20, 2);
    }

    @SneakyThrows
    public void onDisable() {
        npcs.forEach(NPC::destroy);
    }

    public NPC registerAndSpawnNPC(Location location, String displayName, String skinValue, String skinSignature) {
        NPC e = new NPC(UUID.randomUUID(), location.clone(), displayName, SkinUtils.of(skinValue, skinSignature));
        e.init();
        npcs.add(e);
        return e;
    }

    public NPC registerAndSpawnNPC(Location location, String displayName, String skinOwner) {
        SkinUtils skinUtils = new SkinUtils(skinOwner);
        NPC e = new NPC(UUID.randomUUID(), location.clone(), displayName, skinUtils);
        e.init();
        npcs.add(e);
        return e;
    }
}
