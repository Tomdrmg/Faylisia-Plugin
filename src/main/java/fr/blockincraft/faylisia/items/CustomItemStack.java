package fr.blockincraft.faylisia.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.items.enchantment.BaseEnchantedItem;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.items.json.EnchantmentDeserializer;
import fr.blockincraft.faylisia.items.json.EnchantmentSerializer;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.TextUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
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

    private final Map<CustomEnchantments, Integer> enchantments = new HashMap<>();
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
        if (item instanceof BaseEnchantedItem enchantedItem) {
            enchantments.putAll(enchantedItem.getEnchantments());
        }
    }

    @Override
    @NotNull
    public CustomItemStack clone() {
        CustomItemStack customItemStack = new CustomItemStack(item, amount);

        if (item.isEnchantable()) {
            enchantments.forEach(customItemStack::addEnchantment);
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
        if (!item.isEnchantable()) throw new NonEnchantableException();

        return new HashMap<>(enchantments);
    }

    /**
     * @param enchantment enchantment to remove
     */
    public void removeEnchantment(@NotNull CustomEnchantments enchantment) {
        if (!item.isEnchantable()) throw new NonEnchantableException();

        enchantments.remove(enchantment);
    }

    /**
     * Remove all enchantments
     */
    public void clearEnchantment() {
        if (!item.isEnchantable()) throw new NonEnchantableException();

        enchantments.clear();
    }

    /**
     * Add a new enchantment to the stack
     * @param enchant enchantment to add
     * @param level level of the enchantment
     */
    public void addEnchantment(@NotNull CustomEnchantments enchant, int level) {
        if (!item.isEnchantable()) throw new NonEnchantableException();

        enchantments.put(enchant, level);
    }

    /**
     * Check if stack already has an enchantment
     * @param enchant enchantment to check
     * @return if stack have enchantment
     */
    public boolean hasEnchantment(@NotNull CustomEnchantments enchant) {
        if (!item.isEnchantable()) throw new NonEnchantableException();

        return enchantments.containsKey(enchant);
    }

    /**
     * Get level of an enchantment
     * @param enchant enchantment to get
     * @return level of enchantment
     */
    public int getEnchantmentLevel(@NotNull CustomEnchantments enchant) {
        if (!item.isEnchantable()) throw new NonEnchantableException();

        return enchantments.get(enchant);
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
        if (item.isEnchantable() && model.getItemMeta() != null && model.getItemMeta().getPersistentDataContainer().has(enchantsKey, PersistentDataType.STRING)) {
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
                // Check enchantments
                if (!item.isEnchantable() && !customItemStack.item.isEnchantable()) {
                    return true;
                } else if (!item.isEnchantable() || !customItemStack.item.isEnchantable()) {
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
            // Check enchantments
            if (!item.isEnchantable() && !customItemStack.item.isEnchantable()) {
                return true;
            } else if (!item.isEnchantable() || !customItemStack.item.isEnchantable()) {
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
                // Check enchants
                if (!item.isEnchantable() && !customItemStack.item.isEnchantable()) {
                    return true;
                } else if (!item.isEnchantable() || !customItemStack.item.isEnchantable()) {
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
     * Create an item stack from this
     * @return item stack created
     */
    @NotNull
    public ItemStack getAsItemStack() {
        // Create item stack and set amount
        ItemStack itemStack = item.getAsItemStack();
        itemStack.setAmount(amount);

        // Apply enchantments
        if (item.isEnchantable()) {
            // If it has one or more enchants, add a Bukkit enchantment to make enchanted effect on item
            if (enchantments.size() > 0) {
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    // Add item flag
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    // Add enchantments lore
                    List<String> lore = meta.getLore();
                    if (lore != null) {
                        int index = item.getLore().length + item.firstLore().size();
                        if (item.firstLore().size() > 0 && item.getLore().length > 0) index++;

                        if (index > 0) {
                            lore.add(index, "");
                            index++;
                        }

                        List<Map.Entry<CustomEnchantments, Integer>> enchants = enchantments.entrySet().stream().sorted(
                                (o1, o2) -> o1.getKey().index - o2.getKey().index
                        ).toList();

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < enchants.size(); i++) {
                            CustomEnchantments enchant = enchants.get(i).getKey();
                            int level = enchants.get(i).getValue();

                            if (i % 2 == 0) {
                                sb = new StringBuilder("&7" + enchant.nameDependingOfLevel.getName(level, enchant.name) + " " + TextUtils.intToRoman(level));
                                if (i == enchants.size() - 1) {
                                    lore.add(index, ColorsUtils.translateAll(sb.toString()));
                                    index++;
                                }
                            } else {
                                sb.append("&7, ").append(enchant.nameDependingOfLevel.getName(level, enchant.name)).append(" ").append(TextUtils.intToRoman(level));
                                lore.add(index, ColorsUtils.translateAll(sb.toString()));
                                index++;
                            }
                        }

                        meta.setLore(lore);
                    }

                    itemStack.setItemMeta(meta);
                }
            }

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
}
