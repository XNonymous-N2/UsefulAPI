package de.xnonymous.usefulapi.paper.config;


import de.xnonymous.usefulapi.UsefulAPI;
import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.config.impl.DataConfig;
import de.xnonymous.usefulapi.structure.NameableRegistry;

public class ConfigRegistry extends NameableRegistry<Config> {

    public ConfigRegistry(PaperUsefulAPI usefulAPI) {
        register(new DataConfig(usefulAPI));

        getObjects().forEach(Config::onStart);
    }

    public void reload() {
        getObjects().forEach(Config::reload);
    }

}