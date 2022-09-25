package fr.blockincraft.faylisia.items.tools;

import fr.blockincraft.faylisia.items.StatsItemModel;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsToolItem extends ToolItem implements StatsItemModel {
    private final Map<Stats, Double> stats = new HashMap<>();

    public StatsToolItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    /**
     * Add stats to the lore
     * @return text to add
     */
    @Override
    @NotNull
    protected List<String> firstLore() {
        List<String> lore = super.firstLore();

        List<Map.Entry<Stats, Double>> sorted = stats.entrySet().stream().sorted((o1, o2) -> o1.getKey().index - o2.getKey().index).toList();

        sorted.forEach(entry -> {
            lore.add(ColorsUtils.translateAll("&7" + entry.getKey().name + " &" + entry.getKey().color + "+" + entry.getValue()));
        });

        return lore;
    }

    @Override
    public boolean validStats(boolean inMainHand, boolean inArmorSlot) {
        return inMainHand;
    }

    @Override
    public double getStat(@NotNull Stats stat) {
        return stats.get(stat);
    }

    @Override
    public boolean hasStat(@NotNull Stats stat) {
        return stats.get(stat) != null;
    }

    @Override
    @NotNull
    public Map<Stats, Double> getStats() {
        return new HashMap<>(stats);
    }

    /**
     * Remove a stat to this item
     * @param stat stat to remove
     * @return this instance
     */
    @NotNull
    public StatsToolItem removeStat(@NotNull Stats stat) {
        if (isRegistered()) throw new ChangeRegisteredItem();

        stats.remove(stat);
        return this;
    }

    /**
     * Add or edit a stat on this item
     * @param stat stat to add/edit
     * @param value value of stat
     * @return this instance
     */
    @NotNull
    public StatsToolItem setStat(@NotNull Stats stat, double value) {
        if (isRegistered()) throw new ChangeRegisteredItem();

        stats.put(stat, value);
        return this;
    }
}
