package fr.blockincraft.faylisia.menu;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.recipes.CraftingRecipe;
import fr.blockincraft.faylisia.items.recipes.Recipe;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingMenu extends ChestMenu {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    public static final int[] craftingGrid = new int[]{
            11, 12, 13,
            20, 21, 22,
            29, 30, 31
    };
    public static final int resultSlot = 24;
    private static final ItemStack invalidRecipeItem;

    private CustomItemStack lastResult = null;

    static {
        invalidRecipeItem = new ItemStack(Material.BARRIER);

        ItemMeta invalidRecipeMeta = invalidRecipeItem.getItemMeta();

        invalidRecipeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lInvalid recipe!"));
        invalidRecipeMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Place items in grid to"),
                ChatColor.translateAlternateColorCodes('&', "&8craft an item.")
        ));

        invalidRecipeItem.setItemMeta(invalidRecipeMeta);
    }

    public CraftingMenu() {
        super("&d&lCrafting &b&lTable", 5);

        this.setEmptySlotsClickable(true);
        this.setPlayerInventoryClickable(true);

        //Clear crafting grid
        for (int i = 0; i < 9; i++) {
            int slot = craftingGrid[i];
            this.addItem(slot, new ItemStack(Material.AIR), e -> {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), this::refreshRecipe, 1);
                return true;
            });
        }

        //Clear result slot
        this.addItem(resultSlot, invalidRecipeItem, e -> false);

        //Move items in player inventory on close
        this.addMenuCloseHandler(p -> {
            for (int slot : craftingGrid) {
                ItemStack is = getItemInSlot(slot);
                if (is != null) PlayerUtils.giveOrDrop(p, getItemInSlot(slot));
            }
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
                for (int i : craftingGrid) {
                    if (slot == i) Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), this::refreshRecipe, 1);
                }
            }
            return true;
        });


    }

    @Override
    public void refreshMenu() {

    }

    public void refreshRecipe() {
        for (CustomItem item : registry.getItems()) {
            for (Recipe r : item.getRecipes()) {
                if (r instanceof CraftingRecipe recipe) {
                    CustomItemStack[] patternUsed = recipe.matches(getRecipe());
                    if (patternUsed != null) {
                        if (lastResult == null || !lastResult.getItem().getId().equals(item.getId()) || lastResult.getAmount() != recipe.getResultAmount()) {
                            CustomItemStack result = new CustomItemStack(item, recipe.getResultAmount());
                            this.lastResult = result;

                            ItemStack resultItem = getResultItem(result);
                            this.replaceExistingItem(resultSlot, resultItem, e -> {
                                InventoryAction action = e.getAction();

                                switch (action) {
                                    case CLONE_STACK -> {
                                        if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                                            e.setCursor(item.getAsItemStack(item.getMaterial().getMaxStackSize()));
                                        }
                                    }
                                    case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE -> {
                                        if (recipe.matches(getRecipe(), patternUsed)) {
                                            e.setCursor(item.getAsItemStack(recipe.getResultAmount()));
                                            for (int i = 0; i < 9; i++) {
                                                ItemStack recipeItem = getRecipe()[i];
                                                if (recipeItem != null) {
                                                    recipeItem.setAmount(recipeItem.getAmount() - patternUsed[i].getAmount());
                                                }
                                            }
                                        }

                                        refreshRecipe();
                                    }
                                    case SWAP_WITH_CURSOR -> {
                                        if (recipe.matches(getRecipe(), patternUsed)) {
                                            ItemStack cursor = e.getCursor();
                                            if (item.getAsItemStack().isSimilar(cursor)) {
                                                if (cursor.getAmount() + recipe.getResultAmount() <= cursor.getMaxStackSize()) {
                                                    cursor.setAmount(cursor.getAmount() + recipe.getResultAmount());
                                                    for (int i = 0; i < 9; i++) {
                                                        ItemStack recipeItem = getRecipe()[i];
                                                        if (recipeItem != null) {
                                                            recipeItem.setAmount(recipeItem.getAmount() - patternUsed[i].getAmount());
                                                        }
                                                    }
                                                    refreshRecipe();
                                                }
                                            }
                                        } else {
                                            refreshRecipe();
                                        }
                                    }
                                    case MOVE_TO_OTHER_INVENTORY -> {
                                        if (recipe.matches(getRecipe(), patternUsed)) {
                                            ItemStack itemModel = item.getAsItemStack();
                                            int maxToAdd = 0;
                                            PlayerInventory pi = e.getWhoClicked().getInventory();
                                            for (ItemStack slot : pi.getStorageContents()) {
                                                if (slot == null || slot.getType() == Material.AIR) {
                                                    maxToAdd += itemModel.getMaxStackSize();
                                                } else {
                                                    CustomItem inventoryItem = registry.getCustomItemByItemStack(slot);
                                                    if (inventoryItem != null && inventoryItem.getId().equals(item.getId())) {
                                                        maxToAdd += itemModel.getMaxStackSize() - slot.getAmount();
                                                    }
                                                }
                                            }

                                            ItemStack[] gridCopy = Arrays.stream(getRecipe()).map(itemStack -> {
                                                return itemStack == null ? null : itemStack.clone();
                                            }).toList().toArray(new ItemStack[0]);
                                            int repeat = 0;

                                            while (recipe.matches(gridCopy, patternUsed) && (repeat + 1) * recipe.getResultAmount() <= maxToAdd) {
                                                repeat++;
                                                for (int i = 0; i < 9; i++) {
                                                    ItemStack recipeItem = gridCopy[i];
                                                    if (recipeItem != null) {
                                                        int newAmount = recipeItem.getAmount() - patternUsed[i].getAmount();

                                                        if (newAmount > 0)
                                                            recipeItem.setAmount(recipeItem.getAmount() - patternUsed[i].getAmount());
                                                        else gridCopy[i] = null;
                                                    }
                                                }
                                            }

                                            int stacks = repeat * recipe.getResultAmount() / itemModel.getMaxStackSize();
                                            int items = repeat * recipe.getResultAmount() % itemModel.getMaxStackSize();

                                            for (int i = 0; i < stacks; i++) {
                                                PlayerUtils.giveOrDrop((Player) e.getWhoClicked(), item.getAsItemStack(itemModel.getMaxStackSize()));
                                            }

                                            if (items > 0)
                                                PlayerUtils.giveOrDrop((Player) e.getWhoClicked(), item.getAsItemStack(items));

                                            for (int i = 0; i < 9; i++) {
                                                replaceExistingItem(craftingGrid[i], gridCopy[i]);
                                            }
                                        }

                                        refreshRecipe();
                                    }
                                }

                                return false;
                            });
                        }

                        return;
                    }
                }
            }
        }

        this.lastResult = null;
        this.replaceExistingItem(resultSlot, invalidRecipeItem, e -> false);
    }

    private ItemStack getResultItem(CustomItemStack customItemStack) {
        ItemStack result = customItemStack.getItem().getAsItemStack(customItemStack.getAmount());

        ItemMeta meta = result.getItemMeta();
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8Shift click to craft all"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8Click to craft one"));
        meta.setLore(lore);

        if (meta.getPersistentDataContainer().has(CustomItem.idKey, PersistentDataType.STRING)) {
            meta.getPersistentDataContainer().remove(CustomItem.idKey);
        }
        result.setItemMeta(meta);

        return result;
    }

    private boolean isEmpty() {
        for (ItemStack i : getRecipe()) {
            if (i != null) return false;
        }

        return true;
    }

    private ItemStack[] getRecipe() {
        ItemStack[] recipe = new ItemStack[9];

        for (int i = 0; i < 9; i++) {
            ItemStack slotItem = getItemInSlot(craftingGrid[i]);
            if (slotItem != null && slotItem.getType() != Material.AIR) recipe[i] = slotItem;
        }

        return recipe;
    }
}
