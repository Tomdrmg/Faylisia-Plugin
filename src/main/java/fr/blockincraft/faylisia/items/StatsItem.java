package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.player.Stats;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Item which add stat when equip
 */
public interface StatsItem {
    /**
     * Method used to know if stats must be applied
     * @param inMainHand if item is in main hand
     * @param inArmorSlot if item is in armor slot
     * @return if stats are valid
     */
    boolean validStats(boolean inMainHand, boolean inArmorSlot);

    /**
     * @param stat stat that we want the value
     * @return value of stat
     */
    double getStat(@NotNull Stats stat);

    /**
     * @param stat stat that we want to check
     * @return if stat has a value
     */
    boolean hasStat(@NotNull Stats stat);

    /**
     * @return all stats and their values
     */
    @NotNull
    Map<Stats, Double> getStats();
}
