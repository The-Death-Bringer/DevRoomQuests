package me.dthb.drq;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.collect.Lists;
import me.dthb.drq.player.DataManager;
import me.dthb.drq.player.PlayerData;
import me.dthb.drq.quest.QuestProgress;
import me.dthb.drq.quest.QuestType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestCmd extends Command {

    private final DataManager dataManager;

    protected QuestCmd(DataManager dataManager) {
        super("quest");
        this.dataManager = dataManager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        PlayerData data = dataManager.playerData(player);

        if (data == null) {
            Component error = Component.text("Error loading your data!", NamedTextColor.RED);
            player.sendMessage(error);
            return false;
        }

        ChestGui gui = new ChestGui(4, "Quests!");

        StaticPane pane = new StaticPane(0, 1, 9, 2);

        QuestProgress progress = data.progress(QuestType.KILL, "ZOMBIE");
        GuiItem killZombie = fromProgress("Kill Zombies", Material.ROTTEN_FLESH, progress);
        pane.addItem(killZombie, 1, 0);

        progress = data.progress(QuestType.KILL, "COW");
        GuiItem killCow = fromProgress("Kill Cows", Material.LEATHER, progress);
        pane.addItem(killCow, 3, 0);

        progress = data.progress(QuestType.CONSUME, "COOKED_BEEF");
        GuiItem eatBeef = fromProgress("Consume Cooked Beefs", Material.COOKED_BEEF, progress);
        pane.addItem(eatBeef, 5, 0);

        progress = data.progress(QuestType.CONSUME, "GOLDEN_APPLE");
        GuiItem eatApple = fromProgress("Eat Golden Apples", Material.GOLDEN_APPLE, progress);
        pane.addItem(eatApple, 7, 0);

        progress = data.progress(QuestType.CATCH, "PUFFERFISH");
        GuiItem catchItem = fromProgress("Catch Pufferfish", Material.FISHING_ROD, progress);
        pane.addItem(catchItem, 3, 1);

        progress = data.progress(QuestType.WALK, "BLOCK");
        GuiItem walkItem = fromProgress("Walk 300 blocks", Material.DIAMOND_BOOTS, progress);
        pane.addItem(walkItem, 5, 1);

        gui.addPane(pane);
        gui.show(player);

        return true;
    }

    private GuiItem fromProgress(String title, Material material, QuestProgress progress) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(title, NamedTextColor.YELLOW));

        Component info = Component.text("Completed!", NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false);

        if (!progress.isComplete()) {
            info = Component.text()
                    .append(Component.text((int) progress.progress(), NamedTextColor.GREEN))
                    .append(Component.text("/", NamedTextColor.GRAY))
                    .append(Component.text(progress.goal(), NamedTextColor.GREEN))
                    .build().decoration(TextDecoration.ITALIC, false);
        }

        List<Component> lore = meta.hasLore() ? meta.lore() : Lists.newArrayList();
        lore.add(info);
        meta.lore(lore);
        item.setItemMeta(meta);
        return new GuiItem(item);
    }

}
