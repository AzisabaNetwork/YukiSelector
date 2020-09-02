package net.azisaba.yukiselector.listeners

import net.azisaba.yukiselector.YukiSelector
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ReceiveItemListener(private val plugin: YukiSelector) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.inventory.addItem(plugin.items.selectorItem)
    }
}
