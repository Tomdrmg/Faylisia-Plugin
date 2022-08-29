package fr.blockincraft.faylisia.menu.viewer;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.recipes.Recipe;
import fr.blockincraft.faylisia.menu.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Map;

public class RecipeViewerMenu extends ChestMenu {
    private final ChestMenu from;
    private final CustomItem item;
    private final Recipe[] recipes;
    private int recipeIndex = 0;

    public RecipeViewerMenu(CustomItem item, ChestMenu from) {
        super("&" + item.getRarity().getColorChar() + "&l" + item.getName(), 6);
        this.from = from;

        this.item = item;
        recipes = item.getRecipes();
        if (recipes == null || recipes.length == 0) throw new RuntimeException("You cannot create a recipe menu with a non craftable item!");

        setPlayerInventoryClickable(false);
        setEmptySlotsClickable(false);

        refreshMenu();
    }

    @Override
    public void refreshMenu() {
        for (int i = 0; i < 45; i++) {
            ItemStack bgItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.replaceExistingItem(i, bgItem, (e) -> false);
        }

        for (int i = 45; i < 54; i++) {
            ItemStack bgItem = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.replaceExistingItem(i, bgItem, (e) -> false);
        }

        Recipe recipeToDisplay = recipes[recipeIndex];
        if (recipeToDisplay == null) return;
        int resultSlot = recipeToDisplay.getResultSlot();
        if (resultSlot >= 54 || resultSlot < 0) return;

        for (Map.Entry<Integer, CustomItemStack> entry : recipeToDisplay.getForDisplay().entrySet()) {
            int slot = entry.getKey();
            if (slot >= 45 || slot < 0) continue;

            if (entry.getValue() == null) {
                this.replaceExistingItem(slot, new ItemStack(Material.AIR), e -> false);
                continue;
            }

            CustomItem customItem = entry.getValue().getItem();

            ItemStack itemStack = getItemStackFromCustomUnusable(entry.getValue());

            this.replaceExistingItem(slot, itemStack, e -> {
                if (e.getWhoClicked() instanceof Player player && customItem.getRecipes() != null && customItem.getRecipes().length > 0) {
                    new RecipeViewerMenu(customItem, this).open(player);
                }
                return false;
            });
        }

        this.replaceExistingItem(resultSlot, getItemStackFromCustomUnusable(new CustomItemStack(item, recipeToDisplay.getResultAmount())), e -> false);

        ItemStack previousPage = new ItemStack(Material.PAPER);
        ItemMeta previousPageMeta = previousPage.getItemMeta();

        previousPageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dPage Précédente"));
        previousPageMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Page &7" + (recipeIndex + 1) + "&8/&7" + recipes.length)
        ));

        previousPage.setItemMeta(previousPageMeta);
        this.replaceExistingItem(46, previousPage, e -> {
            if (recipeIndex > 0) {
                recipeIndex = recipeIndex - 1;
                refreshMenu();
            }
            return false;
        });

        if (from != null) {
            ItemStack returnItem = new ItemStack(Material.BARRIER);
            ItemMeta returnItemMeta = returnItem.getItemMeta();

            returnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRetour"));
            returnItemMeta.setLore(Arrays.asList(
                    ChatColor.translateAlternateColorCodes('&', "&8Clique pour retourner"),
                    ChatColor.translateAlternateColorCodes('&', "&8en arrière.")
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

            closeItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cFermer"));
            closeItemMeta.setLore(Arrays.asList(
                    ChatColor.translateAlternateColorCodes('&', "&8Clique pour fermer"),
                    ChatColor.translateAlternateColorCodes('&', "&8le menu.")
            ));

            closeItem.setItemMeta(closeItemMeta);
            this.replaceExistingItem(49, closeItem, e -> {
                if (e.getWhoClicked() instanceof Player player) {
                    player.closeInventory();
                }
                return false;
            });
        }

        ItemStack nextPage = new ItemStack(Material.PAPER);
        ItemMeta nextPageMeta = nextPage.getItemMeta();

        nextPageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dPage Suivante"));
        nextPageMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Page &7" + (recipeIndex + 1) + "&8/&7" + recipes.length)
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

    private ItemStack getItemStackFromCustomUnusable(CustomItemStack customItemStack) {
        ItemStack itemStack = customItemStack.getItem().getAsItemStack(customItemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getPersistentDataContainer().has(CustomItem.idKey, PersistentDataType.STRING)) meta.getPersistentDataContainer().remove(CustomItem.idKey);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
