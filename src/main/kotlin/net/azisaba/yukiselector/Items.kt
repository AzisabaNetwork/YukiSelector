package net.azisaba.yukiselector

import net.azisaba.yukiselector.util.State
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture
import kotlin.math.ceil
import kotlin.math.max
import org.bukkit.ChatColor as CC

class Items(private val plugin: YukiSelector) {

    val selectorItem = ItemStack(Material.INK_SACK).apply {
        durability = 4
        itemMeta = itemMeta.apply {
            displayName = "${CC.AQUA}サーバー選択"
            lore = listOf("${CC.YELLOW}クリックでサーバーを選択できます！")
        }
    }

    private fun createServerItem(name: String, players: Int, state: State?): ItemStack {
        val itemType = Material.values()
            .find { it.name == plugin.selectorConfig.getString("servers.$name.item_type") }
            ?: Material.THIN_GLASS
        val serverName = plugin.selectorConfig.getString("servers.$name.name", name)
        val description = plugin.selectorConfig.getString("servers.$name.description", "説明はありません。")
        val version = plugin.selectorConfig.getString("servers.$name.versions.recommended")
        val supportedVersions = plugin.selectorConfig.getString("servers.$name.versions.supported")

        return ItemStack(itemType).apply {
            amount = max(1, players)
            itemMeta = itemMeta.apply {
                displayName = "${CC.RESET}${CC.UNDERLINE}$serverName"
                lore = mutableListOf<String>().apply {
                    description.lines().forEach { add("${CC.GRAY}$it") }
                    if (state != null) {
                        add("")
                        add("${CC.RESET}このサーバーは $state${CC.RESET} です。")
                    }
                    if (players > 0 || version != null) {
                        add("")
                    }
                    if (players > 0) {
                        add("${CC.GRAY}現在 ${CC.YELLOW}$players${CC.GRAY} 人がプレイ中")
                    }
                    if (version != null) {
                        add(buildString {
                            append("${CC.GRAY}バージョン ${CC.GOLD}$version${CC.GRAY} 推奨")
                            if (supportedVersions != null) {
                                append("、${CC.GOLD}$supportedVersions${CC.GRAY} にも対応")
                            }
                        })
                    }
                    add("${CC.BLACK}$name")
                }
            }
        }
    }

    fun generateSelectingItems(): CompletableFuture<Pair<Int, Map<Int, ItemStack>>> {
        val future = CompletableFuture<Pair<Int, Map<Int, ItemStack>>>()

        plugin.bungee.getServers().whenComplete { servers, _ ->
            val cfs = servers.map { server ->
                plugin.bungee.getPlayerCount(server).thenApply { count ->
                    val state = State.values()
                        .find { it.name == plugin.selectorConfig.getString("servers.$server.state") }
                    state to createServerItem(server, count, state)
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

                var length = 0
                val mappedItems = mutableMapOf<Int, ItemStack>()
                    .also { map ->
                        categorized.sortedBy { it.first.ordinal }.forEach { (_, items) ->
                            items.forEachIndexed { index, item -> map[length + index] = item }
                            length += items.size
                            length = ceil(length / 9f).times(9).toInt()
                        }
                        length += 9
                        uncategorized.forEachIndexed { index, item -> map[length + index] = item }
                        length += uncategorized.size
                        length = ceil(length / 9f).times(9).toInt()
                    }

                future.complete(length to mappedItems)
            }
        }

        return future
    }
}
