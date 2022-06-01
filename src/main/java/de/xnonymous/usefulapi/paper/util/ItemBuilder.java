package de.xnonymous.usefulapi.paper.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ItemBuilder {

    private Material material;
    private String displayName;
    private String localizedName;
    private String skull;
    private List<String> lores;
    private int color;
    private int amount;
    private boolean custom;

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material, amount == 0 ? 1 : amount, (short) color);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLocalizedName(localizedName);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);

        if (itemStack.getItemMeta() instanceof SkullMeta) {
            SkullMeta itemMeta1 = (SkullMeta) itemStack.getItemMeta();
            itemMeta1.setOwner(skull);
            itemStack.setItemMeta(itemMeta1);

            if (custom) {
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", skull));
                try {
                    Field profileField = itemMeta1.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(itemMeta1, profile);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                itemStack.setItemMeta(itemMeta1);
            }
        }
        return itemStack;
    }
}
