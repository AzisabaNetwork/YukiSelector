package net.azisaba.lobby.serverselector2;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServerSelector extends JavaPlugin implements Listener, PluginMessageListener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::update, 0, 20 * 5);
        getServer().getPluginManager().registerEvents(this, this);
    }

    // SERVER SELECTOR ITEM - START
    private final ItemStack selectorItem = ItemFactory.create(
            Material.INK_SACK,
            (short) 4,
            ChatColor.AQUA + "サーバー選択",
            Collections.singletonList(ChatColor.YELLOW + "クリックでサーバーを選択できます！"));

    private final Map<String, Function<ServerInfo, ItemStack>> serverMapping = new ImmutableMap.Builder<String, Function<ServerInfo, ItemStack>>()
            .put("lgw", info -> ItemFactory.create(
                    Material.BOW,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Leon Gun War",
                    Arrays.asList(
                            ChatColor.GRAY + "FPSゲームができる銃撃戦サーバーです。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.12.2" + ChatColor.GRAY + " (1.12.2-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: 銃, PvP")))
            .put("casino", info -> ItemFactory.create(
                    Material.NETHER_STAR,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Phantasy Gate",
                    Arrays.asList(
                            ChatColor.GRAY + "WGPサーバーの後継サーバーとして開発されたサーバーです。",
                            ChatColor.GRAY + "サバイバルで生活したり、稼いだお金でギャンブルをすることができます。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.12.2" + ChatColor.GRAY + " (1.12.2-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, 経済, カジノ, PvE")))
            .put("pvp", info -> ItemFactory.create(
                    Material.DIAMOND_SWORD,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "PvP",
                    Arrays.asList(
                            ChatColor.GRAY + "Mizinkobusters と siloneco の2人で作り上げるPvPサーバーです。",
                            ChatColor.GRAY + "シンプルで遊びやすいサーバーを目指しています。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.8.x" + ChatColor.GRAY + " (1.8.x-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: PvP, ミニゲーム")))
            .put("parkour", info -> ItemFactory.create(
                    Material.FEATHER,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Parkour",
                    Arrays.asList(
                            ChatColor.GRAY + "本格的なアスレチックができるサーバーです。",
                            ChatColor.GRAY + "独自のシステムと、大規模なアスレチックがあります。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.13.2" + ChatColor.GRAY + " (1.13.2-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: パルクール")))
            .put("main", info -> ItemFactory.create(
                    Material.GRASS,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Main",
                    Arrays.asList(
                            ChatColor.GRAY + "アジ鯖の中心となる生活サーバーです。",
                            ChatColor.GRAY + "初心者から上級者まで楽しめるサーバーを目指しています。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.15.2" + ChatColor.GRAY + " (1.15.2のみ)",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, 経済")))
            .put("p", info -> ItemFactory.create(
                    Material.MINECART,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "P",
                    Arrays.asList(
                            ChatColor.GRAY + "p_maikura 氏とビルダー陣が作るサーバーです。",
                            ChatColor.GRAY + "本格的な建築を見ることができます。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.13.2" + ChatColor.GRAY + " (1.13.2-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: 観光")))
            .put("pata", info -> ItemFactory.create(
                    Material.ENCHANTMENT_TABLE,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Pata",
                    Arrays.asList(
                            ChatColor.GRAY + "2016年頃に patagonia002 が作り運営していたサーバーです。",
                            ChatColor.GRAY + "バニラにはない様々な要素があります。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.8.x" + ChatColor.GRAY + " (1.8.x-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, 経済, PvE")))
            .build();

    private final String inventoryTitle = "サーバーを選択してね！";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("serverselector2.receiveitem")) {
            PlayerInventory inventory = player.getInventory();
            inventory.setItem(1, selectorItem.clone());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && selectorItem.isSimilar(item)) {
            event.setCancelled(true);
            Action action = event.getAction();
            if (action.toString().startsWith("RIGHT_CLICK_")) {
                Player player = event.getPlayer();
                player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1f, (float) Math.random() + 1f);
                openInventory(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory != null && inventory.getTitle().equals(inventoryTitle)) {
            event.setCancelled(true);
        }
    }

    public void openInventory(Player player) {
        int slotSize = (int) Math.ceil(serverMap.size() / 9f) * 9;
        int totalSize = slotSize + 9;
        Inventory inventory = getServer().createInventory(null, totalSize, inventoryTitle);
        player.openInventory(inventory);
        printInventory(player, inventory);
    }

    public void printInventory(Player player, Inventory inventory) {
        List<ServerInfo> servers = serverMap.values().stream()
                .sorted(Comparator.comparingInt(ServerInfo::getPlayerCount).reversed())
                .sorted(Comparator.comparing((Function<ServerInfo, Boolean>) info -> serverMapping.containsKey(info.getName())).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < servers.size(); i++) {
            ServerInfo info = servers.get(i);
            ItemStack item;
            if (serverMapping.containsKey(info.getName())) {
                item = serverMapping.get(info.getName()).apply(info);
            } else {
                item = ItemFactory.create(
                        Material.STAINED_GLASS_PANE,
                        (short) 15,
                        info.getName(),
                        Collections.singletonList(ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人"));
            }
            if (item != null) {
                item.setAmount(Math.max(1, info.getPlayerCount()));
            }
            int j = i;
            getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
                inventory.setItem(j, item);
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1.3f);
            }, i * 2);
        }
    }
    // SERVER SELECTOR ITEM - END

    // PLUGIN MESSAGING - START
    private final Map<String, Deque<CompletableFuture<?>>> callbacksMap = new HashMap<>();

    private void requestFuture(List<String> salt, CompletableFuture<?> future) {
        String key = String.join("", salt);
        callbacksMap.putIfAbsent(key, new ArrayDeque<>());
        callbacksMap.get(key).offer(future);
    }

    private <T> void completeFuture(List<String> salt, T obj) {
        String key = String.join("", salt);
        if (callbacksMap.containsKey(key)) {
            CompletableFuture<T> future = (CompletableFuture<T>) callbacksMap.get(key).pollFirst();
            if (future != null) {
                future.complete(obj);
            }
        }
    }

    public CompletableFuture<String[]> requestGetServers(Player player) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        requestFuture(Arrays.asList(player.getName(), "GetServers"), future);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());

        return future;
    }

    public CompletableFuture<Integer> requestPlayerCount(Player player, String server) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        requestFuture(Arrays.asList(player.getName(), "PlayerCount", server), future);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());

        return future;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("GetServers")) {
            String[] servers = in.readUTF().split(", ");

            completeFuture(Arrays.asList(player.getName(), "GetServers"), servers);
        } else if (subChannel.equals("PlayerCount")) {
            String server = in.readUTF();
            int playerCount = in.readInt();

            completeFuture(Arrays.asList(player.getName(), "PlayerCount", server), playerCount);
        }
    }
    // PLUGIN MESSAGING - END

    // SERVER UPDATING TASK - START
    private final Map<String, ServerInfo> serverMap = new HashMap<>();

    private void update() {
        Player player = Iterables.getFirst(getServer().getOnlinePlayers(), null);
        if (player == null) {
            return;
        }

        requestGetServers(player)
                .whenComplete((servers, t) -> {
                    serverMap.keySet().stream()
                            .filter(x -> Arrays.stream(servers).noneMatch(x::equals))
                            .forEach(serverMap::remove);
                    Arrays.stream(servers)
                            .forEach(x -> serverMap.putIfAbsent(x, new ServerInfo()));
                })
                .whenComplete((servers, t) -> Arrays.stream(servers)
                        .forEach(server -> requestPlayerCount(player, server)
                                .whenComplete((playerCount, t2) -> {
                                    ServerInfo info = new ServerInfo();
                                    info.setName(server);
                                    info.setPlayerCount(playerCount);
                                    serverMap.put(server, info);
                                })));
    }
    // SERVER UPDATING TASK - END
}
