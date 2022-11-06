package fr.blockincraft.faylisia.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.items.enchantment.BaseEnchantedItemModel;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.items.level.LevelableItemModel;
import fr.blockincraft.faylisia.items.specificitems.EnchantmentLacrymaItem;
import fr.blockincraft.faylisia.items.json.EnchantmentDeserializer;
import fr.blockincraft.faylisia.items.json.EnchantmentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This is an item stack of a custom item with an amount and custom enchantments <br/>
 * You can convert it to an {@link ItemStack} using {@link CustomItemStack#getAsItemStack()} <br/>
 * When create a custom item stack using {@link CustomItemStack#fromItemStack(ItemStack)}, modifications will not be applied to the item stack
 */
public class CustomItemStack implements Cloneable {
    public static final NamespacedKey enchantsKey = new NamespacedKey(Faylisia.getInstance(), "custom-enchants");
    public static final NamespacedKey storedEnchantsKey = new NamespacedKey(Faylisia.getInstance(), "custom-stored-enchants");
    public static final NamespacedKey itemExperienceKey = new NamespacedKey(Faylisia.getInstance(), "item-experience");

    private final Map<CustomEnchantments, Integer> enchantments = new HashMap<>();
    private final Map<CustomEnchantments, Integer> storedEnchantments = new HashMap<>();
    private long experience = 0L;
    private final CustomItem item;
    private int amount;

    /**
     * @param item custom item base
     * @param amount amount of this item
     */
    public CustomItemStack(@NotNull CustomItem item, int amount) {
        this.item = item;

        // Make amount valid
        if (amount < 1) amount = 1;
        if (amount > item.getMaterial().getMaxStackSize()) amount = item.getMaterial().getMaxStackSize();
        this.amount = amount;

        // Apply enchantments if item has base enchantments
        if (item instanceof BaseEnchantedItemModel enchantedItem) {
            enchantments.putAll(enchantedItem.getEnchantments(this));
        }
    }

    @Override
    @NotNull
    public CustomItemStack clone() {
        CustomItemStack customItemStack = new CustomItemStack(item, amount);

        if (item.isEnchantable(this)) {
            enchantments.forEach(customItemStack::addEnchantment);
        }

        if (item instanceof EnchantmentLacrymaItem) {
            storedEnchantments.forEach(customItemStack::addStoredEnchantment);
        }

        return customItemStack;
    }

    /**
     * @return custom item base
     */
    @NotNull
    public CustomItem getItem() {
        return item;
    }

    /**
     * Change amount of item stack
     * @param amount new amount
     */
    public void setAmount(int amount) {
        // Make amount valid
        if (amount < 1) amount = 1;
        if (amount > item.getMaterial().getMaxStackSize()) amount = item.getMaterial().getMaxStackSize();
        this.amount = amount;
    }

    /**
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return copy of enchantments map
     */
    @NotNull
    public Map<CustomEnchantments, Integer> getEnchantments() {
        if (!item.isEnchantable(this)) throw new NonEnchantableException();

        return new HashMap<>(enchantments);
    }

    /**
     * @param enchantment enchantment to remove
     */
    public void removeEnchantment(@NotNull CustomEnchantments enchantment) {
        if (!item.isEnchantable(this)) throw new NonEnchantableException();

        enchantments.remove(enchantment);
    }

    /**
     * Remove all enchantments
     */
    public void clearEnchantment() {
        if (!item.isEnchantable(this)) throw new NonEnchantableException();

        enchantments.clear();
    }

    /**
     * Add a new enchantment to the stack
     * @param enchant enchantment to add
     * @param level level of the enchantment
     */
    public void addEnchantment(@NotNull CustomEnchantments enchant, int level) {
        if (!item.isEnchantable(this)) throw new NonEnchantableException();

        enchantments.put(enchant, level);
    }

    /**
     * Check if stack already has an enchantment
     * @param enchant enchantment to check
     * @return if stack have enchantment
     */
    public boolean hasEnchantment(@NotNull CustomEnchantments enchant) {
        if (!item.isEnchantable(this)) throw new NonEnchantableException();

        return enchantments.containsKey(enchant);
    }

    /**
     * Get level of an enchantment
     * @param enchant enchantment to get
     * @return level of enchantment
     */
    public int getEnchantmentLevel(@NotNull CustomEnchantments enchant) {
        if (!item.isEnchantable(this)) throw new NonEnchantableException();

        return enchantments.get(enchant);
    }


    /**
     * @return copy of stored enchantments map
     */
    @NotNull
    public Map<CustomEnchantments, Integer> getStoredEnchantments() {
        if (!(item instanceof EnchantmentLacrymaItem)) throw new NonEnchantmentLacrymaException();

        return new HashMap<>(storedEnchantments);
    }

    /**
     * @param enchantment stored enchantment to remove
     */
    public void removeStoredEnchantment(@NotNull CustomEnchantments enchantment) {
        if (!(item instanceof EnchantmentLacrymaItem)) throw new NonEnchantmentLacrymaException();

        storedEnchantments.remove(enchantment);
    }

    /**
     * Remove all stored enchantments
     */
    public void clearStoredEnchantment() {
        if (!(item instanceof EnchantmentLacrymaItem)) throw new NonEnchantmentLacrymaException();

        storedEnchantments.clear();
    }

    /**
     * Add a new stored enchantment to the stack
     * @param enchant enchantment to add
     * @param level level of the enchantment
     */
    public void addStoredEnchantment(@NotNull CustomEnchantments enchant, int level) {
        if (!(item instanceof EnchantmentLacrymaItem)) throw new NonEnchantmentLacrymaException();

        storedEnchantments.put(enchant, level);
    }

    /**
     * Check if stack already has a stored enchantment
     * @param enchant enchantment to check
     * @return if stack have enchantment
     */
    public boolean hasStoredEnchantment(@NotNull CustomEnchantments enchant) {
        if (!(item instanceof EnchantmentLacrymaItem)) throw new NonEnchantmentLacrymaException();

        return storedEnchantments.containsKey(enchant);
    }

    /**
     * Get level of a stored enchantment
     * @param enchant enchantment to get
     * @return level of enchantment
     */
    public int getStoredEnchantmentLevel(@NotNull CustomEnchantments enchant) {
        if (!(item instanceof EnchantmentLacrymaItem)) throw new NonEnchantmentLacrymaException();

        return storedEnchantments.get(enchant);
    }

    public long getExperience() {
        if (!(item instanceof LevelableItemModel)) throw new NonLevelableException();

        return experience;
    }

    public void removeExperience(long experience) {
        if (!(item instanceof LevelableItemModel)) throw new NonLevelableException();

        this.experience = Math.max(this.experience - experience, 0);
    }

    public void addExperience(long experience) {
        if (!(item instanceof LevelableItemModel)) throw new NonLevelableException();

        this.experience += experience;
    }

    public void setExperience(long experience) {
        if (!(item instanceof LevelableItemModel)) throw new NonLevelableException();

        this.experience = Math.max(experience, 0);
    }

    public void updateItemStack(ItemStack itemStack) {
        ItemStack model = this.getAsItemStack();

        if (model.getType() != itemStack.getType()) {
            itemStack.setType(model.getType());
        }

        itemStack.setItemMeta(model.getItemMeta());
    }

    /**
     * Create a custom item stack from an item stack
     * @param model model item stack
     * @return custom item stack created
     */
    @Nullable
    public static CustomItemStack fromItemStack(@Nullable ItemStack model) {
        // Get custom item from model
        CustomItem item = Faylisia.getInstance().getRegistry().getCustomItemByItemStack(model);
        if (item == null || model == null) return null;
        // Get amount
        int amount = model.getAmount();

        // Create new custom item stack
        CustomItemStack customItemStack = new CustomItemStack(item, amount);

        // If custom item is enchantable and has enchantments data
        if (item.isEnchantable(customItemStack) && model.getItemMeta() != null && model.getItemMeta().getPersistentDataContainer().has(enchantsKey, PersistentDataType.STRING)) {
            // Then parse data
            String json = model.getItemMeta().getPersistentDataContainer().get(enchantsKey, PersistentDataType.STRING);

            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Map.class, new EnchantmentDeserializer());
            mapper.registerModule(module);

            try {
                Map<CustomEnchantments, Integer> enchants = mapper.readValue(json, Map.class);

                // And add them
                if (enchants != null) {
                    enchants.forEach(customItemStack::addEnchantment);
                }
            } catch (Exception ignored) {

            }
        }

        // If custom item is an instance of EnchantmentLacryma and has stored echantments data then parse it
        if (item instanceof EnchantmentLacrymaItem && model.getItemMeta() != null && model.getItemMeta().getPersistentDataContainer().has(storedEnchantsKey, PersistentDataType.STRING)) {
            // Parse data
            String json = model.getItemMeta().getPersistentDataContainer().get(storedEnchantsKey, PersistentDataType.STRING);

            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Map.class, new EnchantmentDeserializer());
            mapper.registerModule(module);

            try {
                Map<CustomEnchantments, Integer> storedEnchantments = mapper.readValue(json, Map.class);

                // And add them
                if (storedEnchantments != null) {
                    storedEnchantments.forEach(customItemStack::addStoredEnchantment);
                }
            } catch (Exception ignored) {

            }
        }

        if (item instanceof LevelableItemModel && model.getItemMeta() != null && model.getItemMeta().getPersistentDataContainer().has(itemExperienceKey, PersistentDataType.LONG)) {
            Long experience = model.getItemMeta().getPersistentDataContainer().get(itemExperienceKey, PersistentDataType.LONG);

            if (experience != null) {
                customItemStack.setExperience(experience);
            }
        }

        // Return it
        return customItemStack;
    }

    /**
     * Check if an item stack is similar to this custom item stack <br/>
     * It check custom item and enchants
     * @param itemStack item stack to check
     * @return if is similar
     */
    public boolean isSimilar(@NotNull ItemStack itemStack) {
        // Get custom item stack from the item stack to compare
        CustomItemStack customItemStack = fromItemStack(itemStack);

        // Check if they aren't null
        if (customItemStack != null) {
            // Check if its same custom item
            if (customItemStack.item.getId().equals(item.getId())) {
                // Check stored enchantments if item is an instance of EnchantmentLacryma
                if (item instanceof EnchantmentLacrymaItem) {
                    for (Map.Entry<CustomEnchantments, Integer> entry : storedEnchantments.entrySet()) {
                        if (!customItemStack.hasStoredEnchantment(entry.getKey())) return false;
                        if (customItemStack.getStoredEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
                    }
                }

                // Check enchantments
                if (!item.isEnchantable(this) && !customItemStack.item.isEnchantable(this)) {
                    return true;
                } else if (!item.isEnchantable(this) || !customItemStack.item.isEnchantable(this)) {
                    return false;
                } else {
                    for (Map.Entry<CustomEnchantments, Integer> entry : enchantments.entrySet()) {
                        if (!customItemStack.hasEnchantment(entry.getKey())) return false;
                        if (customItemStack.getEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a custom item stack is similar to this custom item stack <br/>
     * It check custom item and enchants
     * @param customItemStack custom item stack to check
     * @return if is similar
     */
    public boolean isSimilar(@NotNull CustomItemStack customItemStack) {
        // Check if they aren't null
        // Check if its same custom item
        if (customItemStack.item.getId().equals(item.getId())) {
            // Check stored enchantments if item is an instance of EnchantmentLacryma
            if (item instanceof EnchantmentLacrymaItem) {
                for (Map.Entry<CustomEnchantments, Integer> entry : storedEnchantments.entrySet()) {
                    if (!customItemStack.hasStoredEnchantment(entry.getKey())) return false;
                    if (customItemStack.getStoredEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
                }
            }

            // Check enchantments
            if (!item.isEnchantable(this) && !customItemStack.item.isEnchantable(this)) {
                return true;
            } else if (!item.isEnchantable(this) || !customItemStack.item.isEnchantable(this)) {
                return false;
            } else {
                for (Map.Entry<CustomEnchantments, Integer> entry : enchantments.entrySet()) {
                    if (!customItemStack.hasEnchantment(entry.getKey())) return false;
                    if (customItemStack.getEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Check if object is equal to this (same custom item, same amount and same enchants)
     * @param obj object to compare
     * @return if they are equals
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        // Check instance of object
        if (obj instanceof CustomItemStack customItemStack) {
            // Check custom item is same (using id)
            if (item.getId().equals(customItemStack.item.getId()) && amount == customItemStack.amount) {
                // Check stored enchantments if item is an instance of EnchantmentLacryma
                if (item instanceof EnchantmentLacrymaItem) {
                    for (Map.Entry<CustomEnchantments, Integer> entry : storedEnchantments.entrySet()) {
                        if (!customItemStack.hasStoredEnchantment(entry.getKey())) return false;
                        if (customItemStack.getStoredEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
                    }
                }

                // Check enchants
                if (!item.isEnchantable(this) && !customItemStack.item.isEnchantable(this)) {
                    return true;
                } else if (!item.isEnchantable(this) || !customItemStack.item.isEnchantable(this)) {
                    return false;
                } else {
                    for (Map.Entry<CustomEnchantments, Integer> entry : enchantments.entrySet()) {
                        if (!customItemStack.hasEnchantment(entry.getKey())) return false;
                        if (customItemStack.getEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
                    }
                }

                if (item instanceof LevelableItemModel && (!(customItemStack.item instanceof LevelableItemModel) || customItemStack.getExperience() != this.getExperience())) {
                    return false;
                } else if (!(item instanceof LevelableItemModel) && customItemStack.item instanceof LevelableItemModel) {
                    return false;
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Create an item stack from this
     * @return item stack created
     */
    @NotNull
    public ItemStack getAsItemStack() {
        // Create item stack and set amount
        ItemStack itemStack = item.getAsItemStack(this);
        itemStack.setAmount(amount);

        // Apply enchantments
        if (item.isEnchantable(this)) {
            // Serialize and write enchantments data
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(Map.class, new EnchantmentSerializer());
            mapper.registerModule(module);

            try {
                String json = mapper.writeValueAsString(enchantments);

                ItemMeta meta = itemStack.getItemMeta();

                if (meta != null) {
                    meta.getPersistentDataContainer().set(enchantsKey, PersistentDataType.STRING, json);

                    itemStack.setItemMeta(meta);
                }
            } catch (Exception ignored) {

            }
        }

        // Add lore and stored enchantments if item is an enchantment lacryma
        if (item instanceof EnchantmentLacrymaItem) {
            // Serialize and write enchantments data
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(Map.class, new EnchantmentSerializer());
            mapper.registerModule(module);

            try {
                String json = mapper.writeValueAsString(storedEnchantments);

                ItemMeta meta = itemStack.getItemMeta();

                if (meta != null) {
                    meta.getPersistentDataContainer().set(storedEnchantsKey, PersistentDataType.STRING, json);

                    itemStack.setItemMeta(meta);
                }
            } catch (Exception ignored) {

            }
        }

        if (item instanceof LevelableItemModel) {
            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null) {
                meta.getPersistentDataContainer().set(itemExperienceKey, PersistentDataType.LONG, experience);

                itemStack.setItemMeta(meta);
            }
        }

        // Return it
        return itemStack;
    }

    /**
     * Exception thrown when we try to use enchantments methods and the custom item isn't enchantable
     */
    public static class NonEnchantableException extends RuntimeException {
        public NonEnchantableException() {
            super("You cannot interact with enchants on non enchantable item!");
        }
    }

    /**
     * Exception thrown when we try to use stored enchantments methods and the custom item is not an
     * instance of {@link EnchantmentLacrymaItem}
     */
    public static class NonEnchantmentLacrymaException extends RuntimeException {
        public NonEnchantmentLacrymaException() {
            super("You cannot use stored enchantments on an item which is not an enchantment lacryma!");
        }
    }

    public static class NonLevelableException extends RuntimeException {
        public NonLevelableException() {
            super("You cannot use experiences methods on an non levelable item");
        }
    }
}
