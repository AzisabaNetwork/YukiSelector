package net.azisaba.lobby.serverselector.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Data;
import lombok.EqualsAndHashCode;

// TODO MVPとかの情報も載せたい。
/**
 * マッチが終了したときに呼び出されるイベント
 *
 * @author siloneco
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerCountUpdateEvent extends Event {

    // マッチを行ったマップ
    private final String serverName;
    // 勝利したチーム
    private final int playerCount;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}