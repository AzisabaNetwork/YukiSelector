package net.azisaba.lobby.serverselector.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import net.azisaba.lobby.serverselector.ServerSelector;

import lombok.RequiredArgsConstructor;

/**
 * 1.9
 * 以前ならPlayerSwapHandItemsEventが存在しないためエラーとなりますが、その場合はListenerが登録されないため問題ありません
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class PreventSwapItemListener implements Listener {

    private final ServerSelector plugin;

    /**
     * オフハンドにサーバー選択アイテムを持つことを阻止するListener
     *
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onSwapItem(PlayerSwapHandItemsEvent e) {
        ItemStack main = e.getMainHandItem();
        ItemStack off = e.getOffHandItem();

        // サーバー選択アイテムならキャンセル
        if ( (main != null && main.isSimilar(plugin.getSelectorItem())) || (off != null && off.isSimilar(plugin.getSelectorItem())) ) {
            e.setCancelled(true);
        }
    }

}
