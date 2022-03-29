package me.dthb.drq.listener;

import me.dthb.drq.DevRoomQuests;
import me.dthb.drq.player.DataManager;
import me.dthb.drq.player.PlayerData;
import me.dthb.drq.quest.QuestProgress;
import me.dthb.drq.quest.QuestType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class QuestListener implements Listener {

    private final DataManager dataManager;
    private final DevRoomQuests plugin;

    public QuestListener(DevRoomQuests plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.dataManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        double distance = from.distance(to);
        checkQuest(player, QuestType.WALK, "BLOCK", distance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material consumed = event.getItem().getType();
        checkQuest(player, QuestType.CONSUME, consumed.name(), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {

        EntityType type = event.getEntityType();
        Player killer = event.getEntity().getKiller();

        if (killer != null)
            checkQuest(killer, QuestType.KILL, type.name(), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if(!(event.getCaught() instanceof Item))
            return;

        Item item = (Item) event.getCaught();
        Material caught = item.getItemStack().getType();
        checkQuest(player, QuestType.CATCH, caught.name(), 1);

    }

    private void checkQuest(Player player, QuestType type, String target, double progress) {
        PlayerData playerData = dataManager.playerData(player);

        if (playerData == null)
            return;

        QuestProgress questProgress = playerData.progress(type, target);

        if (questProgress == null || questProgress.isComplete())
            return;

        questProgress.increaseProgress(progress);

        if (questProgress.isComplete())
            plugin.runCommandFor(player);

    }

}
