package fr.blockincraft.faylisia.listeners;

import fr.blockincraft.faylisia.menu.ChestMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listeners used to make {@link ChestMenu} functional
 */
public class MenuListener implements Listener {
    public static final Map<UUID, ChestMenu> menus = new HashMap<>();

    /**
     * Call {@link ChestMenu.MenuCloseHandler#onClose(Player)} of the chest menu
     */
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        ChestMenu menu = menus.remove(e.getPlayer().getUniqueId());

        // Check if player has a menu
        if (menu != null) {
            menu.getMenuCloseHandler().onClose((Player) e.getPlayer());
        }
    }

    /**
     * Call {@link ChestMenu.MenuDragHandler#onDrag(InventoryDragEvent)} of the chest menu
     */
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        ChestMenu menu = menus.get(e.getWhoClicked().getUniqueId());

        // Check if player has a menu and if menu has a drag handler
        if (menu != null && menu.getMenuDragHandler() != null) {
            e.setCancelled(!menu.getMenuDragHandler().onDrag(e));
        }
    }

    /**
     * Call {@link ChestMenu.MenuClickHandler#onClick(InventoryClickEvent)} of the slot in the chest menu
     */
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ChestMenu menu = menus.get(e.getWhoClicked().getUniqueId());

        // Check if player has a menu
        if (menu != null) {
            // Check if clicked slot is in chest inventory or in player inventory
            if (e.getRawSlot() < e.getInventory().getSize()) {
                // Get handler associated to slot in menu
                ChestMenu.MenuClickHandler handler = menu.getMenuClickHandler(e.getSlot());

                if (handler == null) {
                    // Cancel click if slot is empty and if empty slots aren't clickable in menu
                    e.setCancelled(!menu.isEmptySlotsClickable() && (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR));
                } else {
                    // Call handler
                    e.setCancelled(!handler.onClick(e));
                }
            } else {
                // Call player inventory click handler of the menu
                e.setCancelled(!menu.getPlayerInventoryClickHandler().onClick(e));
            }
        }
    }
}
