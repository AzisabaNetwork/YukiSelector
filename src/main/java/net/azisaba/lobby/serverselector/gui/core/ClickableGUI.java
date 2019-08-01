package net.azisaba.lobby.serverselector.gui.core;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class ClickableGUI {

    public abstract int getSize();

    public abstract String getTitle();

    public abstract Inventory createInventory(Player p);

    public abstract void onClickInventory(InventoryClickEvent e);

    @SuppressWarnings("deprecation")
    public boolean isSameInventory(Inventory inv) {
        return inv.getSize() == getSize() && getTitle().equals(inv.getTitle());
    }
}