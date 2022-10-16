package fr.blockincraft.faylisia.items.management;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.menu.viewer.CategoryViewerMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Categories are used to sort {@link CustomItem} for display in {@link CategoryViewerMenu}
 */
public enum Categories {
    RESOURCES(Material.IRON_INGOT, 1, "&fRessources", "&7%items% items"),
    TOOLS(Material.GOLDEN_SHOVEL, 1, "&bOutils", "&7%items% items"),
    UPGRADES(Material.AMETHYST_SHARD, 1, "&eAméliorations", "&7%items% items");

    public final Material material;
    public final int customModelData;
    public final String name;
    public final String[] lore;
    public final List<CustomItem> items = new ArrayList<>();

    Categories(@NotNull Material material, int customModelData, @NotNull String name, @NotNull String... lore) {
        this.material = material;
        this.customModelData = customModelData;
        this.name = name;
        this.lore = lore;
    }

    /**
     * Create an item stack to represent the category in {@link CategoryViewerMenu}
     * @param amount size of item stack
     * @return created item stack
     */
    @NotNull
    public ItemStack getAsItemStack(int amount) {
        if (amount < 1) amount = 1;
        if (amount > material.getMaxStackSize()) amount = material.getMaxStackSize();

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;
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
