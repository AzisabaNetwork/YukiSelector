package net.azisaba.lobby.serverselector.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.serverselector.ServerSelector;
import net.azisaba.lobby.serverselector.gui.core.ClickableGUI;
import net.azisaba.lobby.serverselector.utils.Chat;
import net.azisaba.lobby.serverselector.utils.ItemHelper;

@RequiredArgsConstructor
public class MainGUI extends ClickableGUI {

    private final ServerSelector plugin;

    private Inventory inv = null;
    private ItemStack lgw, parkour, pvp, survival, casino, pata, sightseeing;
    private ItemStack close;

    @Override
    public Inventory createInventory(Player p) {
        if ( inv == null ) {
            // インベントリ作成
            inv = Bukkit.createInventory(null, getSize(), getTitle());

            // アイテムをセット
            initializeItems();

            // アイテムをセット
            List<ItemStack> items = Arrays.asList(survival, lgw, pvp, casino, pata, sightseeing);
            // 配置場所を取得
            List<Integer> positions = getPosition(items.size());

            // 各場所に配置
            for ( int i = 0; i < items.size(); i++ ) {
                inv.setItem(positions.get(i), items.get(i));
            }

            // 閉じるボタンを配置
            inv.setItem(getSize() - 1, close);
        }

        return inv;
    }

    @Override
    public void onClickInventory(InventoryClickEvent e) {
        // イベントをキャンセル
        e.setCancelled(true);

        // クリックしたアイテムを取得
        ItemStack item = e.getCurrentItem();
        // 無いならreturn
        if ( item == null ) {
            return;
        }

        Player p = (Player) e.getWhoClicked();

        // 閉じるボタンなら閉じてreturn
        if ( item.isSimilar(close) ) {
            p.closeInventory();
            return;
        }

        // アイテムからサーバーを取得する
        String serverName = null;
        if ( item.isSimilar(lgw) ) {
            serverName = "lgw";
        } else if ( item.isSimilar(parkour) ) {
            serverName = "parkour";
        } else if ( item.isSimilar(pvp) ) {
            serverName = "pvp";
        } else if ( item.isSimilar(survival) ) {
            serverName = "main";
        } else if ( item.isSimilar(casino) ) {
            serverName = "casino";
        } else if ( item.isSimilar(pata) ) {
            serverName = "pata";
        } else if (item.isSimilar(sightseeing)) {
            serverName = "p";
        }

        // サーバーが指定されていない場合はreturn
        if ( serverName == null ) {
            return;
        }

        // サーバーに転送する
        try {
            plugin.sendPlayer(p, serverName);
        } catch ( ChannelNotRegisteredException ex ) {
            // サーバーがBungeeCordにつながっていない場合は警告を出力
            plugin.getLogger().warning(Chat.f("BungeeCordに繋がっていないサーバーのためプレイヤーを転送できません！ (Player={0}, Server={1})", p.getName(), serverName));
            p.sendMessage(Chat.f("&cサーバー移動に失敗しました。運営に報告してください。"));
        }

        // インベントリを閉じる
        p.closeInventory();
    }

    @Override
    public int getSize() {
        return 9 * 5;
    }

    @Override
    public String getTitle() {
        return Chat.f("&cServer Selector");
    }

    /**
     * 指定された数のアイテムをインベントリの適切な位置(int)のリストを返すメソッド <br>
     * 指定されていない数のitemAmountだった場合は Collections.emptyList() を返します
     *
     * @param itemAmount 配置するアイテムの数
     * @return 各場所のスロット番号のリスト。想定外のアイテム数の場合 Collections.emptyList() を返す
     */
    private List<Integer> getPosition(int itemAmount) {
        switch (itemAmount) {
        case 1:
            return Arrays.asList(22);
        case 2:
            return Arrays.asList(20, 24);
        case 3:
            return Arrays.asList(20, 22, 24);
        case 4:
            return Arrays.asList(11, 15, 29, 33);
        case 5:
            return Arrays.asList(11, 15, 22, 29, 33);
        case 6:
            return Arrays.asList(11, 13, 15, 29, 31, 33);
        case 7:
            return Arrays.asList(10, 12, 14, 16, 29, 31, 33);
        case 8:
            return Arrays.asList(10, 12, 14, 16, 28, 30, 32, 34);
        }

        return Collections.emptyList();
    }

    /**
     * 各アイテムをフィールドに設定します
     */
    private void initializeItems() {

        String latestVersion = plugin.getDefaultConfig().getLatestMinecraftVersion();

        if ( lgw == null )
            lgw = ItemHelper.create(Material.BOW, Chat.f("&e&lLeonGunWar"), Chat.f("&c銃撃戦サーバー！"), "", Chat.f("&a推奨バージョン: &61.12.2"), Chat.f("&7(参加可能バージョン: 1.12.2-{0})", latestVersion));
        if ( parkour == null )
            parkour = ItemHelper.create(Material.DIAMOND_BOOTS, Chat.f("&e&lParkour"), Chat.f("&cパルクールサーバー！"), "", Chat.f("&a推奨バージョン: &61.13.2"), Chat.f("&7(参加可能バージョン: 1.13.2-{0})", latestVersion));
        if ( pvp == null )
            pvp = ItemHelper.create(Material.DIAMOND_SWORD, Chat.f("&e&lPvP"), Chat.f("&cPvPサーバー！"), "", Chat.f("&a推奨バージョン: &61.8.x"), Chat.f("&7(参加可能バージョン: 1.8.x-{0})", latestVersion));
        if ( survival == null )
            survival = ItemHelper.create(Material.GRASS_BLOCK, Chat.f("&e&l生活"), Chat.f("&cサバイバルサーバー！"), "", Chat.f("&a推奨バージョン: &61.13.2"), Chat.f("&7(参加可能バージョン: 1.13.2-{0})", latestVersion));
        if ( casino == null )
            casino = ItemHelper.create(Material.GOLD_NUGGET, Chat.f("&e&lWGP"), Chat.f("&cカジノサーバー！"), "", Chat.f("&a推奨バージョン: &61.12.2"), Chat.f("&7(参加可能バージョン: 1.12.2-{0})", latestVersion));
        if ( pata == null )
            pata = ItemHelper.create(Material.ZOMBIE_HEAD, Chat.f("&e&lパタ"), Chat.f("&cPvEサーバー！"), "", Chat.f("&a推奨バージョン: &61.8.x"), Chat.f("&7(参加可能バージョン: 1.8.x-{0})", latestVersion));
        if ( sightseeing == null )
            sightseeing = ItemHelper.create(Material.MINECART, Chat.f("&e&l観光"), Chat.f("&c観光サーバー！"), "", Chat.f("&a推奨バージョン: &61.13.2"), Chat.f("&7(参加可能バージョン: 1.13.2-{0})", latestVersion));
        if ( close == null ) {
            close = ItemHelper.create(Material.BARRIER, Chat.f("&c閉じる"));
        }
    }
}
