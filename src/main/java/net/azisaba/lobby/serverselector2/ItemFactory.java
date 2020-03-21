package net.azisaba.lobby.serverselector2;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@UtilityClass
public class ItemFactory {

    public ItemStack create(Material type, String name, List<String> lore) {
        return create(type, (short) 0, name, lore);
    }

    public ItemStack create(Material type, short damage, String name, List<String> lore) {
        ItemStack item = new ItemStack(type);
        item.setDurability(damage);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }
            if (lore != null) {
                meta.setLore(lore);
            }
        }
        item.setItemMeta(meta);
        return item;
    }
}
