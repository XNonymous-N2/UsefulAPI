package de.xnonymous.usefulapi.paper.command;

import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.util.ChatUtil;
import de.xnonymous.usefulapi.structure.NameableRegistry;
import de.xnonymous.usefulapi.util.Cooldown;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.reflections.scanners.Scanners.SubTypes;

public class CommandRegistry extends NameableRegistry<Command> {

    private final ArrayList<Cooldown> cooldowns = new ArrayList<>();

    @SneakyThrows
    public CommandRegistry(PaperUsefulAPI usefulAPI, String where) {
        JavaPlugin instance = usefulAPI.getPlugin();
        Reflections reflections = new Reflections(where);
        Set<Class<?>> classes = reflections.get(SubTypes.of(Command.class).asClass());

        for (Class<?> aClass : classes) {
            register((Command) aClass.getConstructor(PaperUsefulAPI.class).newInstance(usefulAPI));
        }

        Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        getObjects().forEach(command -> {
            BukkitCommand bukkitCommand = new BukkitCommand(command.getName()) {
                @Override
                public boolean execute(CommandSender commandSender, @NotNull String s, String[] strings) {

                    if (!commandSender.hasPermission(instance.getName().toLowerCase() + ".command." + command.getName().toLowerCase())) {
                        sendMessage(usefulAPI, commandSender, "§cDu hast keine Berechtigung diesen Befehl auszuführen!");
                        return false;
                    }

                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        UUID uniqueId = player.getUniqueId();

                        if (command.getCooldown() != 0) {
                            Cooldown cooldown1 = cooldowns.stream()
                                    .filter(cooldown -> cooldown.getWho().equals(uniqueId) && cooldown.getIdentify().equals(command.getName()))
                                    .findFirst().orElse(null);

                            if (cooldown1 == null) {
                                Cooldown e = new Cooldown(uniqueId, command.getCooldown(), command.getName());
                                cooldowns.add(e);

                                Bukkit.getScheduler()
                                        .runTaskLater(instance, () ->
                                                        cooldowns.removeIf(cooldown -> cooldown.getWho().equals(uniqueId) &&
                                                                cooldown.getIdentify().equals(command.getName())),
                                                20L * command.getCooldown());
                            } else {
                                sendMessage(usefulAPI, commandSender, "§cDu kannst diesen Befehl erst in §4" + cooldown1.howLong() + " §cSekunden wieder benutzen!");
                                return false;
                            }
                        }
                    }

                    if (!(commandSender instanceof Player) && !command.isConsole()) {
                        sendMessage(usefulAPI, commandSender, "§cDieser Befehl ist nur für Spieler!");
                        return false;
                    }

                    if (!command.onExecute(commandSender, strings)) {
                        sendMessage(usefulAPI, commandSender, "§7Bitte benutze: §e%syntax%".replaceAll("%syntax%", "/" + command.getName().toLowerCase() + " " + command.getSyntax()));
                        return false;
                    }

                    return true;
                }
            };
            bukkitCommand.setDescription(command.getDescription());
            bukkitCommand.setUsage(command.getSyntax());
            bukkitCommand.setPermission(instance.getName().toLowerCase() + ".command." + command.getName().toLowerCase());
            bukkitCommand.setAliases(Arrays.asList(command.getAlias()));

            commandMap.register(instance.getName(), bukkitCommand);
        });

        Bukkit.getScheduler().runTaskLater(instance, () -> getObjects().forEach(command -> {
            try {
                command.onLoad();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }), 2);
    }

    public void sendMessage(PaperUsefulAPI usefulAPI, CommandSender sender, String message) {
        ChatUtil.sendMessage(sender, usefulAPI.getPrefix() + " " + message);
    }
}
