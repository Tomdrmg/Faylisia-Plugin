package fr.blockincraft.faylisia.items.event;

import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import org.jetbrains.annotations.NotNull;

/**
 * Handlers used to create {@link CustomEnchantments}, in this enum we create a handler model, and to get handlers for a level, we use {@link EnchantmentHandlers#withLevel(int)} which
 * create a copy with same overridden handlers
 */
public abstract class EnchantmentHandlers implements Handlers {
    // Level of the enchantment or -1 if this is the handler model
    protected final int level;

    public void onHandlerCall() {
        if (level == -1) throw new RuntimeException("Cannot use model handler instance, use 'withLevel(int level)' method!");
    }

    /**
     * Constructor used to make a level copy in {@link EnchantmentHandlers#withLevel(int)}, to create a model set level to -1
     * @param level level of enchantment
     */
    protected EnchantmentHandlers(int level) {
        this.level = level;
    }

    /**
     * Make a copy with specified level and keep all overridden handlers
     * @param level level of enchantment (set to one if inferior)
     * @return copy
     */
    @NotNull
    public abstract EnchantmentHandlers withLevel(int level);
}
