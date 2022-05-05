package de.xnonymous.usefulapi.paper.config;

import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.structure.INameable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class Config implements INameable {

    private PaperUsefulAPI usefulAPI;
    private String name;
    private File file;
    private FileConfiguration cfg;

    public Config(PaperUsefulAPI usefulAPI, String name) {
        this.name = name;
        this.usefulAPI = usefulAPI;
        this.file = new File(usefulAPI.getPlugin().getDataFolder(), name + ".yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.file = new File(usefulAPI.getPlugin().getDataFolder(), name + ".yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {

    }

}