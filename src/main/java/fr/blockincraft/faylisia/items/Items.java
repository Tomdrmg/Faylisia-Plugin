package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.enchantment.EnchantmentLacrymaItem;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.recipes.CraftingRecipe;
import fr.blockincraft.faylisia.items.weapons.WeaponAbilityItem;
import fr.blockincraft.faylisia.items.weapons.WeaponItem;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.AbilitiesUtils;
import fr.blockincraft.faylisia.utils.HandlersUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * This class contain all items, armor set and recipes
 */
public class Items {
    private static final Registry registry = Faylisia.getInstance().getRegistry();



    public static final ArmorSet coolDiamondSet = new ArmorSet("cool_diamond_set")
            .setBonus(
                    new ArmorSet.Bonus(
                            "Berserk", 2, new Handlers() {
                        @Override
                        public double calculateItemStat(@NotNull Player player, @NotNull CustomItem customItem, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return inArmorSlot && stat == Stats.STRENGTH ? value * 1.5 : value;
                        }

                        @Override
                        public double getDefaultStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return inArmorSlot && stat == Stats.STRENGTH ? value * 1.5 : value;
                        }
                    }, "&7Vous gagnez &c+50% &7de force", "&7quand il est actif."
                    ),
                    new ArmorSet.Bonus(
                            "Divine protection", 3, new Handlers() {
                        @Override
                        public double getStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                            onHandlerCall();
                            return stat == Stats.DEFENSE && inArmorSlot ? value + 100 : value;
                        }
                    }, "&7Vous gagnez &c+100 &7de défense", "&7quand il est actif."
                    ),
                    new ArmorSet.Bonus(
                            "Hulk Power", 4, new Handlers() {
                        @Override
                        public long getDamage(@NotNull Player player, long damage, boolean inHand, boolean inArmorSlot) {
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
    public static final WeaponAbilityItem testItem = (WeaponAbilityItem) new WeaponAbilityItem(Material.IRON_SWORD, "test_item")
            .setAbilityName("Boom")
            .setAbilityDesc("&7Inflige &c10x &7les dégats aux monstres", "&7Dans un rayon de 10 blocs")
            .setAbility((player, clickedBlock, hand) -> {
                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                long damage = (long) (customPlayer.getDamage(false) * 10);

                AbilitiesUtils.getEntitiesInRadius(player.getLocation(), 10.0).forEach(customEntity -> {
                    long damageIn = HandlersUtils.getValueWithHandlers(customPlayer, "onDamage", damage, long.class, new HandlersUtils.Parameter[]{
                            new HandlersUtils.Parameter(player, Player.class),
                            new HandlersUtils.Parameter(customEntity, CustomEntity.class),
                            new HandlersUtils.Parameter(DamageType.MAGIC_DAMAGE, DamageType.class)
                    });

                    PlayerUtils.spawnDamageIndicator(damage, false, player, customEntity.getEntity().getLocation());
                    customEntity.takeDamage(damage, player);
                });
            })
            .setCooldown(5)
            .setUseCost(20)
            .setDamage(20)
            .setStat(Stats.MAGICAL_RESERVE, 100)
            .setName("Test Item")
            .setRarity(Rarity.COSMIC)
            .setCategory(Categories.COOL_DIAMOND);
    public static final WeaponAbilityItem dagger = (WeaponAbilityItem) new WeaponAbilityItem(Material.IRON_SWORD, "dagger")
            .setAbilityName("Rush")
            .setAbilityDesc("&7Se téléporte a 3 cibles dans un rayon", "&7de 50 blocs et leur assène deux", "&7coûts critiques")
            .setAbility((player, clickedBlock, hand) -> {
                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                long damage = (long) customPlayer.getDamage(true);

                List<CustomEntity> entities = AbilitiesUtils.getEntitiesInRadius(player.getLocation(), 50.0);
                Collections.shuffle(entities);

                for (int i = 0; i < 3 && i < entities.size(); i++) {
                    int finalI = i;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                        long damageIn = HandlersUtils.getValueWithHandlers(customPlayer, "onDamage", damage, long.class, new HandlersUtils.Parameter[]{
                                new HandlersUtils.Parameter(player, Player.class),
                                new HandlersUtils.Parameter(entities.get(finalI), CustomEntity.class),
                                new HandlersUtils.Parameter(DamageType.MELEE_DAMAGE, DamageType.class)
                        });

                        player.teleport(entities.get(finalI).getEntity().getLocation());
                        PlayerUtils.spawnDamageIndicator(damageIn, true, player, entities.get(finalI).getEntity().getLocation());
                        entities.get(finalI).takeDamage(damageIn, player);
                    }, i * 3L);
                }
            })
            .setCooldown(30)
            .setUseCost(100)
            .setDamage(100)
            .setStat(Stats.MAGICAL_RESERVE, 200)
            .setStat(Stats.STRENGTH, 50)
            .setStat(Stats.CRITICAL_DAMAGE, 50)
            .setStat(Stats.CRITICAL_CHANCE, 30)
            .setName("Dague")
            .setRarity(Rarity.COSMIC)
            .setCategory(Categories.COOL_DIAMOND);
    public static final EnchantmentLacrymaItem ENCHANTMENT_LACRYMA_ITEM = (EnchantmentLacrymaItem) new EnchantmentLacrymaItem(Material.ENCHANTED_BOOK, "enchantment_lacryma")
            .setName("Lacryma D'enchantement")
            .setLore("&bUne lacryma magique qui peut", "&bstocker un ou plusieurs", "&benchantements")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);

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

        testItem.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamondBlock, 1),
                new CustomItemStack(coolDiamondSword, 1),
                CraftingRecipe.Direction.VERTICAL
        ));
        dagger.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamondSword, 1),
                new CustomItemStack(coolDiamondBlock, 1),
                CraftingRecipe.Direction.VERTICAL
        ));

        ENCHANTMENT_LACRYMA_ITEM.setRecipe(new CraftingRecipe(1,
                null, new CustomItemStack(coolDiamond, 1), null,
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                null, new CustomItemStack(coolDiamond, 1), null));
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

        testItem.register();
        dagger.register();

        ENCHANTMENT_LACRYMA_ITEM.register();
    }
}
