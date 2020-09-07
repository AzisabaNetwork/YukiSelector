package net.azisaba.yukiselector

import net.azisaba.yukiselector.listeners.ReceiveItemListener
import net.azisaba.yukiselector.listeners.SelectorListener
import net.azisaba.yukiselector.util.BungeeChannel
import net.azisaba.yukiselector.util.ResourceYaml
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

class YukiSelector : JavaPlugin() {

    val inventoryTitle = "サーバーを選択してね！"
    lateinit var items: Items
    lateinit var bungee: BungeeChannel
    lateinit var selectorConfig: YamlConfiguration

    override fun onEnable() {
        items = Items(this)
        bungee = BungeeChannel(this)
        selectorConfig = ResourceYaml("selector.yml").load()
        server.pluginManager.registerEvents(ReceiveItemListener(this), this)
        server.pluginManager.registerEvents(SelectorListener(this), this)
    }
}
