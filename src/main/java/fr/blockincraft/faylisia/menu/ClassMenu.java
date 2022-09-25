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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassMenu extends ChestMenu {
    private static final Map<Classes, Integer> slotPerClasses;

    // Bind class to their slot
    static {
        slotPerClasses = new HashMap<>();
        slotPerClasses.put(Classes.SWORDSMAN, 20);
        slotPerClasses.put(Classes.MAGE, 22);
        slotPerClasses.put(Classes.TANK, 24);
    }

    private final ChestMenu from;
    private final CustomPlayerDTO player;

    /**
     * Initialize menu
     * @param player player which will be change her class
     * @param from previous menu
     */
    public ClassMenu(@NotNull CustomPlayerDTO player, @Nullable ClassMenu from) {
        super("&d&lClasses", 5);
        this.from = from;
        this.player = player;

        // Set return item if previous menu isn't null or close item
        if (from != null) {
            // Create item stack and get meta
            ItemStack returnItem = new ItemStack(Material.BARRIER);
            ItemMeta returnItemMeta = returnItem.getItemMeta();

            // Change display name and lore
            returnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRetour"));
            returnItemMeta.setLore(Arrays.asList(
                    ChatColor.translateAlternateColorCodes('&', "&8Clique pour retourner"),
                    ChatColor.translateAlternateColorCodes('&', "&8en arrière.")
            ));

            // Update meta and change item
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
            // Create item stack and get meta
            ItemStack closeItem = new ItemStack(Material.BARRIER);
            ItemMeta closeItemMeta = closeItem.getItemMeta();

            // Change display name and lore
            closeItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cFermer"));
            closeItemMeta.setLore(Arrays.asList(
                    ChatColor.translateAlternateColorCodes('&', "&8Clique pour fermer"),
                    ChatColor.translateAlternateColorCodes('&', "&8le menu.")
            ));

            // Update meta and change item
            closeItem.setItemMeta(closeItemMeta);
            this.replaceExistingItem(40, closeItem, e -> {
                if (e.getWhoClicked() instanceof Player pl) {
                    pl.closeInventory();
                }
                return false;
            });
        }

        // Refresh menu for the first time to initialize class selection items
        refreshMenu();
    }

    @Override
    public void refreshMenu() {
        // Set class items depending on current player class,
        // If class isn't human replace selected class by human
        slotPerClasses.forEach((classes, slot) -> {
            this.replaceExistingItem(slot, classes == player.getClasses() ? Classes.EXPLORER.getAsItemStack() :  classes.getAsItemStack(), e -> {
                // On click change player class
                player.setClasses(classes == player.getClasses() ? Classes.EXPLORER : classes);

                // Send confirmation message
                Player pl = Bukkit.getPlayer(player.getPlayer());
                if (pl != null) {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("%class%", classes.name);

                    pl.sendMessage(Messages.YOU_SELECTED_A_CLASS.get(parameters));
                }

                // Refresh menu to update class items because current class changed
                refreshMenu();
                return false;
            });
        });
    }
}
