package de.xnonymous.usefulapi.paper.config.impl;

import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.config.Config;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.UUID;

public class DataConfig extends Config {

    public DataConfig(PaperUsefulAPI usefulAPI) {
        super(usefulAPI, "data");
    }

    @Getter
    private final HashMap<UUID, HashMap<String, Object>> datas = new HashMap<>();

    @Override
    public void onStart() {
        FileConfiguration cfg = getCfg();
        cfg.getKeys(true).forEach(s -> {
            UUID uuid;
            if (!s.contains("."))
                return;

            String[] split = s.split("\\.");
            uuid = UUID.fromString(split[0]);

            registerData(uuid, split[1], cfg.get(s));
        });
    }

    private void registerData(UUID who, String key, Object what) {
        HashMap<String, Object> orDefault = datas.getOrDefault(who, new HashMap<>());
        orDefault.put(key, what);

        datas.put(who, orDefault);
    }

    public <t> t getData(UUID from, String key, Class<? extends t> clazz) {
        HashMap<String, Object> stringObjectHashMap = datas.get(from);
        if (stringObjectHashMap == null) return null;
        Object o = stringObjectHashMap.get(key);
        if (o == null) return null;
        return clazz.cast(o);
    }

    public void addData(UUID who, String key, Object what) {
        HashMap<String, Object> orDefault = datas.getOrDefault(who, new HashMap<>());
        orDefault.put(key, what);

        datas.put(who, orDefault);
        getCfg().set(who.toString() + "." + key, what);
        save();
    }

    public void removeData(UUID from, String key) {
        HashMap<String, Object> orDefault = datas.getOrDefault(from, new HashMap<>());
        orDefault.remove(key);

        datas.put(from, orDefault);

        getCfg().set(from.toString() + "." + key, null);
        if (orDefault.isEmpty())
            getCfg().set(from.toString(), null);
        save();
    }
}
