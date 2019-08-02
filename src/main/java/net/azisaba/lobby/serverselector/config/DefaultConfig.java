package net.azisaba.lobby.serverselector.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.azisaba.lobby.serverselector.ServerSelector;

public class DefaultConfig extends Config {

    @Getter
    private boolean clearInventoryEnable;

    public DefaultConfig(@NonNull ServerSelector plugin) {
        super(plugin, "config.yml", "config.yml");
    }

    @SneakyThrows(value = { Exception.class })
    @Override
    public void loadConfig() {
        super.loadConfig();

        clearInventoryEnable = config.getBoolean("ClearInventoryOnJoin", false);
    }
}
