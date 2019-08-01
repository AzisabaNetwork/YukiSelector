package net.azisaba.lobby.serverselector.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;
import net.azisaba.lobby.serverselector.gui.MainGUI;

/**
 *
 * プレイヤーがサーバー選択アイテムをクリックしたときにGUIを開くListenerクラス <br>
 * プレイヤーが参加したときにアイテムを配布するListenerは {@link GiveSelectorItemListener} で担当する
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class ItemClickListener implements Listener {

    private final ServerSelector plugin;

    /**
     * 権限持ちがWorldGuardの移動に引っかからないように、priorityはLOWEST
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onClickItem(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        // 手に何も持っていない場合return
        if ( item == null ) {
            return;
        }
        // サーバー選択アイテムではない場合return
        if ( !item.isSimilar(plugin.getSelectorItem()) ) {
            return;
        }
        // イベントをキャンセル
        e.setCancelled(true);

        // 右クリックでなければreturn
        if ( !e.getAction().toString().startsWith("RIGHT_CLICK_") ) {
            return;
        }

        // インベントリを取得しプレイヤーに開かせる
        openInventory(p);
    }

    /**
     * インベントリを開いてアイテムをクリックした場合もGUIを開くListener
     */
    @EventHandler
    public void onClickItemOnInventory(InventoryClickEvent e) {
        // プレイヤーではない場合return
        if ( !(e.getWhoClicked() instanceof Player) ) {
            return;
        }
        Player p = (Player) e.getWhoClicked();

        // クリックしたアイテムとカーソルのアイテムの両方がサーバー選択アイテムではない場合return
        boolean isClickedItemSelector = e.getCurrentItem() != null && e.getCurrentItem().isSimilar(plugin.getSelectorItem());
        boolean isCursorItemSelector = e.getCursor() != null && e.getCursor().isSimilar(plugin.getSelectorItem());

        if ( !isClickedItemSelector && !isCursorItemSelector ) {
            return;
        }

        // イベントをキャンセル
        e.setCancelled(true);

        // インベントリを開かせる
        openInventory(p);
    }

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

    /**
     * プレイヤーにサーバー選択インベントリを開かせるメソッド
     *
     * @param player サーバー選択インベントリを開かせるプレイヤー。既に開いているインベントリは強制的に閉じられる
     */
    private void openInventory(Player player) {
        Inventory inv = plugin.getGuiManager().getMatchInstance(MainGUI.class).createInventory(player);
        player.openInventory(inv);
    }
}
