package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.recipes.Recipe;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A custom item, similar to a {@link Material} but custom
 */
public class CustomItem {
    public static final NamespacedKey idKey = new NamespacedKey(Faylisia.getInstance(), "custom-id");
    private static final Pattern idPattern = Pattern.compile("[a-z\\d_-]+");
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
    public ItemStack getAsItemStack(CustomItemStack customItemStack) {
        if (!registered) return null;

        ItemStack itemStack = new ItemStack(material);

        ItemMeta meta = itemStack.getItemMeta();

        if (meta instanceof LeatherArmorMeta lam && color != -1) {
            lam.setColor(Color.fromRGB(color));
        }

        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);

        List<String> lore = new ArrayList<>(firstLore(customItemStack));
        if (this.lore.length > 0 && lore.size() > 0) lore.add("");
        for (String l : this.lore) {
            lore.add(ColorsUtils.translateAll(l));
        }

        if (this.hasEnchants(customItemStack)) {
            itemStack.setItemMeta(meta);
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            meta = itemStack.getItemMeta();
            if (lore.size() > 0) lore.add("");

            lore.addAll(this.enchantsLore(customItemStack));
        }

        lore.addAll(buildLore(customItemStack));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        meta.setUnbreakable(true);
        meta.setCustomModelData(customModelData <= 0 ? null : customModelData);
        meta.setLore(lore);
        meta.setDisplayName(ColorsUtils.translateAll(rarity.colorChar + "&l" + name));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @NotNull
    protected List<String> enchantsLore(CustomItemStack customItemStack) {
        List<String> lore = new ArrayList<>();

        List<Map.Entry<CustomEnchantments, Integer>> enchants = this.getEnchants(customItemStack).entrySet().stream().sorted(
               (o1, o2) -> o1.getKey().index - o2.getKey().index
        ).toList();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < enchants.size(); i++) {
            CustomEnchantments enchant = enchants.get(i).getKey();
            int level = enchants.get(i).getValue();

            if (i % 2 == 0) {
                sb = new StringBuilder("&7" + enchant.nameDependingOfLevel.getName(level, enchant.name) + " " + TextUtils.intToRoman(level));
                if (i == enchants.size() - 1) {
                    lore.add(ColorsUtils.translateAll(sb.toString()));
                }
            } else {
                sb.append("&7, ").append(enchant.nameDependingOfLevel.getName(level, enchant.name)).append(" ").append(TextUtils.intToRoman(level));
                lore.add(ColorsUtils.translateAll(sb.toString()));
            }
        }

        return lore;
    }

    protected boolean hasEnchants(CustomItemStack customItemStack) {
        return this.enchantable && customItemStack.getEnchantments().size() > 0;
    }

    protected Map<CustomEnchantments, Integer> getEnchants(CustomItemStack customItemStack) {
        if (!hasEnchants(customItemStack)) return new HashMap<>();

        return customItemStack.getEnchantments();
    }

    /**
     * First lore part, this is util for subclasses, this is lore which will be displayed before {@link CustomItem#lore}
     * @return text to display
     */
    @NotNull
    protected List<String> firstLore(CustomItemStack customItemStack) {
        return new ArrayList<>();
    }

    /**
     * Second lore part, this is lore which will be displayed after {@link CustomItem#lore}
     * @return text to display
     */
    @NotNull
    protected List<String> buildLore(CustomItemStack customItemStack) {
        // Get other lore part to display before rarity
        List<String> lore = new ArrayList<>(moreLore(customItemStack));

        lore.add("");
        lore.add(ColorsUtils.translateAll(rarity.magicalChars ?
                rarity.colorChar + "&l&k۞ &r" + rarity.colorChar + "&l" + getType(customItemStack) + " " + rarity.name + " &k۞"
                :
                rarity.colorChar + "&l۞ " + getType(customItemStack) + " " + rarity.name + " ۞"
        ));

        return lore;
    }

    /**
     * Second lore part, this is lore which will be displayed after {@link CustomItem#lore} but before {@link CustomItem#buildLore(CustomItemStack)} <br/>
     * This is util for subclasses
     * @return text to display
     */
    @NotNull
    protected List<String> moreLore(CustomItemStack customItemStack) {
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

    public int getCustomModelData(CustomItemStack customItemStack) {
        return customModelData;
    }

    public int getColor(CustomItemStack customItemStack) {
        return color;
    }

    @Nullable
    public String getName(CustomItemStack customItemStack) {
        return name;
    }

    @NotNull
    public String[] getLore(CustomItemStack customItemStack) {
        return lore;
    }

    public boolean isEnchantable(CustomItemStack customItemStack) {
        return enchantable;
    }

    public boolean isDisenchantable(CustomItemStack customItemStack) {
        return disenchantable;
    }

    @NotNull
    public Rarity getRarity(CustomItemStack customItemStack) {
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
     * Principally used to build lore {@link CustomItem#buildLore(CustomItemStack)}
     * @return item type
     */
    @NotNull
    protected String getType(CustomItemStack customItemStack) {
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
