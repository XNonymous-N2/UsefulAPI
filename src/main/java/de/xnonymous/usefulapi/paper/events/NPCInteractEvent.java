package de.xnonymous.usefulapi.paper.events;

import de.xnonymous.usefulapi.paper.npc.NPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


@Getter
public class NPCInteractEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final NPC npc;

    public NPCInteractEvent(Player player, NPC npc) {
        super(true);
        this.player = player;
        this.npc = npc;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
