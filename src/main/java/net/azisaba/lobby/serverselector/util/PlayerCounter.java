package net.azisaba.lobby.serverselector.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.azisaba.lobby.serverselector.events.PlayerCountUpdateEvent;
import net.azisaba.lobby.serverselector.utils.Chat;

/**
 * プレイヤーの人数を格納しておくクラス <br>
 * 反映に時間がかかるためupdateメソッドなどは存在していない <br>
 * 定期的にタイマーによってアップデートされ、値がアップデートされる
 *
 * @author siloneco
 *
 */
@NoArgsConstructor
public class PlayerCounter {

    // サーバーリスト
    @Getter
    @Setter
    private List<String> servers = new ArrayList<String>();
    // サーバー情報を格納するHashMap
    private HashMap<String, PlayerCountInfo> playerCounts = new HashMap<>();

    /**
     * サーバーのプレイヤー人数を取得する
     *
     * @param serverName 人数を取得したいサーバー名
     * @return サーバーの人数
     * @throws IllegalStateException サーバー情報がまだ取得できていない、もしくはサーバーが存在しない場合
     */
    public int getPlayerCount(String serverName) throws IllegalStateException {
        if ( !playerCounts.containsKey(serverName) ) {
            throw new IllegalStateException(Chat.f("{0} というサーバーの情報は取得できていません"));
        }

        return playerCounts.get(serverName).getPlayerCount();
    }

    /**
     * サーバー情報が最後にアップデートされたミリ秒を取得する
     *
     * @param serverName 取得したいサーバー名
     * @return 最後にアップデートされたミリ秒
     * @throws IllegalStateException サーバー情報がまだ取得できていない、もしくはサーバーが存在しない場合
     */
    public long getLastUpdated(String serverName) throws IllegalStateException {
        if ( !playerCounts.containsKey(serverName) ) {
            throw new IllegalStateException(Chat.f("{0} というサーバーの情報は取得できていません"));
        }

        return playerCounts.get(serverName).getLastUpadted();
    }

    /**
     * プレイヤー人数を更新する <br>
     * {@link PlayerCountUpdateEvent} が呼ばれます
     *
     * @param serverName 更新したいサーバー名
     * @param count      サーバーの人数
     */
    public void setPlayerCount(String serverName, int count) {
        playerCounts.put(serverName, new PlayerCountInfo(count, System.currentTimeMillis()));
        Bukkit.getPluginManager().callEvent(new PlayerCountUpdateEvent(serverName, count));
    }

    @Getter
    @RequiredArgsConstructor
    private class PlayerCountInfo {
        private final int playerCount;
        private final long lastUpadted;
    }
}
