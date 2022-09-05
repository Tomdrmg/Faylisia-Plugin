package fr.blockincraft.faylisia.player;

import fr.blockincraft.faylisia.displays.Skins;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * All players class with a defined skin, they will have bonus later
 */
public enum Classes {
    HUMAN('f',
            "Humain",
            Skins.GOLD,
            Material.PLAYER_HEAD,
            -1
    ),
    WIZARD('9',
            "Sorcier",
            Skins.WIZARD,
            Material.ENCHANTED_BOOK,
            -1
    ),
    SABREUR('6',
            "Sabreur",
            Skins.LIME,
            Material.DIAMOND_SWORD,
            -1
    ),
    MURDER('8',
            "Meurtrier",
            Skins.AQUA,
            Material.IRON_SWORD,
            -1
    );

    public final char color;
    public final String name;
    public final Skins skin;
    public final Material material;
    public final int customModelData;

    /**
     * @param color color chat to use {@link ChatColor}
     * @param name name of the class
     * @param skin skin of players which have selected this class
     * @param material material of item stack in menus
     * @param customModelData custom model data of item stack in menus
     */
    Classes(char color, @NotNull String name, @NotNull Skins skin, @NotNull Material material, int customModelData) {
        this.color = color;
        this.name = name;
        this.skin = skin;
        this.material = material;
        this.customModelData = customModelData;
    }

    /**
     * This method generate and return the item stack which represent this class
     * @return item stack which represent the class
     */
    @NotNull
    public ItemStack getAsItemStack() {
        // Create the item stack
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        // Apply display name and custom model data
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&" + color + name));
        meta.setCustomModelData(customModelData < 0 ? null : customModelData);

        // Add item flags
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // Apply item meta
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
