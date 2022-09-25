package fr.blockincraft.faylisia.items.enchantment;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An item which has enchants at base
 */
public interface BaseEnchantedItemModel {
    /**
     * @return enchantments of item
     */
    @NotNull
    Map<CustomEnchantments, Integer> getEnchantments();
}
