package fr.blockincraft.faylisia.menu.viewer;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.recipes.Recipe;
import fr.blockincraft.faylisia.menu.ChestMenu;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

/**
 * A chest menu which show recipes of a custom item
 */
public class RecipeViewerMenu extends ChestMenu {
    private final ChestMenu from;
    private final CustomItem item;
    private final Recipe[] recipes;
    private int recipeIndex = 0;

    /**
     * Initialize menu
     * @param item item to show recipes
     * @param from previous menu
     */
    public RecipeViewerMenu(@NotNull CustomItem item, @Nullable ChestMenu from) {
        super(item.getRarity(new CustomItemStack(item, 1)).colorChar + "&l" + item.getName(new CustomItemStack(item, 1)), 6);
        this.from = from;

        // Verify that item has recipe(s)
        this.item = item;
        recipes = item.getRecipes();
        if (recipes == null || recipes.length == 0) throw new RuntimeException("You cannot create a recipe menu with a non craftable item!");

        setPlayerInventoryClickable(false);
        setEmptySlotsClickable(false);

        refreshMenu();
    }

    @Override
    public void refreshMenu() {
        // Fill all background with background item
        for (int i = 0; i < 45; i++) {
            ItemStack bgItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.replaceExistingItem(i, bgItem, (e) -> false);
        }

        // Fill last line with other item
        for (int i = 45; i < 54; i++) {
            ItemStack bgItem = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.replaceExistingItem(i, bgItem, (e) -> false);
        }

        // Get recipe to display and check if result slot is in menu
        Recipe recipeToDisplay = recipes[recipeIndex];
        if (recipeToDisplay == null) return;
        int resultSlot = recipeToDisplay.getResultSlot();
        if (resultSlot >= 54 || resultSlot < 0) return;

        // Show all ingredients of recipe in menu
        for (Map.Entry<Integer, CustomItemStack> entry : recipeToDisplay.getForDisplay().entrySet()) {
            // If ingredient is out of menu skip it
            int slot = entry.getKey();
            if (slot >= 45 || slot < 0) continue;

            // If item is null, clear slot
            if (entry.getValue() == null) {
                this.replaceExistingItem(slot, new ItemStack(Material.AIR), e -> false);
                continue;
            }

            // Get custom item from ingredient
            CustomItem customItem = entry.getValue().getItem();
            // Get an unusable ingredient
            ItemStack itemStack = getItemStackFromCustomUnusable(entry.getValue());

            // Change item to set ingredient
            this.replaceExistingItem(slot, itemStack, e -> {
                if (e.getWhoClicked() instanceof Player player && customItem.getRecipes() != null && customItem.getRecipes().length > 0) {
                    new RecipeViewerMenu(customItem, this).open(player);
                }
                return false;
            });
        }

        // Change result slot item to make an unusable result
        this.replaceExistingItem(resultSlot, getItemStackFromCustomUnusable(new CustomItemStack(item, recipeToDisplay.getResultAmount())), e -> false);

        // Create previous page item
        ItemStack previousPage = new ItemStack(Material.PAPER);
        ItemMeta previousPageMeta = previousPage.getItemMeta();

        previousPageMeta.setDisplayName(ColorsUtils.translateAll("&dPage Précédente"));
        previousPageMeta.setLore(Arrays.asList(
                ColorsUtils.translateAll("&8Page &7" + (recipeIndex + 1) + "&8/&7" + recipes.length)
        ));

        previousPage.setItemMeta(previousPageMeta);
        this.replaceExistingItem(46, previousPage, e -> {
            if (recipeIndex > 0) {
                recipeIndex = recipeIndex - 1;
                refreshMenu();
            }
            return false;
        });

        // Add return or close item depending on previous menu is existing or no
        if (from != null) {
            ItemStack returnItem = new ItemStack(Material.BARRIER);
            ItemMeta returnItemMeta = returnItem.getItemMeta();

            returnItemMeta.setDisplayName(ColorsUtils.translateAll("&cRetour"));
            returnItemMeta.setLore(Arrays.asList(
                    ColorsUtils.translateAll("&8Clique pour retourner"),
                    ColorsUtils.translateAll("&8en arrière.")
            ));

            returnItem.setItemMeta(returnItemMeta);
            this.replaceExistingItem(49, returnItem, e -> {
                if (e.getWhoClicked() instanceof Player player) {
                    if (from != null) {
                        from.open(player);
                    }
                }
                return false;
            });
        } else {
            ItemStack closeItem = new ItemStack(Material.BARRIER);
            ItemMeta closeItemMeta = closeItem.getItemMeta();

            closeItemMeta.setDisplayName(ColorsUtils.translateAll("&cFermer"));
            closeItemMeta.setLore(Arrays.asList(
                    ColorsUtils.translateAll("&8Clique pour fermer"),
                    ColorsUtils.translateAll("&8le menu.")
            ));

            closeItem.setItemMeta(closeItemMeta);
            this.replaceExistingItem(49, closeItem, e -> {
                if (e.getWhoClicked() instanceof Player player) {
                    player.closeInventory();
                }
                return false;
            });
        }

        // Create next page item
        ItemStack nextPage = new ItemStack(Material.PAPER);
        ItemMeta nextPageMeta = nextPage.getItemMeta();

        nextPageMeta.setDisplayName(ColorsUtils.translateAll("&dPage Suivante"));
        nextPageMeta.setLore(Arrays.asList(
                ColorsUtils.translateAll("&8Page &7" + (recipeIndex + 1) + "&8/&7" + recipes.length)
        ));

        nextPage.setItemMeta(nextPageMeta);
        this.replaceExistingItem(52, nextPage, e -> {
            if (recipeIndex < recipes.length - 1) {
                recipeIndex = recipeIndex + 1;
                refreshMenu();
            }
            return false;
        });
    }

    /**
     * Get an item stack from a custom item but which isn't recognize like this custom item
     * To prevent duplication of this item, if it is taken, it will be unusable
     * @param customItemStack custom item stack to make unusable
     * @return unusable item stack
     */
    @NotNull
    private ItemStack getItemStackFromCustomUnusable(@NotNull CustomItemStack customItemStack) {
        // Create item
        ItemStack itemStack = customItemStack.getAsItemStack();

        // Get meta and remove item id if it has
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getPersistentDataContainer().has(CustomItem.idKey, PersistentDataType.STRING)) meta.getPersistentDataContainer().remove(CustomItem.idKey);
        // Then apply it
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
