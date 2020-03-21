package net.azisaba.lobby.serverselector2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemFactory {

    protected ItemFactory() {
    }

    public static List<String> createLore(String... lines) {
        return Arrays.asList(lines);
    }

    public static ItemStack create(Material type, String name, List<String> lore) {
        return create(type, (short) 1, name, lore);
    }

    public static ItemStack create(Material type, short damage, String name, List<String> lore) {
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
