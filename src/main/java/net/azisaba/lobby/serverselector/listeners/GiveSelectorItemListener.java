package net.azisaba.lobby.serverselector.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;

/**
 *
 * サーバーに参加したときにプレイヤーのインベントリにサーバー選択アイテムをセットするListener <br>
 * クリック時の処理は {@link ItemClickListener} によって処理される
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class GiveSelectorItemListener implements Listener {

    private final ServerSelector plugin;

    /**
     * プレイヤーが参加したときにインベントリにサーバー選択アイテムを追加するListener
     */
    @EventHandler
    public void onJoinServer(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // アイテムをセット
        p.getInventory().setItem(4, plugin.getSelectorItem());
    }
}
