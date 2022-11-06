package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.player.Stats;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Item which add stat when equip
 */
public interface StatsItemModel {
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
    double getStat(@NotNull Stats stat, CustomItemStack customItemStack);

    /**
     * @param stat stat that we want to check
     * @return if stat has a value
     */
    default boolean hasStat(@NotNull Stats stat, CustomItemStack customItemStack) {
        return getStat(stat, customItemStack) > 0;
    }

    /**
     * Return a copy of all default stats of this item /!\ Do not include enchants or others modifiers /!\
     * @return copy of all default stats and their values
     */
    @NotNull
    Map<Stats, Double> getStats(CustomItemStack customItemStack);
}
