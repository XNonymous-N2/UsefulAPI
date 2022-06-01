package de.xnonymous.usefulapi.paper;

import de.xnonymous.usefulapi.paper.command.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

@RequiredArgsConstructor
public abstract class PaperInstance extends JavaPlugin {

    @Getter
    private static PaperInstance instance;
    @Getter
    private final PaperUsefulAPI api;

    public void preEnable() {
    }

    public void preDisable() {
    }

    @Override
    public void onEnable() {
        instance = this;

        api.setLog(s -> System.out.println(api.getPrefix() + " " + s));
        api.setPlugin(this);

        preEnable();

        PluginDescriptionFile description = getDescription();
        String version = getDescription().getVersion();
        String description1 = description.getDescription();
        List<String> authors = description.getAuthors();

        log("--------------------------------------------");
        log("Enabling " + api.getName() + " v" + version + " from " + String.join(", ", authors));
        log("> " + description1);
        log("--------------------------------------------");

        api.startBeingUseful();

        postEnable();
    }

    @Override
    public void onDisable() {
        preDisable();

        PluginDescriptionFile description = getDescription();
        String version = getDescription().getVersion();
        String description1 = description.getDescription();
        List<String> authors = description.getAuthors();

        log("--------------------------------------------");
        log("Disabling " + api.getName() + " v" + version + " from " + String.join(", ", authors));
        log("> " + description1);
        log("--------------------------------------------");

        api.onDisable();
        postDisable();
    }

    public void log(String msg) {
        api.log(msg);
    }

    public abstract void postEnable();

    public abstract void postDisable();

}
