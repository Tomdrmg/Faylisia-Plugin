package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An item which has enchants at base
 */
public interface BaseEnchantedItem {
    /**
     * @return enchantments of item
     */
    @NotNull
    Map<CustomEnchantments, Integer> getEnchantments();
}
