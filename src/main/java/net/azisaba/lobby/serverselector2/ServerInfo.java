package net.azisaba.lobby.serverselector2;

public class ServerInfo {

    private String name;
    private int playerCount = -1;

    public String getName() {
        return name;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
