package me.dthb.drq.player;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.List;

public class DataManager {

    private final List<PlayerData> dataList = Lists.newArrayList();
    private final PlayerRepository repository;

    public DataManager(PlayerRepository repository) {
        this.repository = repository;
    }

    public void close() {
        dataList.forEach(repository::saveData);
    }

    public PlayerData playerData(Player player) {
        return dataList.stream().filter(pd -> player.getUniqueId().equals(pd.id())).findFirst().orElse(null);
    }

    public void loadPlayerData(Player player) {
        PlayerData playerData = repository.playerData(player.getUniqueId().toString());
        if (playerData == null)
            playerData = new PlayerData(player);
        dataList.add(playerData);
    }

    public void unloadAndSave(Player player) {
        PlayerData data = playerData(player);
        if (data == null)
            return;
        repository.saveData(data);
        dataList.remove(data);
    }

}
