package fr.blockincraft.faylisia.menu.viewer;

import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.menu.ChestMenu;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryViewerMenu extends ChestMenu {
    private final ChestMenu from;
    private final Categories category;
    private final boolean withGive;
    private final int totalPage;
    private int currentPage = 0;

    /**
     * Initialize menu
     * @param from previous menu
     * @param category category to display
     * @param withGive if player can give him custom items
     */
    public CategoryViewerMenu(ChestMenu from, Categories category, boolean withGive) {
        super("&8&lItems &d&l> &8&l" + net.md_5.bungee.api.ChatColor.stripColor(ColorsUtils.translateAll(category.name)), 6);

        this.from = from;
        this.category = category;
        // Calculate page amount
        int totalPage = (int) Math.ceil(category.items.size() / 28.0);
        this.totalPage = totalPage == 0 ? 1 : totalPage;
        this.withGive = withGive;

        this.setEmptySlotsClickable(false);
        this.setPlayerInventoryClickable(false);

        refreshMenu();
    }

    @Override
    public void refreshMenu() {
        // Fill background with background item
        for (int i = 0; i < 54; i++) {
            ItemStack bgItem = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.addItem(i, bgItem, (e) -> false);
        }

        // Get items of category
        List<CustomItem> it = category.items;
        List<CustomItem> items = new ArrayList<>();

        // Add all items of current page
        for (int i = currentPage * 28; i < it.size() && i < (currentPage + 1) * 28; i++) {
            items.add(it.get(i));
        }

        // Show all items of the page
        for (int i = 0; i < 28; i++) {
            // Calculate slot
            int slot = 10 + i + i / 7 * 2;
            // If item has to be placed in the slot add it else clear slot
            if (i < items.size()) {
                // Add item
                CustomItem item = items.get(i);
                this.replaceExistingItem(slot, getItemStackFromCustomUnusable(new CustomItemStack(item, 1)), e -> {
                    if (e.getWhoClicked() instanceof Player player) {
                        // If click is middle, click to give, and if player can give
                        // Itself then give an item to player
                        if (e.getClick() == ClickType.MIDDLE && player.hasPermission("faylisia.items.give")) {
                            PlayerUtils.giveOrDrop(player, new CustomItemStack(item, 1).getAsItemStack());
                            return false;
                        }

                        // In other cases try to open menu
                        if (item.getRecipes() != null && item.getRecipes().length > 0) {
                            try {
                                new RecipeViewerMenu(item, this).open(player);
                            } catch (Exception ex) {
                                // We handle the Runtime Exception in case of it don't exec 'return false'
                                // even if exception cannot be generated because we check conditions before
                                ex.printStackTrace();
                            }
                        }
                    }
                    return false;
                });
            } else {
                // Clear slot
                this.replaceExistingItem(slot, new ItemStack(Material.AIR), e -> false);
            }
        }

        // Create open wiki item
        ItemStack wiki = new ItemStack(Material.BOOK);
        ItemMeta wikiMeta = wiki.getItemMeta();

        wikiMeta.setDisplayName(ColorsUtils.translateAll("&dWiki"));
        wikiMeta.setLore(Arrays.asList(
                ColorsUtils.translateAll("&8Clique pour accéder"),
                ColorsUtils.translateAll("&8au wiki")
        ));

        wiki.setItemMeta(wikiMeta);
        this.replaceExistingItem(4, wiki, e -> {
            if (e.getWhoClicked() instanceof Player player) {
                BaseComponent message = new TextComponent(Messages.WIKI_MESSAGE.get());
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://faylisia.fr/wiki"));
                player.spigot().sendMessage(message);
            }
            return false;
        });

        // Create previous page item
        ItemStack previousPage = new ItemStack(Material.PAPER);
        ItemMeta previousPageMeta = previousPage.getItemMeta();

        previousPageMeta.setDisplayName(ColorsUtils.translateAll("&dPage Précédente"));
        previousPageMeta.setLore(Arrays.asList(
                ColorsUtils.translateAll("&8Page &7" + (currentPage + 1) + "&8/&7" + totalPage)
        ));

        previousPage.setItemMeta(previousPageMeta);
        this.replaceExistingItem(46, previousPage, e -> {
            if (currentPage > 0) {
                currentPage = currentPage - 1;
                refreshMenu();
            }
            return false;
        });

        // Create return or close item depending on previous menu existing or no
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
                ColorsUtils.translateAll("&8Page &7" + (currentPage + 1) + "&8/&7" + totalPage)
        ));

        nextPage.setItemMeta(nextPageMeta);
        this.replaceExistingItem(52, nextPage, e -> {
            if (currentPage < totalPage - 1) {
                currentPage = currentPage + 1;
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

        // Get or create lore for item
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

        // Add click indications
        lore.add(ColorsUtils.translateAll("&8&m--------------------------"));
        lore.add(ColorsUtils.translateAll("&8Clique pour voir les crafts"));
        if (withGive) lore.add(ColorsUtils.translateAll("&8Clique molette pour l'obtenir"));

        // Apply new lore
        meta.setLore(lore);

        // Then apply it
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
