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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private final Map<String, ItemStack> serverMapping = ImmutableMap.of();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("serverselector2.receiveitem")) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(1, selectorItem.clone());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !selectorItem.isSimilar(item)) {
            return;
        }
        event.setCancelled(true);
        Action action = event.getAction();
        if (!action.toString().startsWith("RIGHT_CLICK_")) {
            return;
        }
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1f, (float) Math.random() + 1f);
        openInventory(player);
    }

    public void openInventory(Player player) {
        int slotSize = (int) Math.ceil(serverMap.size() / 9f) * 9;
        int totalSize = slotSize + 9;
        Inventory inventory = getServer().createInventory(null, totalSize, "サーバーを選択してね！");
        player.openInventory(inventory);

        serverMap.values().stream()
                .sorted(Comparator.comparingInt(ServerInfo::getPlayerCount).reversed())
                .forEach(info -> {
                    ItemStack item;
                    if (serverMapping.containsKey(info.getName())) {
                        // TODO: do mapping
                        item = null;
                    } else {
                        item = ItemFactory.create(
                                Material.GRASS,
                                info.getName(),
                                Collections.singletonList(ChatColor.GRAY + "オンライン人数: " + ChatColor.YELLOW + info.getPlayerCount() + "人"));
                    }
                    if (item != null) {
                        item.setAmount(info.getPlayerCount());
                        inventory.addItem(item);
                    }
                });
        player.updateInventory();
    }
    // SERVER SELECTOR ITEM - END

    // PLUGIN MESSAGING - START
    private final Map<String, LinkedList<CompletableFuture<?>>> callbacksMap = new HashMap<>();

    private void requestFuture(List<String> salt, CompletableFuture<?> future) {
        String key = String.join("", salt);
        callbacksMap.putIfAbsent(key, new LinkedList<>());
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
                .whenComplete((servers, t) -> Arrays.stream(servers).forEach(server -> requestPlayerCount(player, server)
                        .whenComplete((playerCount, t2) -> {
                            ServerInfo info = new ServerInfo();
                            info.setName(server);
                            info.setPlayerCount(playerCount);
                            serverMap.put(server, info);
                        })));
    }
    // SERVER UPDATING TASK - END
}
