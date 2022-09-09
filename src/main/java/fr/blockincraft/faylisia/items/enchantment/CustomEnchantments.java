package fr.blockincraft.faylisia.items.enchantment;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.event.Handlers;

import java.util.Arrays;

/**
 * Custom entchantments contain a {@link Handlers} which is called with others player handlers
 */
public enum CustomEnchantments {
    ;

    public final String id;
    public final String name;
    public final Handlers handlers;
    public final int maxLevel;
    public final int maxFusionLevel;
    public final Class<? extends CustomItem>[] itemTypes;
    public final CustomEnchantments[] conflicts;

    CustomEnchantments(String id, String name, Handlers handlers, int maxLevel, int maxFusionLevel, Class<? extends CustomItem>[] itemTypes, CustomEnchantments[] conflicts) {
        this.id = id;
        this.name = name;
        this.handlers = handlers;
        this.maxLevel = maxLevel;
        this.maxFusionLevel = maxFusionLevel;
        this.itemTypes = itemTypes;
        this.conflicts = conflicts;
    }

    public boolean canBeApplyOn(CustomItem on) {
        if (!on.isEnchantable()) {
            return false;
        }

        for (Class<? extends CustomItem> itemType : itemTypes) {
            if (itemType.isInstance(on)) {
                return true;
            }
        }

        return false;
    }

    public boolean canBeApplyOn(CustomItemStack on, boolean ignoreEnchants) {
        if (!canBeApplyOn(on.getItem())) {
            return false;
        }

        if (!ignoreEnchants) {
            for (CustomEnchantments enchant : on.getEnchantments().keySet()) {
                if (Arrays.asList(conflicts).contains(enchant)) {
                    return false;
                }
            }
        }

        return true;
    }
}
