package fr.blockincraft.faylisia.items.specificitems;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * We create a new class in order to use 'instanceof' function
 */
public class EnchantmentLacrymaItem extends CustomItem {
    public EnchantmentLacrymaItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    @Override
    protected boolean hasEnchants(CustomItemStack customItemStack) {
        return customItemStack.getStoredEnchantments().size() > 0;
    }

    @Override
    protected Map<CustomEnchantments, Integer> getEnchants(CustomItemStack customItemStack) {
        return customItemStack.getStoredEnchantments();
    }

    @Override
    public boolean isEnchantable(CustomItemStack customItemStack) {
        return false;
    }

    @Override
    public boolean isDisenchantable(CustomItemStack customItemStack) {
        return false;
    }

    @Override
    @NotNull
    protected String getType(CustomItemStack customItemStack) {
        return "LACRYMA";
    }
}
