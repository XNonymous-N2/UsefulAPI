package de.xnonymous.usefulapi.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class UUIDFetcher {

    public static HashMap<UUID, String> fetch = new HashMap<>();
    public static HashMap<String, UUID> fetch1 = new HashMap<>();

    public static UUID getUUID(String name) {
        try {
            if (fetch1.containsKey(name))
                return fetch1.get(name);

            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 400)
                return null;

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            JsonElement element = new JsonParser().parse(bufferedReader);
            JsonObject object = element.getAsJsonObject();
            String uuidAsString = object.get("id").getAsString();

            UUID uuid = parseUUIDFromString(uuidAsString);
            fetch.put(uuid, name.toLowerCase());
            fetch1.put(name.toLowerCase(), uuid);
            return uuid;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getName(UUID uuid) {
        try {
            if (fetch.containsKey(uuid))
                return fetch.get(uuid);

            URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 400)
                return null;

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            JsonElement element = new JsonParser().parse(bufferedReader);
            JsonArray array = element.getAsJsonArray();
            JsonObject object = array.get(array.size() - 1).getAsJsonObject();
            String name = object.get("name").getAsString();
            fetch.put(uuid, name);
            fetch1.put(name, uuid);
            return name;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static UUID parseUUIDFromString(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]);

        long leastSigBits = Long.decode(parts[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]);

        return new UUID(mostSigBits, leastSigBits);
    }

}
