package de.xnonymous.usefulapi.paper.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@RequiredArgsConstructor
@Getter
@Setter
public class SkinUtils {

    public static final ArrayList<SkinUtils> fetched = new ArrayList<>();

    private final String searchUrl = "https://api.ashcon.app/mojang/v2/user/";
    private final String skinOwner;

    private String value;
    private String signature;

    public static SkinUtils of(String skinValue, String skinSignature) {
        SkinUtils skinUtils = new SkinUtils(null);
        skinUtils.setValue(skinValue);
        skinUtils.setSignature(skinSignature);
        return skinUtils;
    }

    public void search() {
        try {
            if (skinOwner == null || value != null || signature != null)
                return;
            if (skinOwner.equals(""))
                return;

            SkinUtils skinUtils = anyMatch();
            if (skinUtils != null) {
                value = skinUtils.getValue();
                signature = skinUtils.getSignature();
                return;
            }

            URL url = new URL(searchUrl + skinOwner);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 400)
                return;

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            JsonElement element = new JsonParser().parse(bufferedReader);
            JsonObject object = element.getAsJsonObject();
            JsonObject asJsonObject = object.getAsJsonObject("textures").getAsJsonObject("raw");

            value = asJsonObject.get("value").getAsString();
            signature = asJsonObject.get("signature").getAsString();

            fetched.add(this);
            connection.disconnect();
        } catch (Exception ignored) {

        }
    }

    public SkinUtils anyMatch() {
        return fetched.stream().filter(skinUtils -> skinUtils.getSkinOwner().equalsIgnoreCase(skinOwner)).findFirst().orElse(null);
    }

}
