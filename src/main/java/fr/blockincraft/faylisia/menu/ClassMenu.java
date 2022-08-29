package fr.blockincraft.faylisia.menu;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.player.Classes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassMenu extends ChestMenu {
    private static final Map<Classes, Integer> slotPerClasses;

    static {
        slotPerClasses = new HashMap<>();
        slotPerClasses.put(Classes.WIZARD, 20);
        slotPerClasses.put(Classes.MURDER, 22);
        slotPerClasses.put(Classes.SABREUR, 24);
    }

    private final ChestMenu from;
    private final CustomPlayerDTO player;

    public ClassMenu(CustomPlayerDTO player, ClassMenu from) {
        super("&d&lClasses", 5);
        this.from = from;
        this.player = player;

        if (from != null) {
            ItemStack returnItem = new ItemStack(Material.BARRIER);
            ItemMeta returnItemMeta = returnItem.getItemMeta();

            returnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRetour"));
            returnItemMeta.setLore(Arrays.asList(
                    ChatColor.translateAlternateColorCodes('&', "&8Clique pour retourner"),
                    ChatColor.translateAlternateColorCodes('&', "&8en arrière.")
            ));

            returnItem.setItemMeta(returnItemMeta);
            this.replaceExistingItem(40, returnItem, e -> {
                if (e.getWhoClicked() instanceof Player pl) {
                    if (from != null) {
                        from.open(pl);
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
            this.replaceExistingItem(40, closeItem, e -> {
                if (e.getWhoClicked() instanceof Player pl) {
                    pl.closeInventory();
                }
                return false;
            });
        }

        refreshMenu();
    }

    @Override
    public void refreshMenu() {
        slotPerClasses.forEach((classes, slot) -> {
            this.replaceExistingItem(slot, classes == player.getClasses() ? Classes.HUMAN.getAsItemStack() :  classes.getAsItemStack(), e -> {
                player.setClasses(classes == player.getClasses() ? Classes.HUMAN : classes);

                Player pl = Bukkit.getPlayer(player.getPlayer());
                if (pl != null) {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("%class%", classes.name);

                    pl.sendMessage(Messages.YOU_SELECTED_A_CLASS.get(parameters));
                }

                refreshMenu();
                return false;
            });
        });
    }
}
