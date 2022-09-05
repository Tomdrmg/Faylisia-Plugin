package fr.blockincraft.faylisia.menu.viewer;

import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.menu.ChestMenu;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A chest menu which show all categories of custom items
 */
public class ItemsViewerMenu extends ChestMenu {
    private final ChestMenu from;
    private final int totalPage;
    private int currentPage = 0;

    /**
     * Initialize menu
     * @param from previous menu
     */
    public ItemsViewerMenu(@Nullable ChestMenu from) {
        super("&8&lItems", 6);

        // Calculate page amount
        this.from = from;
        totalPage = (int) Math.ceil(Categories.values().length / 28.0);

        setEmptySlotsClickable(false);
        setPlayerInventoryClickable(false);

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

        // Get categories
        Categories[] cat = Categories.values();
        List<Categories> categories = new ArrayList<>();

        // Add all categories of current page
        for (int i = currentPage * 28; i < cat.length && i < (currentPage + 1) * 28; i++) {
            categories.add(cat[i]);
        }

        // Set all categories slots
        for (int i = 0; i < 28; i++) {
            // Calculate slot
            int slot = 10 + i + i / 7 * 2;
            // If category has to be placed in the slot add it else clear slot
            if (i < categories.size()) {
                // Add category item
                Categories category = categories.get(i);
                this.replaceExistingItem(slot, category.getAsItemStack(1), e -> {
                    if (e.getWhoClicked() instanceof Player player) {
                        // Open category
                        new CategoryViewerMenu(this, category, player.hasPermission("faylisia.items.give")).open(player);
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

        wikiMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dWiki"));
        wikiMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Clique pour accéder"),
                ChatColor.translateAlternateColorCodes('&', "&8au wiki")
        ));

        wiki.setItemMeta(wikiMeta);
        this.replaceExistingItem(4, wiki, e -> {
            if (e.getWhoClicked() instanceof Player player) {
                BaseComponent message = new TextComponent(Messages.WIKI_MESSAGE.get());
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://faylis.xyz/wiki"));
                player.spigot().sendMessage(message);
            }
            return false;
        });

        // Create previous page item
        ItemStack previousPage = new ItemStack(Material.PAPER);
        ItemMeta previousPageMeta = previousPage.getItemMeta();

        previousPageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dPage Précédente"));
        previousPageMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Page &7" + (currentPage + 1) + "&8/&7" + totalPage)
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

        // Create next page item
        ItemStack nextPage = new ItemStack(Material.PAPER);
        ItemMeta nextPageMeta = nextPage.getItemMeta();

        nextPageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dPage Suivante"));
        nextPageMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8Page &7" + (currentPage + 1) + "&8/&7" + totalPage)
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
}
