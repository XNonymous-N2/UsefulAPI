package de.xnonymous.usefulapi.paper.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

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
        }
        return itemStack;
    }
}
