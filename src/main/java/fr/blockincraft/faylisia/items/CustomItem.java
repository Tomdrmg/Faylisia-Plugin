package fr.blockincraft.faylisia.items;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.api.serializer.CustomItemSerializer;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.recipes.Recipe;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@JsonSerialize(using = CustomItemSerializer.class)
public class CustomItem {
    public static final NamespacedKey idKey = new NamespacedKey(Faylisia.getInstance(), "custom-id");
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered = false;

    private final Material material;
    private final String id;
    private int customModelData = -1;
    private String name;
    private String[] lore = new String[0];
    private boolean enchantable = false;
    private boolean disenchantable = false;
    private Rarity rarity = Rarity.COMMON;
    private Recipe[] recipes = new Recipe[0];
    private Categories category = null;
    private int color = -1;

    public CustomItem(Material material, String id) throws InvalidBuildException {
        this.material = material;
        this.id = id;
    }

    public ItemStack getAsItemStack() {
        if (!registered) return null;

        ItemStack itemStack = new ItemStack(material);

        ItemMeta meta = itemStack.getItemMeta();

        if (meta instanceof LeatherArmorMeta lam && color != -1) {
            lam.setColor(Color.fromRGB(color));
        }

        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);

        List<String> lore = new ArrayList<>(firstLore());
        if (this.lore.length > 0 && lore.size() > 0) lore.add("");
        for (String l : this.lore) {
            lore.add(ChatColor.translateAlternateColorCodes('&', l));
        }
        lore.addAll(buildLore());

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        meta.setUnbreakable(true);
        meta.setCustomModelData(customModelData <= 0 ? null : customModelData);
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.getByChar(rarity.getColorChar()) + ChatColor.BOLD.toString() + name);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack getAsItemStack(int amount) {
        if (!registered) return null;

        if (amount > material.getMaxStackSize()) amount = material.getMaxStackSize();
        if (amount < 1) amount = 1;

        ItemStack itemStack = getAsItemStack();
        itemStack.setAmount(amount);

        return itemStack;
    }

    public List<String> firstLore() {
        return new ArrayList<>();
    }

    public List<String> buildLore() {
        List<String> lore = new ArrayList<>(moreLore());

        lore.add("");
        lore.add(rarity.magicalChars() ?
                ChatColor.getByChar(rarity.getColorChar()).toString() + ChatColor.BOLD + ChatColor.MAGIC + "۞ " + ChatColor.RESET + ChatColor.getByChar(rarity.getColorChar()) + ChatColor.BOLD + getType() + " " + rarity.getName() + ChatColor.MAGIC + " ۞"
                :
                ChatColor.getByChar(rarity.getColorChar()).toString() + ChatColor.BOLD + "۞ " + getType() + " " + rarity.getName() + " ۞"
        );

        return lore;
    }

    protected List<String> moreLore() {
        return new ArrayList<>();
    }

    public CustomItem setCustomModelData(int customModelData) {
        if (registered) throw new ChangeRegisteredItem();
        this.customModelData = customModelData;
        return this;
    }

    public CustomItem setColor(int color) {
        if (registered) throw new ChangeRegisteredItem();
        this.color = color;
        return this;
    }

    public int getColor() {
        return color;
    }

    public CustomItem setName(String name) {
        if (registered) throw new ChangeRegisteredItem();
        this.name = name;
        return this;
    }

    public CustomItem setLore(String... lore) {
        if (registered) throw new ChangeRegisteredItem();
        this.lore = lore == null ? new String[0] : lore;
        return this;
    }

    public CustomItem setEnchantable(boolean enchantable)  {
        if (registered) throw new ChangeRegisteredItem();
        this.enchantable = enchantable;
        return this;
    }

    public CustomItem setDisenchantable(boolean disenchantable) {
        if (registered) throw new ChangeRegisteredItem();
        this.disenchantable = disenchantable;
        return this;
    }

    public CustomItem setRarity(Rarity rarity) {
        if (registered) throw new ChangeRegisteredItem();
        this.rarity = rarity;
        return this;
    }

    public CustomItem setRecipe(Recipe... recipe) {
        if (registered) throw new ChangeRegisteredItem();
        this.recipes = recipe == null ? new Recipe[0] : recipe;
        return this;
    }

    public CustomItem setCategory(Categories category) {
        this.category = category;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public String getId() {
        return id;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getName() {
        return name;
    }

    public String[] getLore() {
        return lore;
    }

    public boolean isEnchantable() {
        return enchantable;
    }

    public boolean isDisenchantable() {
        return disenchantable;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Recipe[] getRecipes() {
        return recipes;
    }

    public Categories getCategory() {
        return category;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void register() {
        if (registered) throw new ChangeRegisteredItem();

        if (material == null) throw new InvalidBuildException("Material cannot be null!");
        if (id == null || id.isEmpty()) throw new InvalidBuildException("Id cannot be null or empty!");
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]+!");
        if (registry.itemIdUsed(id)) throw new InvalidBuildException("Id already used!");
        if (rarity == null) throw new InvalidBuildException("Rarity cannot be null!");

        registerOthers();

        registered = true;
        registry.registerItem(this);
    }

    protected String getType() {
        return "OBJET";
    }

    protected void registerOthers() {

    }

    protected static class ChangeRegisteredItem extends RuntimeException {
        public ChangeRegisteredItem() {
            super("You tried to edit a registered item!");
        }
    }

    protected static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid custom item build: " + cause);
        }
    }
}
