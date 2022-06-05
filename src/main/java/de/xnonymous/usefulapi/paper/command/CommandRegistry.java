package de.xnonymous.usefulapi.paper.command;

import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.util.ChatUtil;
import de.xnonymous.usefulapi.paper.util.ConsumerUtil;
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
import java.util.function.Consumer;

public class CommandRegistry extends NameableRegistry<Command> {

    private final ArrayList<Cooldown> cooldowns = new ArrayList<>();

    @SneakyThrows
    public CommandRegistry(PaperUsefulAPI usefulAPI, String where, Consumer<ConsumerUtil> noPerm, Consumer<ConsumerUtil> cooldown, Consumer<ConsumerUtil> noPlayer, Consumer<ConsumerUtil> syntax) {
        JavaPlugin instance = usefulAPI.getPlugin();
        Reflections reflections = new Reflections(where);
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        for (Class<?> aClass : classes) {
            register((Command) aClass.getConstructor(PaperUsefulAPI.class).newInstance(usefulAPI));
        }

        Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        getObjects().forEach(command -> {
            String perm = instance.getName().toLowerCase() + ".command." + command.getName().toLowerCase();
            BukkitCommand bukkitCommand = new BukkitCommand(command.getName()) {
                @Override
                public boolean execute(CommandSender commandSender, @NotNull String s, String[] strings) {

                    if (!commandSender.hasPermission(perm)) {
                        if (noPerm == null)
                            sendMessage(usefulAPI, commandSender, "§cDu hast keine Berechtigung diesen Befehl auszuführen!");
                        else noPerm.accept(new ConsumerUtil(commandSender, perm));
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
                                if (cooldown == null)
                                    sendMessage(usefulAPI, commandSender, "§cDu kannst diesen Befehl erst in §4" + cooldown1.howLong() + " §cSekunden wieder benutzen!");
                                else cooldown.accept(new ConsumerUtil(commandSender, cooldown1.howLong()));
                                return false;
                            }
                        }
                    }

                    if (!(commandSender instanceof Player) && !command.isConsole()) {
                        if (noPlayer == null)
                            sendMessage(usefulAPI, commandSender, "§cDieser Befehl ist nur für Spieler!");
                        else noPlayer.accept(new ConsumerUtil(commandSender, null));
                        return false;
                    }

                    if (!command.onExecute(commandSender, strings)) {
                        String syntax1 = "/" + command.getName().toLowerCase() + " " + command.getSyntax();
                        if (syntax == null)
                            sendMessage(usefulAPI, commandSender, "§7Bitte benutze: §e" + syntax1);
                        else syntax.accept(new ConsumerUtil(commandSender, syntax1));
                        return false;
                    }

                    return true;
                }
            };
            bukkitCommand.setDescription(command.getDescription());
            bukkitCommand.setUsage(command.getSyntax());
            bukkitCommand.setPermission(perm);
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
