package net.azisaba.lobby.serverselector2;

import lombok.Data;

@Data
public class ServerInfo {

    private String name;
    private int playerCount = -1;
}
