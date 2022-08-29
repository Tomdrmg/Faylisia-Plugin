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

public class MenuListener implements Listener {
    public static final Map<UUID, ChestMenu> menus = new HashMap<>();

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        ChestMenu menu = menus.remove(e.getPlayer().getUniqueId());

        if (menu != null) {
            menu.getMenuCloseHandler().onClose((Player) e.getPlayer());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        ChestMenu menu = menus.get(e.getWhoClicked().getUniqueId());

        if (menu != null && menu.getMenuDragHandler() != null) {
            e.setCancelled(!menu.getMenuDragHandler().onDrag(e));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ChestMenu menu = menus.get(e.getWhoClicked().getUniqueId());

        if (menu != null) {
            if (e.getRawSlot() < e.getInventory().getSize()) {
                ChestMenu.MenuClickHandler handler = menu.getMenuClickHandler(e.getSlot());

                if (handler == null) {
                    e.setCancelled(!menu.isEmptySlotsClickable() && (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR));
                } else {
                    e.setCancelled(!handler.onClick(e));
                }
            } else {
                e.setCancelled(!menu.getPlayerInventoryClickHandler().onClick(e));
            }
        }
    }
}
