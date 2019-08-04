package net.azisaba.lobby.serverselector.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;

import net.azisaba.lobby.serverselector.ServerSelector;

public class BungeeUtils {

    /**
     * BungeeCordへデータを送信して、プレイヤーを他サーバーへ移動させるメソッド
     *
     * @param p          転送したいプレイヤー
     * @param serverName 転送先のサーバー名
     * @throws ChannelNotRegisteredException BungeeCordチャンネルが指定されていない場合
     */
    public static void requestSendPlayer(ServerSelector plugin, Player p, String serverName) throws ChannelNotRegisteredException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    /**
     * BungeeCordへデータを送信して、サーバーリストを取得するメソッド
     *
     * @param plugin リクエストを送信するPlugin
     * @param p      リクエストを送信するプレイヤー
     * @throws ChannelNotRegisteredException BungeeCordチャンネルが指定されていない場合
     */
    public static void requestServerList(ServerSelector plugin, Player p) throws ChannelNotRegisteredException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("GetServers");
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    /**
     * BungeeCordへデータを送信して、サーバーのプレイヤー人数を取得するメソッド
     *
     * @param plugin     リクエストを送信するPlugin
     * @param p          リクエストを送信するプレイヤー
     * @param serverName プレイヤー人数を取得したいサーバー名
     * @throws ChannelNotRegisteredException BungeeCordチャンネルが指定されていない場合
     */
    public static void requestPlayerCount(ServerSelector plugin, Player p, String serverName) throws ChannelNotRegisteredException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(serverName);
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
