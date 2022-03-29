package me.dthb.drq.player;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.dthb.drq.quest.QuestProgress;
import me.dthb.drq.quest.QuestType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private final Table<QuestType, String, QuestProgress> table = HashBasedTable.create();
    private final String _id;

    public PlayerData(Player player) {
        this._id = player.getUniqueId().toString();

        table.put(QuestType.CATCH, "PUFFERFISH", new QuestProgress(15));

        // We square it to prevent un needed sqrt calculations
        table.put(QuestType.WALK, "BLOCK", new QuestProgress(300 * 300));

        table.put(QuestType.KILL, "ZOMBIE", new QuestProgress(20));
        table.put(QuestType.KILL, "COW", new QuestProgress(50));

        table.put(QuestType.CONSUME, "COOKED_BEEF", new QuestProgress(32));
        table.put(QuestType.CONSUME, "GOLDEN_APPLE", new QuestProgress(16));
    }

    // Mongo constructor
    private PlayerData() {
        this._id = null;
    }

    public String _id() {
        return _id;
    }

    public UUID id() {
        return UUID.fromString(_id);
    }

    public QuestProgress progress(QuestType type, String column) {
        return table.get(type, column);
    }

}
