package fr.blockincraft.faylisia.items.specificitems;

import fr.blockincraft.faylisia.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * We create a new class in order to use 'instanceof' function
 */
public class EnchantmentLacrymaItem extends CustomItem {
    public EnchantmentLacrymaItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    @Override
    @NotNull
    protected String getType() {
        return "LACRYMA";
    }
}
