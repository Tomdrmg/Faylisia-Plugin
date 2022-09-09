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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A custom item, similar to a {@link Material} but custom
 */
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

    /**
     * @param material material of item
     * @param id id of custom item (must be unique)
     */
    public CustomItem(@NotNull Material material, @NotNull String id) {
        this.material = material;
        this.id = id;
    }

    /**
     * Create an item stack from this item, so use {@link CustomItemStack#getAsItemStack()} to create an item to give
     * @return created item stack
     */
    @NotNull
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

    /**
     * First lore part, this is util for subclasses, this is lore which will be displayed before {@link CustomItem#lore}
     * @return text to display
     */
    @NotNull
    protected List<String> firstLore() {
        return new ArrayList<>();
    }

    /**
     * Second lore part, this is lore which will be displayed after {@link CustomItem#lore}
     * @return text to display
     */
    @NotNull
    protected List<String> buildLore() {
        // Get other lore part to display before rarity
        List<String> lore = new ArrayList<>(moreLore());

        lore.add("");
        lore.add(rarity.magicalChars() ?
                ChatColor.getByChar(rarity.getColorChar()).toString() + ChatColor.BOLD + ChatColor.MAGIC + "۞ " + ChatColor.RESET + ChatColor.getByChar(rarity.getColorChar()) + ChatColor.BOLD + getType() + " " + rarity.getName() + ChatColor.MAGIC + " ۞"
                :
                ChatColor.getByChar(rarity.getColorChar()).toString() + ChatColor.BOLD + "۞ " + getType() + " " + rarity.getName() + " ۞"
        );

        return lore;
    }

    /**
     * Second lore part, this is lore which will be displayed after {@link CustomItem#lore} but before {@link CustomItem#buildLore()} <br/>
     * This is util for subclasses
     * @return text to display
     */
    @NotNull
    protected List<String> moreLore() {
        return new ArrayList<>();
    }

    /**
     * Change custom model data of the item
     * @param customModelData new value
     * @return this instance
     */
    @NotNull
    public CustomItem setCustomModelData(int customModelData) {
        if (registered) throw new ChangeRegisteredItem();
        this.customModelData = customModelData;
        return this;
    }

    /**
     * Change item color, only used if item is leather item
     * @param color new value
     * @return this instance
     */
    @NotNull
    public CustomItem setColor(int color) {
        if (registered) throw new ChangeRegisteredItem();
        this.color = color;
        return this;
    }

    /**
     * Change item display name
     * @param name new value
     * @return this instance
     */
    @NotNull
    public CustomItem setName(@NotNull String name) {
        if (registered) throw new ChangeRegisteredItem();
        this.name = name;
        return this;
    }

    /**
     * Change item lore
     * @param lore new value
     * @return this instance
     */
    @NotNull
    public CustomItem setLore(@Nullable String... lore) {
        if (registered) throw new ChangeRegisteredItem();
        this.lore = lore == null ? new String[0] : lore;
        return this;
    }

    /**
     * Change item enchantability state
     * @param enchantable new value
     * @return this instance
     */
    @NotNull
    public CustomItem setEnchantable(boolean enchantable)  {
        if (registered) throw new ChangeRegisteredItem();
        this.enchantable = enchantable;
        return this;
    }

    /**
     * Change item disenchantability state
     * @param disenchantable new value
     * @return this instance
     */
    @NotNull
    public CustomItem setDisenchantable(boolean disenchantable) {
        if (registered) throw new ChangeRegisteredItem();
        this.disenchantable = disenchantable;
        return this;
    }

    /**
     * Change item rarity
     * @param rarity new value
     * @return this instance
     */
    @NotNull
    public CustomItem setRarity(@NotNull Rarity rarity) {
        if (registered) throw new ChangeRegisteredItem();
        this.rarity = rarity;
        return this;
    }

    /**
     * Change item recipes
     * @param recipe new value
     * @return this instance
     */
    @NotNull
    public CustomItem setRecipe(@Nullable Recipe... recipe) {
        if (registered) throw new ChangeRegisteredItem();
        this.recipes = recipe == null ? new Recipe[0] : recipe;
        return this;
    }

    /**
     * Change item category
     * @param category new value
     * @return this instance
     */
    @NotNull
    public CustomItem setCategory(@NotNull Categories category) {
        this.category = category;
        return this;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public int getColor() {
        return color;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @NotNull
    public String[] getLore() {
        return lore;
    }

    public boolean isEnchantable() {
        return enchantable;
    }

    public boolean isDisenchantable() {
        return disenchantable;
    }

    @NotNull
    public Rarity getRarity() {
        return rarity;
    }

    @NotNull
    public Recipe[] getRecipes() {
        return recipes;
    }

    @Nullable
    public Categories getCategory() {
        return category;
    }

    public boolean isRegistered() {
        return registered;
    }

    /**
     * Valid all parameters of the item and then register it in {@link Registry} <br/>
     * Registered items can't be edited
     */
    public void register() {
        if (registered) throw new ChangeRegisteredItem();

        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]+!");
        if (registry.itemIdUsed(id)) throw new InvalidBuildException("Id already used!");
        if (rarity == null) throw new InvalidBuildException("Rarity cannot be null!");

        // Call this to subclasses
        registerOthers();

        registered = true;
        registry.registerItem(this);
    }

    /**
     * Get the object type like object, weapon, armor..., to override in subclasses <br/>
     * Principally used to build lore {@link CustomItem#buildLore()}
     * @return item type
     */
    @NotNull
    protected String getType() {
        return "OBJET";
    }

    /**
     * Method only used in subclasses to do actions and valid more parameters in it
     */
    protected void registerOthers() {

    }

    /**
     * Thrown when we use a Setter of this class on an item which is {@link CustomItem#registered}
     */
    protected static class ChangeRegisteredItem extends RuntimeException {
        public ChangeRegisteredItem() {
            super("You tried to edit a registered item!");
        }
    }

    /**
     * Thrown when an error occurred during item registration in method {@link CustomItem#register()}
     */
    protected static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(@NotNull String cause) {
            super("Invalid custom item build: " + cause);
        }
    }
}
