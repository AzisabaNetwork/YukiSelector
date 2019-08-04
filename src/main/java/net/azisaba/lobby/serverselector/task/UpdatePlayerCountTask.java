package net.azisaba.lobby.serverselector.task;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;
import net.azisaba.lobby.serverselector.utils.BungeeUtils;

/**
 * 定期的に全サーバーのプレイヤー人数を取得するタスク
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class UpdatePlayerCountTask extends BukkitRunnable {

    private final ServerSelector plugin;

    @Override
    public void run() {
        // プレイヤーが居なかったらreturn
        if ( Bukkit.getOnlinePlayers().size() <= 0 ) {
            return;
        }

        // プレイヤーを1人取得
        Player p = Bukkit.getOnlinePlayers().iterator().next();

        // サーバーリストを取得
        List<String> servers = plugin.getPlayerCounter().getServers();
        // 空の場合サーバーリストをリクエスト
        if ( servers == null || servers.isEmpty() ) {
            BungeeUtils.requestServerList(plugin, p);
            return;
        }

        // 各サーバーの人数を更新
        servers.forEach(serverName -> BungeeUtils.requestPlayerCount(plugin, p, serverName));
    }
}
