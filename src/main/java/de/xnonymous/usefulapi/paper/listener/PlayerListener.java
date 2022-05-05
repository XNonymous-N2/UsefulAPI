package de.xnonymous.usefulapi.paper.listener;

import de.xnonymous.usefulapi.paper.npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Objects;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        World world = event.getPlayer().getLocation().getWorld();
        summon(event.getPlayer(), world);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getLocation().getWorld();
        summon(event.getPlayer(), world);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        World world = event.getPlayer().getLocation().getWorld();
        summon(event.getPlayer(), world);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        NPCManager.getInstance().getNpcs().forEach(npc -> npc.getInRange().remove(event.getPlayer().getUniqueId()));
    }

    private void summon(Player player, World world) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(NPCManager.getInstance().getPlugin(), () -> NPCManager.getInstance().getNpcs().stream().filter(npc -> Objects.equals(npc.getLocation().getWorld(), world)).forEach(npc -> npc.spawn(player)), 2);
    }

}
