package fr.blockincraft.faylisia.items.enchantment;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.event.EnchantmentHandlers;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.weapons.DamageItemModel;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Custom entchantments contain a {@link Handlers} which is called with others player handlers
 */
public enum CustomEnchantments {
    PROTECTION(0, "Protection", new EnchantmentHandlersIn(-1) {
        @Override
        public double getDefaultStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
            return inArmorSlot && stat == Stats.DEFENSE ? value + 25 * level : value;
        }

        @Override
        public double calculateItemStat(@NotNull Player player, @NotNull CustomItem customItem, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
            return inArmorSlot && stat == Stats.DEFENSE ? value + 25 * level : value;
        }
    }, 4, 4, new Class[]{ArmorItem.class}, new CustomEnchantments[0]),
    ABSORPTION(1, "Absorption", new EnchantmentHandlersIn(-1) {
        @Override
        public long onDamage(@NotNull Player player, @NotNull CustomEntity customEntity, @NotNull DamageType damageType, long damage, boolean inHand, boolean inArmorSlot) {
            CustomPlayerDTO custom = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
            custom.setEffectiveHealth((long) (custom.getEffectiveHealth() + custom.getEffectiveHealth() * 0.0015 * level));
            return damage;
        }
    }, 5, 5, new Class[]{DamageItemModel.class}, new CustomEnchantments[0]);

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
     * Constructor without more validation and custom level name
     */
    CustomEnchantments(int index, @NotNull String name, @NotNull EnchantmentHandlers handlers, int maxLevel, int maxFusionLevel, @NotNull Class<? extends CustomItem>[] itemTypes, @NotNull CustomEnchantments[] conflicts) {
        this(index, name, handlers, maxLevel, maxFusionLevel, itemTypes, conflicts, item -> true);
    }

    /**
     * Constructor without custom level name
     */
    CustomEnchantments(int index, @NotNull String name, @NotNull EnchantmentHandlers handlers, int maxLevel, int maxFusionLevel, @NotNull Class<? extends CustomItem>[] itemTypes, @NotNull CustomEnchantments[] conflicts, @NotNull ValidateEnchantability validate) {
        this(index, name, handlers, maxLevel, maxFusionLevel, itemTypes, conflicts, validate, (level, nameIn) -> nameIn);
    }

    /**
     * Full constructor
     * @param index index used to sort enchantments on rendering them
     * @param name display name of enchantment
     * @param handlers handlers to make enchantment functions
     * @param maxLevel maximum level obtaining
     * @param maxFusionLevel maximum level obtaining by fusion of enchantment lacryma
     * @param itemTypes types of item which can have this
     * @param conflicts conflicts with others enchantments
     * @param validate more validation function
     * @param nameDependingOfLevel different name depending on enchantment level function
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
        @NotNull
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
