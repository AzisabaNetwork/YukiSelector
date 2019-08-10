package net.azisaba.lobby.serverselector.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;

@RequiredArgsConstructor
public class PreventDropListener implements Listener {

    private final ServerSelector plugin;

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if ( plugin.getSelectorItem().isSimilar(e.getItemDrop().getItemStack()) ) {
            e.setCancelled(true);
        }
    }
}
