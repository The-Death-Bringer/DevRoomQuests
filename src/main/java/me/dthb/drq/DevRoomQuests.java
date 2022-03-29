package me.dthb.drq;

import me.dthb.drq.listener.PlayerListener;
import me.dthb.drq.listener.QuestListener;
import me.dthb.drq.player.DataManager;
import me.dthb.drq.player.PlayerRepository;
import me.dthb.drq.util.exception.DatabaseException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DevRoomQuests extends JavaPlugin {

    private String questCompleteCommand;
    private PlayerRepository repository;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        reloadConfigData();

        try {
            this.repository = new PlayerRepository(this);
        } catch (DatabaseException e) {
            getLogger().severe("Error loading the database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this, true);
            return;
        }

        this.dataManager = new DataManager(repository);

        getServer().getPluginManager().registerEvents(new PlayerListener(dataManager), this);
        getServer().getPluginManager().registerEvents(new QuestListener(this), this);

        getServer().getCommandMap().register("quest", new QuestCmd(dataManager));
    }

    public void runCommandFor(Player player) {
        if (questCompleteCommand == null)
            return;

        String replaced = questCompleteCommand.replace("{player}", player.getName());
        getServer().dispatchCommand(getServer().getConsoleSender(), replaced);
    }

    @Override
    public void onDisable() {
        if (dataManager != null)
            dataManager.close();

        if (repository != null)
            repository.close();
    }

    private void reloadConfigData() {
        saveDefaultConfig();
        reloadConfig();

        questCompleteCommand = getConfig().getString("command-on-completion");
    }

    public DataManager dataManager() {
        return dataManager;
    }

}
