package net.azisaba.yukiselector.listeners

import net.azisaba.yukiselector.YukiSelector
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

class SelectorListener(private val plugin: YukiSelector) : Listener {

    @EventHandler
    fun onSelectorOpen(event: PlayerInteractEvent) {
        if (event.item?.isSimilar(plugin.items.selectorItem) != true) {
            return
        }
        if (arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK).none { it == event.action }) {
            return
        }

        event.isCancelled = true

        val player = event.player
        player.playSound(player.location, Sound.ENTITY_BAT_TAKEOFF, 1f, 1f + Math.random().toFloat())

        plugin.items.generateSelectingItems().whenComplete { (length, items), _ ->
            val inventory = plugin.server.createInventory(null, length, plugin.inventoryTitle)
            player.openInventory(inventory)

            var counter = 0
            items.forEach { (index, item) ->
                plugin.server.scheduler.runTaskLaterAsynchronously(plugin, {
                    inventory.setItem(index, item)
                    player.playSound(player.location, Sound.ENTITY_CHICKEN_EGG, 1f, 1.3f)
                }, counter.toLong())
                counter++
            }
        }
    }

    @EventHandler
    fun onItemClick(event: InventoryClickEvent) {
        if (event.inventory?.title != plugin.inventoryTitle) {
            return
        }

        event.isCancelled = true

        val lore = event.currentItem?.itemMeta?.lore ?: return

        val serverName = ChatColor.stripColor(lore.last())
        val player = event.whoClicked as Player

        plugin.bungee.getServers().whenComplete { servers, _ ->
            servers.find { it == serverName } ?: return@whenComplete

            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)

            plugin.logger.info("${player.name} が $serverName に接続中...")
            plugin.bungee.connect(player, serverName)
        }
    }
}
