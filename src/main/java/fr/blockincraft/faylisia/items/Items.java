package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.recipes.CraftingRecipe;
import fr.blockincraft.faylisia.items.weapons.WeaponItem;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This class contain all items, armor set and recipes
 */
public class Items {
    public static final ArmorSet coolDiamondSet = new ArmorSet("cool_diamond_set")
            .setBonus(
                    new ArmorSet.Bonus(
                            "Berserk", 2, new Handlers() {
                        @Override
                        public double calculateItemStat(Player player, CustomItem customItem, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return inArmorSlot && stat == Stats.STRENGTH ? value * 1.5 : value;
                        }

                        @Override
                        public double getDefaultStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return inArmorSlot && stat == Stats.STRENGTH ? value * 1.5 : value;
                        }
                    }, "&7Vous gagnez &c+50% &7de force", "&7quand il est actif."
                    ),
                    new ArmorSet.Bonus(
                            "Divine protection", 3, new Handlers() {
                        @Override
                        public double getStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return stat == Stats.DEFENSE && inArmorSlot ? value + 100 : value;
                        }
                    }, "&7Vous gagnez &c+100 &7de défense", "&7quand il est actif."
                    ),
                    new ArmorSet.Bonus(
                            "Hulk Power", 4, new Handlers() {
                        @Override
                        public long getDamage(Player player, long damage, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return inArmorSlot ? damage * 2 : damage;
                        }
                    }, "&7Vos dégats sont augmentés", "&7de &c100%&7."
                    )
            );

    // Register armor set here
    static {
        coolDiamondSet.register();
    }

    public static final CustomItem coolDiamond = new CustomItem(Material.DIAMOND, "cool_diamond")
            .setName("Cool Diamond")
            .setLore("&eA legendary diamond from", "&ethe coolest ore!")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.COSMIC);
    public static final CustomItem coolDiamondBlock = new CustomItem(Material.DIAMOND_BLOCK, "cool_diamond_block")
            .setName("Cool Diamond Block")
            .setLore("&eA legendary diamond block", "&erefined block from the", "&ecoolest ore!")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.NOTHINGNESS);
    public static final WeaponItem coolDiamondSword = (WeaponItem) new WeaponItem(Material.DIAMOND_SWORD, "cool_diamond_sword")
            .setDamage(50)
            .setName("Cool Diamond Sword")
            .setLore("&eA beautiful sword forged", "&ewith the coolest ore!")
            .setEnchantable(true)
            .setDisenchantable(true)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.MYSTICAL);
    public static final ArmorItem coolDiamondHelmet = (ArmorItem) new ArmorItem(Material.LEATHER_HELMET, "cool_diamond_helmet")
            .setArmorSet(coolDiamondSet)
            .setStat(Stats.STRENGTH, 7.5)
            .setStat(Stats.SPEED, 50)
            .setStat(Stats.HEALTH, 50)
            .setName("Cool Diamond Helmet")
            .setLore("&eA unique helmet made with", "&ethe coolest ore!")
            .setColor(0xa103fc)
            .setEnchantable(true)
            .setDisenchantable(true)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.DEUS);
    public static final ArmorItem coolDiamondChestplate = (ArmorItem) new ArmorItem(Material.LEATHER_CHESTPLATE, "cool_diamond_chestplate")
            .setArmorSet(coolDiamondSet)
            .setStat(Stats.STRENGTH, 7.5)
            .setStat(Stats.SPEED, 50)
            .setStat(Stats.HEALTH, 50)
            .setName("Cool Diamond Chestplate")
            .setLore("&eA unique chestplate made with", "&ethe coolest ore!")
            .setColor(0xa103fc)
            .setEnchantable(true)
            .setDisenchantable(true)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.DEUS);
    public static final ArmorItem coolDiamondLeggings = (ArmorItem) new ArmorItem(Material.LEATHER_LEGGINGS, "cool_diamond_leggings")
            .setArmorSet(coolDiamondSet)
            .setStat(Stats.STRENGTH, 7.5)
            .setStat(Stats.SPEED, 50)
            .setStat(Stats.HEALTH, 50)
            .setName("Cool Diamond Leggings")
            .setLore("&eUnique leggings made with", "&ethe coolest ore!")
            .setColor(0xa103fc)
            .setEnchantable(true)
            .setDisenchantable(true)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.DEUS);
    public static final ArmorItem coolDiamondBoots = (ArmorItem) new ArmorItem(Material.LEATHER_BOOTS, "cool_diamond_boots")
            .setArmorSet(coolDiamondSet)
            .setStat(Stats.STRENGTH, 7.5)
            .setStat(Stats.SPEED, 50)
            .setStat(Stats.HEALTH, 50)
            .setName("Cool Diamond Boots")
            .setLore("&eUnique boots made with", "&ethe coolest ore!")
            .setColor(0xa103fc)
            .setEnchantable(true)
            .setDisenchantable(true)
            .setCategory(Categories.COOL_DIAMOND)
            .setRarity(Rarity.DEUS);

    // Set recipes here
    static {
        coolDiamond.setRecipe(new CraftingRecipe(9, new CustomItemStack(coolDiamondBlock, 1)));
        coolDiamondBlock.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1)
        ));
        coolDiamondSword.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 4),
                new CustomItemStack(coolDiamond, 4),
                new CustomItemStack(coolDiamondBlock, 1),
                CraftingRecipe.Direction.VERTICAL
        ));
        coolDiamondHelmet.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                CraftingRecipe.Direction.HORIZONTAL
        ));
        coolDiamondChestplate.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1)
        ));
        coolDiamondLeggings.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1)
        ));
        coolDiamondBoots.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                CraftingRecipe.Direction.HORIZONTAL
        ));
    }

    // Register items here
    static {
        coolDiamond.register();
        coolDiamondBlock.register();

        coolDiamondSword.register();

        coolDiamondHelmet.register();
        coolDiamondChestplate.register();
        coolDiamondLeggings.register();
        coolDiamondBoots.register();
    }
}
