package net.azisaba.yukiselector.util

import org.bukkit.ChatColor

enum class State(private val state: String) {
    OPEN("${ChatColor.GREEN}公開中"),
    SYSTEM_OPEN("${ChatColor.DARK_GRAY}システム"),
    DEVELOP("${ChatColor.AQUA}開発中");

    override fun toString(): String {
        return state
    }
}
