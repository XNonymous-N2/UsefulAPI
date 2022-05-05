package de.xnonymous.usefulapi.paper.util;

import com.comphenix.protocol.events.PacketContainer;
import de.xnonymous.usefulapi.paper.npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PacketUtils {

    public static void broadcastPacket(PacketContainer... packetContainers) {
        Bukkit.getOnlinePlayers().forEach(player -> sendPacket(player, packetContainers));
    }

    public static void sendPacket(Player player, PacketContainer... packetContainers) {
        for (PacketContainer packetContainer : packetContainers) {
            try {
                NPCManager.getInstance().getProtocolManager().sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
