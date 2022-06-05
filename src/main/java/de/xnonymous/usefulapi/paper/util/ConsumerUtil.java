package de.xnonymous.usefulapi.paper.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
@AllArgsConstructor
public class ConsumerUtil {

    private CommandSender player;
    private Object replace;

}
