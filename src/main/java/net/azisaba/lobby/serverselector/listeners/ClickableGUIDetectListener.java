package net.azisaba.lobby.serverselector.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;
import net.azisaba.lobby.serverselector.gui.core.ClickableGUI;

@RequiredArgsConstructor
public class ClickableGUIDetectListener implements Listener {

    private final ServerSelector plugin;
    private HashMap<UUID, Long> doubleClickPreventer = new HashMap<>();

    @EventHandler
    public void clickInventory(InventoryClickEvent e) {
        if ( !(e.getWhoClicked() instanceof Player) ) {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        Inventory openingInv = e.getInventory();
        ClickableGUI gui = plugin.getGuiManager().getMatchGUI(openingInv);

        if ( gui == null ) {
            return;
        }

        if ( doubleClickPreventer.getOrDefault(p.getUniqueId(), 0L) + 100 > System.currentTimeMillis() ) {
            e.setCancelled(true);
            return;
        }

        gui.onClickInventory(e);

        doubleClickPreventer.put(p.getUniqueId(), System.currentTimeMillis());
    }
}