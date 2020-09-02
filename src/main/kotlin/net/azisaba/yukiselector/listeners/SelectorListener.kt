package net.azisaba.yukiselector.listeners

import net.azisaba.yukiselector.YukiSelector
import net.azisaba.yukiselector.util.State
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture
import kotlin.math.ceil

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

        plugin.bungee.servers.whenComplete { servers, _ ->
            val cfs = servers.map { server ->
                plugin.bungee.getPlayerCount(server).thenApply { count ->
                    val config = plugin.selectorConfig

                    val state = State.values().find { it.name == config.getString("servers.$server.state") }
                    state to plugin.items.createServerItem(server, count, state)
                }
            }
            CompletableFuture.allOf(*cfs.toTypedArray()).whenComplete { _, _ ->
                val results = cfs.map { future -> future.get() }

                val mapped = results
                    .map { it.first }
                    .distinct()
                    .associateWith { state -> results.filter { it.first == state }.map { it.second } }

                val categorized = mapped.filter { it.key != null }.map { it.key!! to it.value }
                val uncategorized = mapped.filter { it.key == null }.flatMap { it.value }

                var now = 0
                val mappedItems = mutableMapOf<Int, ItemStack>()
                    .also { map ->
                        categorized.sortedBy { it.first.ordinal }.forEach { (_, items) ->
                            items.forEachIndexed { index, item -> map[now + index] = item }
                            now += items.size
                            now = ceil(now / 9f).times(9).toInt()
                        }
                        now += 9
                        uncategorized.forEachIndexed { index, item -> map[now + index] = item }
                        now += uncategorized.size
                        now = ceil(now / 9f).times(9).toInt()
                    }

                val inventory = plugin.server.createInventory(null, now, plugin.inventoryTitle)
                player.openInventory(inventory)
                mappedItems.forEach { (index, item) -> inventory.setItem(index, item) }
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

        plugin.bungee.getPlayerCount(serverName).whenComplete { _, _ ->
            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)

            plugin.logger.info("${player.name} が $serverName に接続中...")
            plugin.bungee.connect(player, serverName)
        }
    }
}
