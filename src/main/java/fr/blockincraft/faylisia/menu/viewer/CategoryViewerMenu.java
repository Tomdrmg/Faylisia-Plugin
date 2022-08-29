package fr.blockincraft.faylisia.menu.viewer;

import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.menu.ChestMenu;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryViewerMenu extends ChestMenu {
    private final ChestMenu from;
    private final Categories category;
    private final boolean withGive;
    private final int totalPage;
    private int currentPage = 0;

    public CategoryViewerMenu(ChestMenu from, Categories category, boolean withGive) {
        super("&8&lItems &d&l> &8&l" + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', category.name)), 6);

        this.from = from;
        this.category = category;
        int totalPage = (int) Math.ceil(category.items.size() / 28.0);
        this.totalPage = totalPage == 0 ? 1 : totalPage;
        this.withGive = withGive;

        this.setEmptySlotsClickable(false);
        this.setPlayerInventoryClickable(false);

        refreshMenu();
    }

    @Override
    public void refreshMenu() {
        for (int i = 0; i < 54; i++) {
            ItemStack bgItem = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.addItem(i, bgItem, (e) -> false);
        }

        List<CustomItem> it = category.items;
        List<CustomItem> items = new ArrayList<>();

        for (int i = currentPage * 28; i < it.size() && i < (currentPage + 1) * 28; i++) {
            items.add(it.get(i));
        }

        for (int i = 0; i < 28; i++) {
            int slot = 10 + i + i / 7 * 2;
            if (i < items.size()) {
                CustomItem item = items.get(i);
                this.replaceExistingItem(slot, getItemStackFromCustomUnusable(new CustomItemStack(item, 1)), e -> {
                    if (e.getWhoClicked() instanceof  Player player) {
                        if (e.getClick() == ClickType.MIDDLE && player.hasPermission("faylisia.items.give")) {
                            PlayerUtils.giveOrDrop(player, item.getAsItemStack(1));
                            return false;
                        }
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
                this.replaceExistingItem(slot, new ItemStack(Material.AIR), e -> false);
            }
        }

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

    private ItemStack getItemStackFromCustomUnusable(CustomItemStack customItemStack) {
        ItemStack itemStack = customItemStack.getItem().getAsItemStack(customItemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getPersistentDataContainer().has(CustomItem.idKey, PersistentDataType.STRING)) meta.getPersistentDataContainer().remove(CustomItem.idKey);
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8&m--------------------------"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8Clique pour voir les crafts"));
        if (withGive) lore.add(ChatColor.translateAlternateColorCodes('&', "&8Clique molette pour l'obtenir"));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
