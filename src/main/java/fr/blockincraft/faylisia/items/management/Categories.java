package fr.blockincraft.faylisia.items.management;

import fr.blockincraft.faylisia.items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Categories {
    COOL_DIAMOND(Material.DIAMOND_HORSE_ARMOR, -1, "&d&lCool Diamond",
            "&8The most cool category",
            "&8of the game",
            "",
            "&8Contient %items% items"
    );

    public final Material material;
    public final int customModelData;
    public final String name;
    public final String[] lore;
    public final List<CustomItem> items = new ArrayList<>();

    Categories(Material material, int customModelData, String name, String... lore) {
        this.material = material;
        this.customModelData = customModelData;
        this.name = name;
        this.lore = lore;
    }

    public ItemStack getAsItemStack(int amount) {
        if (amount < 1) amount = 1;
        if (amount > material.getMaxStackSize()) amount = material.getMaxStackSize();

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name + " Category"));
        meta.setLore(Arrays.stream(lore).map(
                text -> ChatColor.translateAlternateColorCodes('&', text.replace("%items%", String.valueOf(items.size())))
        ).toList());
        meta.setCustomModelData(customModelData < 0 ? null : customModelData);

        itemStack.setItemMeta(meta);
        itemStack.setAmount(amount);

        return itemStack;
    }
}
