package fr.blockincraft.faylisia.items.enchantment;

import fr.blockincraft.faylisia.items.CustomItemStack;
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
    Map<CustomEnchantments, Integer> getEnchantments(CustomItemStack customItemStack);
}
