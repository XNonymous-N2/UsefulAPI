package de.xnonymous.usefulapi.paper.util;

import de.xnonymous.usefulapi.util.diff_match_patch;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class ChatUtil {

    public static boolean areMessagesEquals(String lastMessage, String message) {
        diff_match_patch diffChecker = new diff_match_patch();

        int differences = diffChecker.diff_levenshtein(diffChecker.diff_main(lastMessage, message));
        int longestMessageSize = Math.max(lastMessage.length(), message.length());

        return (differences * 100) / longestMessageSize < 30;
    }

    public static void sendActionbar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(message);
    }

    public static void sendMessage(CommandSender commandSender, Component component) {
        commandSender.sendMessage(Identity.nil(), component, MessageType.CHAT);
    }

    public static void broadcast(String message) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(message);
        }

        ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
        consoleSender.sendMessage(message);
    }

    public static String timeToRelative(long time) {
        Date date1 = new Date();
        Date date = new Date(time);

        StringBuilder build = new StringBuilder("§7vor §e");

        int i = date1.getYear() - date.getYear();
        if (i > 0)
            if (i != 1)
                build.append(i).append(" §7Jahren §e");
            else build.append("einem §7Jahr §e");

        int i1 = date1.getDay() - date.getDay();
        if (i1 > 0)
            if (i1 != 1)
                build.append(i1).append(" §7Tagen §e");
            else build.append("einem §7Tag §e");

        int i2 = date1.getHours() - date.getHours();
        if (i2 > 0)
            if (i2 != 1)
                build.append(i2).append(" §7Stunden §e");
            else build.append("einer §7Stunde §e");

        int i3 = date1.getMinutes() - date.getMinutes();
        if (i3 > 0)
            if (i3 != 1)
                build.append(i3).append(" §7Minuten §e");
            else build.append("einer §7Minute §e");

        int i4 = date1.getSeconds() - date.getSeconds();
        if (i4 > 0)
            if (i4 != 1)
                build.append(i4).append(" §7Sekunden §e");
            else build.append("einer §7Sekunde §e");

        if ("§7vor §e".equals(build.toString()))
            return "§ejetzt";

        return build.toString();
    }

}
