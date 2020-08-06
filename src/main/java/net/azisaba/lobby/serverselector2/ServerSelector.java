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
import org.bukkit.inventory.meta.ItemMeta;
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
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.12.2" + ChatColor.GRAY + " (1.12.2-1.16.x)",
                            "",
                            ChatColor.GRAY + "タグ: 銃, PvP")))
            .put("casino", info -> ItemFactory.create(
                    Material.NETHER_STAR,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Phantasy Gate",
                    Arrays.asList(
                            ChatColor.GRAY + "WGPサーバーの後継サーバーとして",
                            ChatColor.GRAY + "開発されたサーバーです。",
                            ChatColor.GRAY + "サバイバルで生活したり稼いだお金で",
                            ChatColor.GRAY + "ギャンブルをすることができます。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.12.2" + ChatColor.GRAY + " (1.12.2-1.15.2)",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, 経済, カジノ, PvE")))
            .put("pvp", info -> ItemFactory.create(
                    Material.DIAMOND_SWORD,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "PvP",
                    Arrays.asList(
                            ChatColor.GRAY + "Mizinkobusters と siloneco の",
                            ChatColor.GRAY + "2人で作り上げるPvPサーバーです。",
                            ChatColor.GRAY + "シンプルで遊びやすいサーバーを",
                            ChatColor.GRAY + "目指しています。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.8.x" + ChatColor.GRAY + " (1.8.x-1.16.x)",
                            "",
                            ChatColor.GRAY + "タグ: PvP, ミニゲーム")))
            .put("parkour", info -> ItemFactory.create(
                    Material.FEATHER,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Parkour",
                    Arrays.asList(
                            ChatColor.GRAY + "本格的なアスレチックができる",
                            ChatColor.GRAY + "サーバーです。",
                            ChatColor.GRAY + "独自のシステムと大規模なアスレチック",
                            ChatColor.GRAY + "があります。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.13.2" + ChatColor.GRAY + " (1.13.2のみ)",
                            "",
                            ChatColor.GRAY + "タグ: パルクール")))
            .put("main", info -> ItemFactory.create(
                    Material.GRASS,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Main",
                    Arrays.asList(
                            ChatColor.GRAY + "アジ鯖の中心となる生活サーバーです。",
                            ChatColor.GRAY + "初心者から上級者まで楽しめる",
                            ChatColor.GRAY + "サーバーを目指しています。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.15.2" + ChatColor.GRAY + " (1.15.2のみ)",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, 経済")))
            .put("rpg", info -> ItemFactory.create(
                    Material.CHEST,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Role-Playing Game",
                    Arrays.asList(
                            ChatColor.GRAY + "RPG鯖主率いる運営たち独特の世界観が入り混じったサーバーです。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.15.2" + ChatColor.GRAY + " 1.15.2~1.16.x",
                            "",
                            ChatColor.GRAY + "タグ: 銃, PvE, サバイバル, 経済")))
            .put("lobby", info -> ItemFactory.create(
                    Material.STONE,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Lobby",
                    Arrays.asList(
                            ChatColor.GRAY + "AzisabaNetWorkの中心です。",
                            ChatColor.GRAY + "このサーバーがなければアジ鯖には入れません！",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.8" + ChatColor.GRAY + " 1.8~1.16.x",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, PvP")))
            .put("afk", info -> ItemFactory.create(
                    Material.WATER_BUCKET,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Role-Playing Game",
                    Arrays.asList(
                            ChatColor.GRAY + "放置専用サーバーです。",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.8" + ChatColor.GRAY + " 1.8~1.16.x",
                            "",
                            ChatColor.GRAY + "タグ: AFK")))
            .put("hh", info -> ItemFactory.create(
                    Material.TNT,
                    ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Hyper Hard Core",
                    Arrays.asList(
                            ChatColor.GRAY + "戦争をするもよし、略奪するもよし、ハードコア要素を楽しむもよし。",
                            ChatColor.GRAY + "結構自由なサーバー。ただし死ぬと思い代償が...",
                            "",
                            ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + ChatColor.GRAY + "人",
                            ChatColor.GRAY + "バージョン: " + ChatColor.GOLD + "1.15.2" + ChatColor.GRAY + " 1.15.2のみ",
                            "",
                            ChatColor.GRAY + "タグ: サバイバル, PvP, ハードコア")))
            .build();

    private final String inventoryTitle = "サーバーを選択してね！";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("serverselector.receive")) {
            PlayerInventory inventory = player.getInventory();
            inventory.setItem(1, selectorItem.clone());
            inventory.setHeldItemSlot(1);
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
        Inventory inventory = event.getInventory();
        if (inventory != null && inventory.getTitle().equals(inventoryTitle)) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            processClick(player, inventory, event.getRawSlot(), event.getCurrentItem());
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

    public void processClick(Player player, Inventory inventory, int slot, ItemStack item) {
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }
        int slotSize = inventory.getSize() - 9;
        if (slot < slotSize) {
            String clickedName = meta.getDisplayName();
            String name = serverMapping.entrySet().stream()
                    .filter(entry -> {
                        ItemStack item2 = entry.getValue().apply(new ServerInfo());
                        if (!item2.hasItemMeta()) {
                            return false;
                        }
                        ItemMeta meta2 = item2.getItemMeta();
                        if (!meta2.hasDisplayName()) {
                            return false;
                        }
                        return meta2.getDisplayName().equals(clickedName);
                    })
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            String server = name != null ? name : clickedName;
            requestConnect(player, server);
            getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1f, 1.5f);
            }, 4);
        }
        player.playSound(player.getLocation(), Sound.CLICK, 1f, 1f);
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

    public void requestConnect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
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
