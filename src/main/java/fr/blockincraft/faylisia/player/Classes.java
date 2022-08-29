package fr.blockincraft.faylisia.player;

import fr.blockincraft.faylisia.displays.Skins;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    Classes(char color, String name, Skins skin, Material material, int customModelData) {
        this.color = color;
        this.name = name;
        this.skin = skin;
        this.material = material;
        this.customModelData = customModelData;
    }

    public ItemStack getAsItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&" + color + name));
        meta.setCustomModelData(customModelData < 0 ? null : customModelData);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
