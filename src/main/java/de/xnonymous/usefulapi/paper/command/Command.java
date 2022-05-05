package de.xnonymous.usefulapi.paper.command;

import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.config.impl.DataConfig;
import de.xnonymous.usefulapi.paper.util.ChatUtil;
import de.xnonymous.usefulapi.structure.INameable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public abstract class Command implements Listener, INameable {

    private PaperUsefulAPI usefulAPI;
    private int cooldown = 0;
    private final String name;
    private final String description;
    private final String[] alias;
    private final String syntax;
    private final boolean console;

    public Command(PaperUsefulAPI usefulAPI, String name, String description, boolean console, String syntax, String... alias) {
        this.usefulAPI = usefulAPI;
        this.name = name;
        this.description = description;
        this.alias = alias;
        this.syntax = syntax;
        this.console = console;
    }

    public abstract boolean onExecute(CommandSender commandSender, String[] args);

    public void onLoad() {
        Bukkit.getPluginManager().registerEvents(this, usefulAPI.getPlugin());
    }

    public void onUnload() {
        HandlerList.unregisterAll(this);
    }

    public HashMap<UUID, HashMap<String, Object>> getData(String key) {
        DataConfig byClass = (DataConfig) usefulAPI.getConfigRegistry().getByClass(DataConfig.class);
        HashMap<UUID, HashMap<String, Object>> datas = byClass.getDatas();

        HashMap<UUID, HashMap<String, Object>> list = new HashMap<>();
        for (Map.Entry<UUID, HashMap<String, Object>> uuidHashMapEntry : datas.entrySet()) {
            UUID key1 = uuidHashMapEntry.getKey();
            HashMap<String, Object> value = uuidHashMapEntry.getValue();

            if (value.containsKey(key))
                list.put(key1, value);
        }

        return list;
    }

    public <t> t getData(UUID from, String key, Class<? extends t> clazz) {
        DataConfig byClass = (DataConfig) usefulAPI.getConfigRegistry().getByClass(DataConfig.class);
        return byClass.getData(from, key, clazz);
    }

    public void addData(UUID who, String key, Object what) {
        DataConfig byClass = (DataConfig) usefulAPI.getConfigRegistry().getByClass(DataConfig.class);
        byClass.addData(who, key, what);
    }

    public void removeData(UUID uniqueId, String key) {
        DataConfig byClass = (DataConfig) usefulAPI.getConfigRegistry().getByClass(DataConfig.class);
        byClass.removeData(uniqueId, key);
    }

    public boolean checkPerm(CommandSender sender, String perm) {
        String name = "essentials.command." + this.name.toLowerCase() + "." + perm;
        boolean b = sender.hasPermission(name);
        if (!b) {
            sendMessage(sender, "§cDu hast keine Berechtigung diesen Befehl auszuführen!");
            sendMessage(sender, "§cDu benötigst: §e" + name);
        }
        return !b;
    }

    public Player getPlayer(CommandSender sender) {
        return ((Player) sender);
    }

    public void sendMessage(CommandSender sender, String message) {
        ChatUtil.sendMessage(sender, usefulAPI.getPrefix() + " " + message);
    }

    public void sendMessage(CommandSender sender, Component component) {
        TextComponent append = Component.text(usefulAPI.getPrefix() + " ").append(component);
        ChatUtil.sendMessage(sender, append);
    }

}