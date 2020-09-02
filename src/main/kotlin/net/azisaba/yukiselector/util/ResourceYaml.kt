package net.azisaba.yukiselector.util

import org.bukkit.configuration.file.YamlConfiguration

class ResourceYaml(private val name: String) {

    fun load(): YamlConfiguration {
        val url = javaClass.classLoader.getResource(name)
        val input = url!!.openConnection().also { it.useCaches = false }.getInputStream()
        val text = input.readBytes().decodeToString()
        return YamlConfiguration().apply { loadFromString(text) }
    }
}
