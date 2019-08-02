package net.azisaba.lobby.serverselector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;

import lombok.Getter;
import net.azisaba.lobby.serverselector.config.DefaultConfig;
import net.azisaba.lobby.serverselector.gui.MainGUI;
import net.azisaba.lobby.serverselector.gui.core.ClickableGUIManager;
import net.azisaba.lobby.serverselector.listeners.ClickableGUIDetectListener;
import net.azisaba.lobby.serverselector.listeners.GiveSelectorItemListener;
import net.azisaba.lobby.serverselector.listeners.ItemClickListener;
import net.azisaba.lobby.serverselector.utils.Chat;
import net.azisaba.lobby.serverselector.utils.ItemHelper;

/**
 *
 * このPluginのメインクラス。各インスタンスとそのGetterがある
 *
 * @author siloneco
 *
 */
@Getter
public class ServerSelector extends JavaPlugin {

    // 全部のClickableGUIを管理するインスタンス
    private ClickableGUIManager guiManager = new ClickableGUIManager();
    // config.ymlの設定
    private DefaultConfig defaultConfig;

    // サーバー選択用アイテム
    private ItemStack selectorItem = ItemHelper.create(Material.COMPASS, Chat.f("&cサーバー選択"), Chat.f("&eクリックでサーバーを選択できます！"));

    @Override
    public void onEnable() {
        // Configの読み込み
        this.defaultConfig = new DefaultConfig(this);
        this.defaultConfig.loadConfig();

        // GUIの登録
        guiManager.registerGUI(new MainGUI(this));

        // Listenerの登録
        Bukkit.getPluginManager().registerEvents(new ItemClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GiveSelectorItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ClickableGUIDetectListener(this), this);

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    /**
     * BungeeCordへデータを送信して、プレイヤーを他サーバーへ移動させるメソッド <br>
     *
     * @param p          転送したいプレイヤー
     * @param serverName 転送先のサーバー名
     * @throws ChannelNotRegisteredException BungeeCordチャンネルが指定されていない、つまりBungeeCordに接続されていないサーバーである場合
     */
    public void sendPlayer(Player p, String serverName) throws ChannelNotRegisteredException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }
}
