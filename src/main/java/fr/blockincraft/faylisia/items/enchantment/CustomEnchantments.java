package fr.blockincraft.faylisia.items.enchantment;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.event.EnchantmentHandlers;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Custom entchantments contain a {@link Handlers} which is called with others player handlers
 */
public enum CustomEnchantments {
    PROTECTION(0, "Protection", new EnchantmentHandlersIn(-1) {
        @Override
        public double getStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
            return inArmorSlot && stat == Stats.DEFENSE ? value + 25 * level : value;
        }
    }, 4, 4, new Class[]{ArmorItem.class}, new CustomEnchantments[0]);

    public final int index;
    public final String name;
    public final EnchantmentHandlers handlers;
    public final int maxLevel;
    public final int maxFusionLevel;
    public final Class<? extends CustomItem>[] itemTypes;
    public final CustomEnchantments[] conflicts;
    public final ValidateEnchantability validate;
    public final NameDependingOfLevel nameDependingOfLevel;

    /**
     * Constructor without more validation
     */
    CustomEnchantments(int index, @NotNull String name, @NotNull EnchantmentHandlers handlers, int maxLevel, int maxFusionLevel, @NotNull Class<? extends CustomItem>[] itemTypes, @NotNull CustomEnchantments[] conflicts) {
        this(index, name, handlers, maxLevel, maxFusionLevel, itemTypes, conflicts, item -> true);
    }

    CustomEnchantments(int index, @NotNull String name, @NotNull EnchantmentHandlers handlers, int maxLevel, int maxFusionLevel, @NotNull Class<? extends CustomItem>[] itemTypes, @NotNull CustomEnchantments[] conflicts, @NotNull ValidateEnchantability validate) {
        this(index, name, handlers, maxLevel, maxFusionLevel, itemTypes, conflicts, validate, (level, nameIn) -> nameIn);
    }

    /**
     * Constructor with more validation
     */
    CustomEnchantments(int index, @NotNull String name, @NotNull EnchantmentHandlers handlers, int maxLevel, int maxFusionLevel, @NotNull Class<? extends CustomItem>[] itemTypes, @NotNull CustomEnchantments[] conflicts, @NotNull ValidateEnchantability validate, @NotNull NameDependingOfLevel nameDependingOfLevel) {
        this.index = index;
        this.name = name;
        this.handlers = handlers;
        this.maxLevel = maxLevel;
        this.maxFusionLevel = maxFusionLevel;
        this.itemTypes = itemTypes;
        this.conflicts = conflicts;
        this.validate = validate;
        this.nameDependingOfLevel = nameDependingOfLevel;
    }

    /**
     * Check if item is enchantable and if enchant can be applied on this type of item
     * @param on item to check
     * @return if enchant can be applied
     */
    public boolean canBeApplyOn(@NotNull CustomItem on) {
        // Check if item is enchantable
        if (!on.isEnchantable()) {
            return false;
        }

        // Check item type
        for (Class<? extends CustomItem> itemType : itemTypes) {
            if (itemType.isInstance(on)) {
                // Check enchantment validation
                return validate.validate(on);
            }
        }

        return false;
    }

    /**
     * Check if item is enchantable, if enchant can be applied on this type of item and if this item doesn't have conflicts depending on second
     * parameter
     * @param on item stack to check
     * @param ignoreConflicts if we also check conflicts or not
     * @return if enchant can be applied
     */
    public boolean canBeApplyOn(@NotNull CustomItemStack on, boolean ignoreConflicts) {
        // Check if item is enchantable and item type
        if (!canBeApplyOn(on.getItem())) {
            return false;
        }

        // Check conflicts
        if (!ignoreConflicts) {
            for (CustomEnchantments enchant : on.getEnchantments().keySet()) {
                if (Arrays.asList(conflicts).contains(enchant)) {
                    return false;
                }
            }
        }

        return true;
    }

    @FunctionalInterface
    public interface ValidateEnchantability {
        /**
         * Function called to validate if an item can be enchanted, before calling this, we have already check {@link CustomItem#isEnchantable()} and
         * conflicts. This can be util to make enchantment only usable on specific {@link Material}
         * @param item item to check
         * @return if enchant can be applied on this item
         */
        boolean validate(CustomItem item);
    }

    @FunctionalInterface
    public interface NameDependingOfLevel {
        /**
         * Function called to get enchantment name at a specified level, util if we want to make a gradiant at max level
         * @param level level of enchantment
         * @param name name of enchantment
         * @return name to display
         */
        String getName(int level, String name);
    }

    public static class EnchantmentHandlersIn extends EnchantmentHandlers {
        protected EnchantmentHandlersIn(int level) {
            super(level);
        }

        /**
         * Create a child class of {@link EnchantmentHandlers} because if we make this method directly in, a {@link IllegalAccessException} will be thrown
         * because of {@link  EnchantmentHandlers} class can't access a member of the {@link CustomEnchantments} class
         * @param level level of enchantment (set to one if inferior)
         * @return copy
         */
        @Nonnull
        public EnchantmentHandlers withLevel(int level) {
            // Level need to be at least 1
            if (level < 1) level = 1;
            // Constructor to use, it will be 'EnchantmentHandlers(int level)'
            Constructor<? extends EnchantmentHandlers> constructor = null;

            // Try to get constructor
            try {
                constructor = this.getClass().getDeclaredConstructor(int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Missing constructor 'EnchantmentHandlers(int level)'");
            }

            // Try to create instance normally we will not generate exceptions except if this class is used out of the CustomEnchantments class
            try {
                return constructor.newInstance(level);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException("Cannot use constructor: " + e.getMessage());
            }
        }
    }
}
