package net.azisaba.lobby.serverselector.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;

import net.azisaba.lobby.serverselector.ServerSelector;
import net.azisaba.lobby.serverselector.events.PlayerCountUpdateEvent;
import net.azisaba.lobby.serverselector.gui.core.ClickableGUI;
import net.azisaba.lobby.serverselector.utils.BungeeUtils;
import net.azisaba.lobby.serverselector.utils.Chat;
import net.azisaba.lobby.serverselector.utils.ItemHelper;

public class MainGUI extends ClickableGUI implements Listener {

    private final ServerSelector plugin;
    private final int versionNumber;

    public MainGUI(ServerSelector plugin, int versionNumber) {
        this.plugin = plugin;
        this.versionNumber = versionNumber;
        // アイテムの初期化
        initializeItems();
    }

    private Inventory inv = null;
    private ItemStack lgw, parkour, pvp, survival, casino, pata, sightseeing, fsw;
    private ItemStack close;

    @Override
    public Inventory createInventory(Player p) {
//        if ( inv == null ) {
        // インベントリ作成
        inv = Bukkit.createInventory(null, getSize(), getTitle());

        // アイテムをセット
        List<ItemStack> items = Arrays.asList(survival, lgw, pvp, casino, pata, sightseeing, parkour, fsw);
        // 配置場所を取得
        List<Integer> positions = getPosition(items.size());

        // 各場所に配置
        for ( int i = 0; i < items.size(); i++ ) {
            inv.setItem(positions.get(i), items.get(i));
        }

        // 閉じるボタンを配置
        inv.setItem(getSize() - 1, close);
//        }

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
        String serverName = getServerNameFromItem(item);
        // サーバーが指定されていない場合はreturn
        if ( serverName == null ) {
            return;
        }

        // サーバーに転送する
        try {
            BungeeUtils.requestSendPlayer(plugin, p, serverName);
        } catch ( ChannelNotRegisteredException ex ) {
            // サーバーがBungeeCordにつながっていない場合は警告を出力
            plugin.getLogger().warning(Chat.f("PluginMessaginChannelが登録されていないため送信できません！ (Player={0}, Server={1})", p.getName(), serverName));
            p.sendMessage(Chat.f("&cサーバー移動に失敗しました。運営に報告してください。"));
        }

        // インベントリを閉じる
        p.closeInventory();
    }

    /**
     * 人数が変わったときにアイテムの情報を書き換えるListener
     */
    @EventHandler
    public void onPlayerCountUpdate(PlayerCountUpdateEvent e) {
        String serverName = e.getServerName();
        int count = e.getPlayerCount();

        ItemStack item = getItemFromServerName(serverName);

        // アイテムが取得できなかった場合return
        if ( item == null ) {
            return;
        }

        // 更新
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>(meta.getLore());

        // loreの書き換え
        lore.set(2, Chat.f("&aオンライン人数: &e{0}人", count));

        // アイテムにセット
        meta.setLore(lore);
        // ItemMetaのアップデート
        item.setItemMeta(meta);
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
     * アイテムからBungeeCordに登録されているサーバー名を取得する
     *
     * @param item 取得したいアイテム
     * @return BungeeCordに登録されているサーバー名。見つからなければnull
     */
    private String getServerNameFromItem(ItemStack item) {
        if ( item.isSimilar(lgw) ) {
            return "lgw";
        } else if ( item.isSimilar(parkour) ) {
            return "parkour";
        } else if ( item.isSimilar(pvp) ) {
            return "pvp";
        } else if ( item.isSimilar(survival) ) {
            return "main";
        } else if ( item.isSimilar(casino) ) {
            return "casino";
        } else if ( item.isSimilar(pata) ) {
            return "pata";
        } else if ( item.isSimilar(sightseeing) ) {
            return "p";
        } else if ( item.isSimilar(fsw) ) {
            return "fsw";
        } else {
            return null;
        }
    }

    /**
     * BungeeCordに登録されているサーバー名からアイテムを取得する
     *
     * @param serverName 取得したいアイテムのサーバー名
     * @return このクラスのフィールドにあるアイテム、存在しない場合null
     */
    private ItemStack getItemFromServerName(String serverName) {
        switch (serverName) {
        case "lgw":
            return lgw;
        case "parkour":
            return parkour;
        case "pvp":
            return pvp;
        case "main":
            return survival;
        case "casino":
            return casino;
        case "pata":
            return pata;
        case "p":
            return sightseeing;
        default:
            return null;
        }
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
        case 9:
            return Arrays.asList(10, 12, 14, 16, 22, 28, 30, 32, 34);
        case 10:
            return Arrays.asList(10, 12, 14, 16, 20, 24, 28, 30, 32, 34);
        case 11:
            return Arrays.asList(10, 12, 14, 16, 20, 22, 24, 28, 30, 32, 34);
        }

        return Collections.emptyList();
    }

    /**
     * 各アイテムをフィールドに設定します
     */
    private void initializeItems() {

        String latestVersion = plugin.getDefaultConfig().getLatestMinecraftVersion();

        if ( lgw == null )
            lgw = ItemHelper.create(Material.BOW, Chat.f("&e&lLeonGunWar"), getLore("銃撃戦", "1.12.2", "1.12.2", latestVersion));
        if ( parkour == null )
            parkour = ItemHelper.create(Material.DIAMOND_BOOTS, Chat.f("&e&lParkour"), getLore("パルクール", "1.13.2", "1.13.2", "1.13.2"));
        if ( pvp == null )
            pvp = ItemHelper.create(Material.DIAMOND_SWORD, Chat.f("&e&lPvP"), getLore("PvP", "1.8.x", "1.8.x", latestVersion));
        if ( survival == null ) {
            String materialName;
            if ( versionNumber >= 13 ) {
                materialName = "GRASS_BLOCK";
            } else {
                materialName = "GRASS";
            }
            survival = ItemHelper.create(Material.valueOf(materialName), Chat.f("&e&l生活"), getLore("サバイバル", "1.13.2", "1.13.2", latestVersion));
        }
        if ( casino == null )
            casino = ItemHelper.create(Material.GOLD_NUGGET, Chat.f("&e&lWGP"), getLore("カジノ", "1.12.2", "1.12.2", latestVersion));
        if ( pata == null ) {
            if ( versionNumber >= 13 ) {
                pata = ItemHelper.create(Material.valueOf("ZOMBIE_HEAD"), Chat.f("&e&lパタ"), getLore("PvE", "1.8.x", "1.8.x", latestVersion));
            } else {
                pata = ItemHelper.createItem(Material.valueOf("SKULL_ITEM"), 2, Chat.f("&e&lパタ"), getLore("PvE", "1.8.x", "1.8.x", latestVersion));
            }
        }
        if ( sightseeing == null )
            sightseeing = ItemHelper.create(Material.MINECART, Chat.f("&e&l観光"), getLore("観光", "1.13.2", "1.13.2", latestVersion));
        if ( fsw == null ) {
            String materialName;
            if ( versionNumber >= 13 ) {
                materialName = "GOLDEN_SWORD";
            } else {
                materialName = "GOLD_SWORD";
            }
            fsw = ItemHelper.create(Material.valueOf(materialName), Chat.f("&e&lFSW"), getLore("サバイバル + PvP", "1.11.x", "1.11.x", latestVersion));
        }
        if ( close == null ) {
            close = ItemHelper.create(Material.BARRIER, Chat.f("&c閉じる"));
        }
    }

    private String[] getLore(String serverType, String suggestVersion, String minVersion, String maxVersion) {
        String[] lore = { Chat.f("&c{0}サーバー！", serverType), "", Chat.f("&aオンライン人数: &e-人"), Chat.f("&a推奨バージョン: &6{0}", suggestVersion), Chat.f("&7(参加可能バージョン: {0}-{1})", minVersion, maxVersion) };

        // minVersionとmaxVersionが同じの場合メッセージを変更
        if ( minVersion.equals(maxVersion) ) {
            lore[lore.length - 1] = Chat.f("&7(参加可能バージョン: {0}のみ)", minVersion);
        }
        return lore;
    }
}
