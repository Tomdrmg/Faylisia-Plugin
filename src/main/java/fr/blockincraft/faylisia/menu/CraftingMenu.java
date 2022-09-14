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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
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

    // Initialize menu items
    static {
        // Create item
        invalidRecipeItem = new ItemStack(Material.BARRIER);

        // Get meta
        ItemMeta invalidRecipeMeta = invalidRecipeItem.getItemMeta();

        // change display name and lore
        invalidRecipeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lInvalid recipe!"));
        invalidRecipeMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Placez des items dans la grille"),
                ChatColor.translateAlternateColorCodes('&', "&8pour craft un item.")
        ));

        // Update meta
        invalidRecipeItem.setItemMeta(invalidRecipeMeta);
    }

    /**
     * Initialize menu
     */
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
                if (is != null) PlayerUtils.giveOrDrop(p, is);
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

    /**
     * Actualize result item stack when an item changed in the crafting grid
     */
    public void refreshRecipe() {
        // Retrieve all items and their recipes
        for (CustomItem item : registry.getItems()) {
            for (Recipe r : item.getRecipes()) {
                // Only exec if recipe can be used here
                if (r instanceof CraftingRecipe recipe) {
                    // Save pattern used to valid shift click
                    CustomItemStack[] patternUsed = recipe.matches(getRecipe());
                    if (patternUsed != null) {
                        // Check if this is a different result item stack
                        if (lastResult == null || !lastResult.equals(new CustomItemStack(item, recipe.getResultAmount()))) {
                            // Create result custom item stack
                            CustomItemStack result = new CustomItemStack(item, recipe.getResultAmount());
                            this.lastResult = result;

                            // Get item stack which will be displayed
                            ItemStack resultItem = getResultItem(result);
                            // Display item stack in inventory
                            this.replaceExistingItem(resultSlot, resultItem, e -> {
                                // Get action of click
                                InventoryAction action = e.getAction();

                                // Craft item depending on action
                                switch (action) {
                                    // If clone and cursor is empty, place a stack of item in cursor without use items in grid
                                    case CLONE_STACK -> {
                                        if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                                            // Make a copy with max stack size
                                            CustomItemStack stack = result.clone();
                                            stack.setAmount(item.getMaterial().getMaxStackSize());

                                            // Place it in player cursor
                                            e.setCursor(stack.getAsItemStack());
                                        }
                                    }
                                    // If player pickup item, give it one and remove ingredients
                                    // (Pickup is only when player has an empty cursor)
                                    case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE -> {
                                        // Check if recipe match again with used pattern to prevent bugs
                                        if (recipe.matches(getRecipe(), patternUsed)) {
                                            // Then put result in player cursor
                                            e.setCursor(result.getAsItemStack());
                                            // And remove ingredients
                                            for (int i = 0; i < 9; i++) {
                                                ItemStack recipeItem = getRecipe()[i];
                                                if (recipeItem != null) {
                                                    recipeItem.setAmount(recipeItem.getAmount() - patternUsed[i].getAmount());
                                                }
                                            }
                                        }

                                        // We refresh recipe because we update grid or recipe doesn't match again
                                        refreshRecipe();
                                    }
                                    // If player swap with cursor (click on item with items in his cursor)
                                    // We will add item in her cursor if it can and remove ingredients
                                    case SWAP_WITH_CURSOR -> {
                                        // Check if recipe match again with used pattern to prevent bugs
                                        if (recipe.matches(getRecipe(), patternUsed)) {
                                            // Get player cursor
                                            ItemStack cursor = e.getCursor();
                                            assert cursor != null;
                                            // Check if cursor is similar to result
                                            if (result.isSimilar(cursor)) {
                                                // Check if we can add result to cursor (if we don't exceed max stack size)
                                                if (cursor.getAmount() + recipe.getResultAmount() <= cursor.getMaxStackSize()) {
                                                    // Add items in cursor
                                                    cursor.setAmount(cursor.getAmount() + recipe.getResultAmount());
                                                    // Remove ingredients
                                                    for (int i = 0; i < 9; i++) {
                                                        ItemStack recipeItem = getRecipe()[i];
                                                        if (recipeItem != null) {
                                                            recipeItem.setAmount(recipeItem.getAmount() - patternUsed[i].getAmount());
                                                        }
                                                    }
                                                    // Refresh recipe because we update grid
                                                    refreshRecipe();
                                                }
                                            }
                                        } else {
                                            // Refresh result to actualize recipe that doesn't match
                                            refreshRecipe();
                                        }
                                    }
                                    // If player shift click, do like crafting table and craft all we can
                                    case MOVE_TO_OTHER_INVENTORY -> {
                                        // Check if recipe match again with used pattern to prevent bugs
                                        if (recipe.matches(getRecipe(), patternUsed)) {
                                            // Make a model of result and calculate max amount of items
                                            // That we can add in player inventory
                                            ItemStack itemModel = result.getAsItemStack();
                                            int maxToAdd = 0;
                                            // Get player inventory and check all slots except offhand
                                            // And armor slots
                                            PlayerInventory pi = e.getWhoClicked().getInventory();
                                            for (ItemStack slot : pi.getStorageContents()) {
                                                // Check if slot is empty
                                                if (slot == null || slot.getType() == Material.AIR) {
                                                    // If empty add max stack size
                                                    maxToAdd += itemModel.getMaxStackSize();
                                                } else {
                                                    // Else if result and slot are similar, add max items
                                                    // That we can add in this slot
                                                    if (result.isSimilar(slot)) {
                                                        maxToAdd += itemModel.getMaxStackSize() - slot.getAmount();
                                                    }
                                                }
                                            }

                                            // Make a copy of the grid
                                            ItemStack[] gridCopy = Arrays.stream(getRecipe()).map(itemStack -> {
                                                return itemStack == null ? null : itemStack.clone();
                                            }).toList().toArray(new ItemStack[0]);
                                            // Count how much time we can repeat the recipe
                                            // Depending on max amount that we can add and
                                            // Amount of items in crafting grid
                                            int repeat = 0;

                                            // Check how much time we can repeat the recipe
                                            while (recipe.matches(gridCopy, patternUsed) && (repeat + 1) * recipe.getResultAmount() <= maxToAdd) {
                                                repeat++;
                                                // Remove ingredients in grid copy
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

                                            // Calculate how many full stacks and items we will craft
                                            int stacks = repeat * recipe.getResultAmount() / itemModel.getMaxStackSize();
                                            int items = repeat * recipe.getResultAmount() % itemModel.getMaxStackSize();

                                            // Make a copy of result to change amount safely
                                            CustomItemStack resultCopy = result.clone();

                                            // First we give all full stacks to player
                                            for (int i = 0; i < stacks; i++) {
                                                resultCopy.setAmount(itemModel.getMaxStackSize());
                                                PlayerUtils.giveOrDrop((Player) e.getWhoClicked(), resultCopy.getAsItemStack());
                                            }

                                            // Then we give rest of items to player
                                            if (items > 0) {
                                                resultCopy.setAmount(items);
                                                PlayerUtils.giveOrDrop((Player) e.getWhoClicked(), resultCopy.getAsItemStack());
                                            }

                                            // Update grid with copy
                                            for (int i = 0; i < 9; i++) {
                                                replaceExistingItem(craftingGrid[i], gridCopy[i]);
                                            }
                                        }

                                        // We refresh recipe because we update grid or recipe doesn't match again
                                        refreshRecipe();
                                    }
                                }

                                // Return false because item stack can't be taken
                                return false;
                            });
                        }
                        // Return to prevent result reset
                        return;
                    }
                }
            }
        }

        // Reset result if no recipe match
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
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8&m---------------------------"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8Shift clique pour tous craft"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8Clique pour en craft un"));
        meta.setLore(lore);

        // If it has a custom item id, remove it
        if (meta.getPersistentDataContainer().has(CustomItem.idKey, PersistentDataType.STRING)) {
            meta.getPersistentDataContainer().remove(CustomItem.idKey);
        }
        // Update item meta
        result.setItemMeta(meta);

        return result;
    }

    /**
     * Used to get items in grid in an array
     * @return grid
     */
    @NotNull
    private ItemStack[] getRecipe() {
        // Create grid array
        ItemStack[] recipe = new ItemStack[9];

        // Get items in each grid slots
        for (int i = 0; i < 9; i++) {
            ItemStack slotItem = getItemInSlot(craftingGrid[i]);
            if (slotItem != null && slotItem.getType() != Material.AIR) recipe[i] = slotItem;
        }

        return recipe;
    }
}
