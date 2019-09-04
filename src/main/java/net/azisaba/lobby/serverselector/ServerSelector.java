package net.azisaba.lobby.serverselector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.azisaba.lobby.serverselector.config.DefaultConfig;
import net.azisaba.lobby.serverselector.gui.MainGUI;
import net.azisaba.lobby.serverselector.gui.core.ClickableGUIManager;
import net.azisaba.lobby.serverselector.listeners.BungeeMessageListener;
import net.azisaba.lobby.serverselector.listeners.ClickableGUIDetectListener;
import net.azisaba.lobby.serverselector.listeners.GiveSelectorItemListener;
import net.azisaba.lobby.serverselector.listeners.ItemClickListener;
import net.azisaba.lobby.serverselector.listeners.PreventDropListener;
import net.azisaba.lobby.serverselector.listeners.PreventSwapItemListener;
import net.azisaba.lobby.serverselector.task.UpdatePlayerCountTask;
import net.azisaba.lobby.serverselector.util.PlayerCounter;
import net.azisaba.lobby.serverselector.utils.Chat;
import net.azisaba.lobby.serverselector.utils.ItemHelper;

import lombok.Getter;

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
    // プレイヤー人数を格納するクラス
    private PlayerCounter playerCounter = new PlayerCounter();
    // プレイヤーの人数を更新するタスク
    private UpdatePlayerCountTask playerCountTask;

    // サーバー選択用アイテム
    private ItemStack selectorItem = ItemHelper.create(Material.COMPASS, Chat.f("&cサーバー選択"), Chat.f("&eクリックでサーバーを選択できます！"));

    @Override
    public void onEnable() {
        // Configの読み込み
        this.defaultConfig = new DefaultConfig(this);
        this.defaultConfig.loadConfig();

        // PluginMessagingChannelの登録
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessageListener(this));

        // タスクを起動
        playerCountTask = new UpdatePlayerCountTask(this);
        playerCountTask.runTaskTimer(this, 0, 20);

        // サーバーバージョンを取得
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        int versionNumber = Integer.parseInt(version.substring(3).substring(0, version.substring(3).indexOf("_")));

        // GUIの登録
        MainGUI gui = new MainGUI(this, versionNumber);
        guiManager.registerGUI(gui);


        // Listenerの登録
        Bukkit.getPluginManager().registerEvents(new ItemClickListener(this,versionNumber), this);
        Bukkit.getPluginManager().registerEvents(new GiveSelectorItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ClickableGUIDetectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PreventDropListener(this), this);
        // 1.9以降の場合はオフハンド無効化Listenerを登録
        if (versionNumber >= 9) {
            Bukkit.getPluginManager().registerEvents(new PreventSwapItemListener(this), this);
        }

        Bukkit.getPluginManager().registerEvents(gui, this);

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {

        // タスクをキャンセル
        if ( playerCountTask != null ) {
            playerCountTask.cancel();
        }

        Bukkit.getLogger().info(getName() + " disabled.");
    }
}
