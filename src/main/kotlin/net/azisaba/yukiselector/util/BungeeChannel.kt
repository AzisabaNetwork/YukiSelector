package net.azisaba.yukiselector.util

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.concurrent.CompletableFuture

class BungeeChannel(private val plugin: Plugin) {

    private val futures = mutableListOf<Pair<Pair<String, Array<*>>, CompletableFuture<*>>>()

    init {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this::onReceive)
    }

    private fun onReceive(channel: String, player: Player, message: ByteArray) {
        if (channel != "BungeeCord") return

        val i = DataInputStream(message.inputStream())
        val subChannel = i.readUTF()

        when (subChannel) {
            "GetServers" -> {
                val servers = i.readUTF().split(", ")
                emptyArray<Any>() to servers
            }
            "PlayerCount" -> {
                val serverName = i.readUTF()
                val playerCount = i.readInt()
                arrayOf(serverName) to playerCount
            }
            else -> return
        }.also { (checks, response) ->
            futures
                .filter { it.first.first == subChannel }
                .filter { it.first.second.contentEquals(checks) }
                .onEach { (it.second as CompletableFuture<Any>).complete(response) }
                .forEach { futures.remove(it) }
        }
    }

    fun getServers(): CompletableFuture<List<String>> {
        return CompletableFuture<List<String>>().apply {
            futures.add(("GetServers" to emptyArray<Any>()) to this)

            val player = plugin.server.onlinePlayers.first()
            val message = ByteArrayOutputStream().also {
                DataOutputStream(it).apply {
                    writeUTF("GetServers")
                }
            }.toByteArray()
            player.sendPluginMessage(plugin, "BungeeCord", message)
        }
    }

    fun getPlayerCount(serverName: String): CompletableFuture<Int> {
        return CompletableFuture<Int>().apply {
            futures.add(("PlayerCount" to arrayOf(serverName)) to this)

            val player = plugin.server.onlinePlayers.first()
            val message = ByteArrayOutputStream().also {
                DataOutputStream(it).apply {
                    writeUTF("PlayerCount")
                    writeUTF(serverName)
                }
            }.toByteArray()
            player.sendPluginMessage(plugin, "BungeeCord", message)
        }
    }

    fun connect(player: Player, serverName: String) {
        val message = ByteArrayOutputStream().also {
            DataOutputStream(it).apply {
                writeUTF("Connect")
                writeUTF(serverName)
            }
        }.toByteArray()
        player.sendPluginMessage(plugin, "BungeeCord", message)
    }
}
