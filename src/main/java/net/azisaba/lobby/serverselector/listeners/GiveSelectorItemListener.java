package net.azisaba.lobby.serverselector.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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

    /**
     * プレイヤー退出時にサーバー選択アイテムを削除するListener <br>
     * Pluginを抜いた後にプレイヤーデータをいじってアイテムを削除しなければならなくなるのを防ぐため
     * @param e
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        // スロット4のアイテムを取得
        ItemStack item = p.getInventory().getItem(4);

        // nullならreturn
        if ( item == null ) {
            return;
        }
        if ( item.isSimilar(plugin.getSelectorItem()) ) {
            p.getInventory().clear(4);
        }
    }
}
