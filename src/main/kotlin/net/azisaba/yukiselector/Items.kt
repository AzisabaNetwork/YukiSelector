package net.azisaba.yukiselector

import net.azisaba.yukiselector.util.State
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
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

    fun createServerItem(name: String, players: Int, state: State?): ItemStack {
        val config = plugin.selectorConfig

        val itemType = Material.values().find { it.name == config.getString("servers.$name.item_type") }
            ?: Material.THIN_GLASS
        val serverName = config.getString("servers.$name.name", name)
        val description = config.getString("servers.$name.description", "説明はありません。")
        val version = config.getString("servers.$name.versions.recommended")
        val supportedVersions = config.getString("servers.$name.versions.supported")

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
}
