package net.azisaba.lobby.serverselector.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;

/**
 *
 * サーバー人数を受け取って設定するListener
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class BungeeMessageListener implements PluginMessageListener {

    private final ServerSelector plugin;

    /**
     * PluginMessageを受け取ったとき、
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if ( !channel.equals("BungeeCord") ) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        // リクエスト内容がプレイヤー人数の場合
        if ( subchannel.equals("PlayerCount") ) {

            // サーバー名とプレイヤーの人数を取得
            String server = in.readUTF();
            int playerCount = in.readInt();

            // データを更新
            plugin.getPlayerCounter().setPlayerCount(server, playerCount);
            return;
        }

        // リクエスト内容がサーバーリストの場合
        if ( subchannel.equals("GetServers") ) {

            // リスト形式で取得
            List<String> serverList = Arrays.asList(in.readUTF().split(", "));

            // サーバーリストを更新
            plugin.getPlayerCounter().setServers(serverList);
            return;
        }
    }
}
