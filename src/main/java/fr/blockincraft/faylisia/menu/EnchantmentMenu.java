package fr.blockincraft.faylisia.menu;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.specificitems.EnchantmentLacrymaItem;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantmentMenu extends ChestMenu {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    public static final int resultSlot = 13;
    public static final int lacrymaSlot = 16;
    public static final int itemSlot = 10;
    private static final ItemStack invalidRecipeItem;

    private CustomItemStack lastResult = null;

    // Initialize menu items
    static {
        // Create item
        invalidRecipeItem = new ItemStack(Material.BARRIER);

        // Get meta
        ItemMeta invalidRecipeMeta = invalidRecipeItem.getItemMeta();

        // change display name and lore
        invalidRecipeMeta.setDisplayName(ColorsUtils.translateAll("&c&lFusion invalide!"));
        invalidRecipeMeta.setLore(Arrays.asList(
                ColorsUtils.translateAll("&8Place un item a fusionner à gauche et"),
                ColorsUtils.translateAll("&8une lacryma d'enchantement à droite"),
                ColorsUtils.translateAll("&8pour fusionner un item")
        ));

        // Update meta
        invalidRecipeItem.setItemMeta(invalidRecipeMeta);
    }

    /**
     * Initialize menu
     */
    public EnchantmentMenu() {
        super("&b&lFusionneur", 3);

        this.setEmptySlotsClickable(true);
        this.setPlayerInventoryClickable(true);

        //Clear input slots
        this.addItem(itemSlot, new ItemStack(Material.AIR), e -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), this::refreshRecipe, 1);
            return true;
        });
        this.addItem(lacrymaSlot, new ItemStack(Material.AIR), e -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), this::refreshRecipe, 1);
            return true;
        });

        //Clear result slot
        this.addItem(resultSlot, invalidRecipeItem, e -> false);

        //Move items in player inventory on close
        this.addMenuCloseHandler(p -> {
            ItemStack is1 = getItemInSlot(itemSlot);
            if (is1 != null) PlayerUtils.giveOrDrop(p, is1);

            ItemStack is2 = getItemInSlot(lacrymaSlot);
            if (is2 != null) PlayerUtils.giveOrDrop(p, is2);
        });

        //Refresh on double/shift click in player inventory
        this.addPlayerInventoryClickHandler(e -> {
            if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), this::refreshRecipe, 1);
            }

            return true;
        });

        //Refresh on drag items in crafting grid
        this.addMenuDragHandler(e -> {
            for (int slot : e.getRawSlots()) {
                if (slot == lacrymaSlot || slot == itemSlot) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), this::refreshRecipe, 1);
                }
            }
            return true;
        });
    }

    @Override
    public void refreshMenu() {

    }

    /**
     * Actualize result item stack when an item changed in lacryma or item slot
     */
    public void refreshRecipe() {
        ItemStack lacrymaIs = getItemInSlot(lacrymaSlot);
        CustomItemStack lacrymaCustomIs = CustomItemStack.fromItemStack(lacrymaIs);

        // Check if an enchanted lacryma was added and if she hasn't any stored enchantment
        if (lacrymaCustomIs != null && lacrymaCustomIs.getItem() instanceof EnchantmentLacrymaItem && lacrymaCustomIs.getStoredEnchantments().size() > 0 && lacrymaCustomIs.getAmount() == 1) {
            ItemStack itemIs = getItemInSlot(itemSlot);
            CustomItemStack itemCustomIs = CustomItemStack.fromItemStack(itemIs);

            if (itemCustomIs != null && itemCustomIs.getItem() instanceof EnchantmentLacrymaItem && itemCustomIs.getAmount() == 1) {
                CustomItemStack result = itemCustomIs.clone();

                lacrymaCustomIs.getStoredEnchantments().forEach((enchants, lvl) -> {
                    if (enchants.maxLevel < lvl) {
                        return;
                    }

                    if (result.hasStoredEnchantment(enchants)) {
                        int pLvl = result.getStoredEnchantmentLevel(enchants);

                        if (pLvl < lvl) {
                            result.addStoredEnchantment(enchants, lvl);
                        } else if (pLvl == lvl) {
                            int lvlPlus = lvl + 1;
                            if (enchants.maxFusionLevel >= lvlPlus) {
                                result.addStoredEnchantment(enchants, lvlPlus);
                            }
                        }
                    } else {
                        result.addStoredEnchantment(enchants, lvl);
                    }
                });

                if (result.equals(lastResult)) {
                    return;
                }

                lastResult = result;

                this.replaceExistingItem(resultSlot, getResultItem(result), e -> {
                    CustomItemStack itemCustomIsIn = CustomItemStack.fromItemStack(getItemInSlot(itemSlot));
                    CustomItemStack lacrymaCustomIsIn = CustomItemStack.fromItemStack(getItemInSlot(lacrymaSlot));

                    switch (e.getAction()) {
                        // If clone and cursor is empty, place a stack of item in cursor without use items in grid
                        case CLONE_STACK -> {
                            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                                // Make a copy with max stack size
                                CustomItemStack stack = result.clone();
                                stack.setAmount(result.getItem().getMaterial().getMaxStackSize());

                                // Place it in player cursor
                                e.setCursor(stack.getAsItemStack());
                            }
                        }
                        // If player pickup item, give it one and remove ingredients
                        // (Pickup is only when player has an empty cursor)
                        case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE -> {
                            // Check if recipe match again with used pattern to prevent bugs
                            if (itemCustomIsIn != null && lacrymaCustomIsIn != null && itemCustomIsIn.equals(itemCustomIs) && lacrymaCustomIsIn.equals(lacrymaCustomIs)) {
                                // Then put result in player cursor
                                e.setCursor(result.getAsItemStack());
                                // And remove items
                                this.replaceExistingItem(itemSlot, new ItemStack(Material.AIR));
                                this.replaceExistingItem(lacrymaSlot, new ItemStack(Material.AIR));
                            }

                            // We refresh recipe because we update grid or recipe doesn't match again
                            refreshRecipe();
                        }
                    }
                    return false;
                });
                return;
            } else if (itemCustomIs != null && itemCustomIs.getItem().isEnchantable(itemCustomIs) && itemCustomIs.getAmount() == 1) {
                CustomItemStack result = itemCustomIs.clone();

                lacrymaCustomIs.getStoredEnchantments().forEach((enchants, lvl) -> {
                    if (enchants.canBeApplyOn(result.getItem())) {
                        if (enchants.maxLevel < lvl) {
                            return;
                        }

                        if (result.hasEnchantment(enchants)) {
                            int pLvl = result.getEnchantmentLevel(enchants);

                            if (pLvl < lvl) {
                                result.addEnchantment(enchants, lvl);
                            } else if (pLvl == lvl) {
                                int lvlPlus = lvl + 1;
                                if (enchants.maxFusionLevel >= lvlPlus) {
                                    result.addEnchantment(enchants, lvlPlus);
                                }
                            }
                        } else {
                            result.addEnchantment(enchants, lvl);
                        }
                    }
                });

                if (result.equals(lastResult)) {
                    return;
                }

                lastResult = result;

                this.replaceExistingItem(resultSlot, getResultItem(result), e -> {
                    CustomItemStack itemCustomIsIn = CustomItemStack.fromItemStack(getItemInSlot(itemSlot));
                    CustomItemStack lacrymaCustomIsIn = CustomItemStack.fromItemStack(getItemInSlot(lacrymaSlot));

                    switch (e.getAction()) {
                        // If clone and cursor is empty, place a stack of item in cursor without use items in grid
                        case CLONE_STACK -> {
                            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                                // Make a copy with max stack size
                                CustomItemStack stack = result.clone();
                                stack.setAmount(result.getItem().getMaterial().getMaxStackSize());

                                // Place it in player cursor
                                e.setCursor(stack.getAsItemStack());
                            }
                        }
                        // If player pickup item, give it one and remove ingredients
                        // (Pickup is only when player has an empty cursor)
                        case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE -> {
                            // Check if recipe match again with used pattern to prevent bugs
                            if (itemCustomIsIn != null && lacrymaCustomIsIn != null && itemCustomIsIn.equals(itemCustomIs) && lacrymaCustomIsIn.equals(lacrymaCustomIs)) {
                                // Then put result in player cursor
                                e.setCursor(result.getAsItemStack());
                                // And remove items
                                this.replaceExistingItem(itemSlot, new ItemStack(Material.AIR));
                                this.replaceExistingItem(lacrymaSlot, new ItemStack(Material.AIR));
                            }

                            // We refresh recipe because we update grid or recipe doesn't match again
                            refreshRecipe();
                        }
                    }
                    return false;
                });
                return;
            }
        }

        // Reset result if no requirement are complete
        this.lastResult = null;
        this.replaceExistingItem(resultSlot, invalidRecipeItem, e -> false);
    }

    /**
     * Make an item stack from a {@link CustomItemStack} and remove his item id data
     * In case of server lag and player take items because event wasn't cancelled
     * @param customItemStack custom item stack to make unusable
     * @return unusable item stack
     */
    @NotNull
    private ItemStack getResultItem(@NotNull CustomItemStack customItemStack) {
        // Make an item stack from custom item stack
        ItemStack result = customItemStack.getAsItemStack();

        // If it hasn't a meta, it hasn't data then return it
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return result;

        // Add craft footer to lore
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        lore.add(ColorsUtils.translateAll("&8&m--------------------------"));
        lore.add(ColorsUtils.translateAll("&8Clique pour fusioner l'item"));
        meta.setLore(lore);

        // If it has a custom item id, remove it
        if (meta.getPersistentDataContainer().has(CustomItem.idKey, PersistentDataType.STRING)) {
            meta.getPersistentDataContainer().remove(CustomItem.idKey);
        }
        // Update item meta
        result.setItemMeta(meta);

        return result;
    }
}
