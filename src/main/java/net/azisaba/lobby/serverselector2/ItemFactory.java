package net.azisaba.lobby.serverselector2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemFactory {

    public static ItemStack create(Material type, String name, List<String> lore) {
        return create(type, (short) 0, name, lore);
    }

    public static ItemStack create(Material type, short damage, String name, List<String> lore) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.setDurability(damage);
        return item;
    }
}
