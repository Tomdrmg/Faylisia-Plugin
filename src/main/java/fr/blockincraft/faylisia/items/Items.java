package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomLivingEntity;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.specificitems.EnchantmentLacrymaItem;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.items.recipes.CraftingRecipe;
import fr.blockincraft.faylisia.items.specificitems.StatsLacrymaItem;
import fr.blockincraft.faylisia.items.tools.ToolItem;
import fr.blockincraft.faylisia.items.tools.ToolType;
import fr.blockincraft.faylisia.items.weapons.WeaponAbilityItem;
import fr.blockincraft.faylisia.items.weapons.WeaponItem;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.AbilitiesUtils;
import fr.blockincraft.faylisia.utils.HandlersUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    public static final WeaponAbilityItem boomItem = (WeaponAbilityItem) new WeaponAbilityItem(Material.IRON_SWORD, "boom_item")
            .setAbilityName("Boom")
            .setAbilityDesc("&7Inflige &c10x &7les dégats aux monstres", "&7Dans un rayon de 10 blocs")
            .setAbility((player, clickedBlock, hand) -> {
                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                long damage = (long) (customPlayer.getDamage(false) * 10);

                AbilitiesUtils.getLivingEntitiesInRadius(player.getLocation(), 10.0).forEach(customEntity -> {
                    long damageIn = HandlersUtils.getValueWithHandlers(customPlayer, "onDamage", damage, long.class, new HandlersUtils.Parameter[]{
                            new HandlersUtils.Parameter(player, Player.class),
                            new HandlersUtils.Parameter(customEntity, CustomEntity.class),
                            new HandlersUtils.Parameter(DamageType.MAGIC_DAMAGE, DamageType.class)
                    });

                    PlayerUtils.spawnDamageIndicator(damageIn, false, player, customEntity.getEntity().getLocation());
                    customEntity.takeDamage(damageIn, player);
                });

                return false;
            })
            .setCooldown(5)
            .setUseCost(20)
            .setDamage(20)
            .setStat(Stats.MAGICAL_RESERVE, 100)
            .setName("Boom Item")
            .setRarity(Rarity.COSMIC)
            .setCategory(Categories.COOL_DIAMOND);
    public static final WeaponAbilityItem dagger = (WeaponAbilityItem) new WeaponAbilityItem(Material.IRON_SWORD, "dagger")
            .setAbilityName("Rush")
            .setAbilityDesc("&7Se téléporte a 3 cibles dans un rayon", "&7de 50 blocs et leur assène deux", "&7coûts critiques")
            .setAbility((player, clickedBlock, hand) -> {
                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                long damage = (long) customPlayer.getDamage(true);

                List<CustomLivingEntity> entities = new ArrayList<>(AbilitiesUtils.getLivingEntitiesInRadius(player.getLocation(), 50.0));
                Collections.shuffle(entities);

                if (entities.size() == 0) return true;

                for (int i = 0; i < 3 && i < entities.size(); i++) {
                    int finalI = i;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                        long damageIn = HandlersUtils.getValueWithHandlers(customPlayer, "onDamage", damage, long.class, new HandlersUtils.Parameter[]{
                                new HandlersUtils.Parameter(player, Player.class),
                                new HandlersUtils.Parameter(entities.get(finalI), CustomEntity.class),
                                new HandlersUtils.Parameter(DamageType.MELEE_DAMAGE, DamageType.class)
                        });

                        player.teleport(entities.get(finalI).getEntity().getLocation());
                        for (int j = 0; j < 2; j++) {
                            PlayerUtils.spawnDamageIndicator(damageIn, true, player, entities.get(finalI).getEntity().getLocation());
                            entities.get(finalI).takeDamage(damageIn, player);
                        }
                    }, i * 3L);
                }

                return false;
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
    public static final EnchantmentLacrymaItem enchantmentLacryma = (EnchantmentLacrymaItem) new EnchantmentLacrymaItem(Material.ENCHANTED_BOOK, "enchantment_lacryma")
            .setName("Lacryma D'enchantement")
            .setLore("&bUne lacryma magique qui peut", "&bstocker un ou plusieurs", "&benchantements")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);
    public static final StatsLacrymaItem smallEthernanosLacryma = (StatsLacrymaItem) new StatsLacrymaItem(Material.LAVA_BUCKET, "small_ethernanos_lacryma")
            .setStat(Stats.MAGICAL_RESERVE, 100.0)
            .setName("Petite Lacryma D'éthernanos")
            .setLore("&bUne lacryma magique qui vous", "&bpermet d'augmenter votre", "&bréserve d'éthernanos")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.EPIC);
    public static final StatsLacrymaItem mediumEthernanosLacryma = (StatsLacrymaItem) new StatsLacrymaItem(Material.LAVA_BUCKET, "medium_ethernanos_lacryma")
            .setStat(Stats.MAGICAL_RESERVE, 1000.0)
            .setName("Lacryma D'éthernanos Moyenne")
            .setLore("&bUne lacryma magique qui vous", "&bpermet d'augmenter votre", "&bréserve d'éthernanos")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.EPIC);
    public static final StatsLacrymaItem bigEthernanosLacryma = (StatsLacrymaItem) new StatsLacrymaItem(Material.LAVA_BUCKET, "big_ethernanos_lacryma")
            .setStat(Stats.MAGICAL_RESERVE, 10000.0)
            .setName("Grande Lacryma D'éthernanos")
            .setLore("&bUne lacryma magique qui vous", "&bpermet d'augmenter votre", "&bréserve d'éthernanos")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.LEGENDARY);
    public static final WeaponAbilityItem piouPiouLaser = (WeaponAbilityItem) new WeaponAbilityItem(Material.DIAMOND_SHOVEL, "piou_piou_laser")
            .setAbilityName("Laser")
            .setUseCost(50)
            .setAbilityDesc("&7Tire un laser qui inflige", "&7des dégâts au entités")
            .setAbility((player, clickedBlock, hand) -> {
                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                long damage = (long) customPlayer.getDamage(false);
                double distance = 20;

                Location viewLocation = player.getLocation().getDirection().toLocation(player.getWorld());
                Set<CustomLivingEntity> entities = new HashSet<>();

                for (double i = 0; i < distance; i += 0.5) {
                    Location pointLocation = viewLocation.clone().multiply(i).add(player.getLocation()).add(0, 1.6, 0);
                    double x = pointLocation.getX();
                    double y = pointLocation.getY();
                    double z = pointLocation.getZ();

                    player.spawnParticle(Particle.REDSTONE, pointLocation, 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(0x4287f5), 2F));
                    BoundingBox collision = new BoundingBox(x - 0.25, y - 0.25, z - 0.25, x + 0.25, y + 0.25, z + 0.25);

                    registry.getEntities().forEach(customEntity -> {
                        if (customEntity instanceof CustomLivingEntity && customEntity.getEntity().isValid() && customEntity.getEntity().getBoundingBox().overlaps(collision)) {
                            entities.add((CustomLivingEntity) customEntity);
                        }
                    });
                }

                for (CustomLivingEntity entity : entities) {
                    long damageIn = HandlersUtils.getValueWithHandlers(customPlayer, "onDamage", damage, long.class, new HandlersUtils.Parameter[]{
                            new HandlersUtils.Parameter(player, Player.class),
                            new HandlersUtils.Parameter(entity, CustomEntity.class),
                            new HandlersUtils.Parameter(DamageType.MAGIC_DAMAGE, DamageType.class)
                    });

                    if (entity.getEntity() instanceof LivingEntity living) {
                        living.damage(0);
                    }
                    PlayerUtils.spawnDamageIndicator(damageIn, false, player, entity.getEntity().getLocation());
                    entity.takeDamage(damageIn, player);
                }

                return false;
            })
            .setDamage(520)
            .setStat(Stats.STRENGTH, 165)
            .setName("Piou Piou Laser")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.LEGENDARY);
    public static final WeaponAbilityItem aspectOfTheEnd = (WeaponAbilityItem) new WeaponAbilityItem(Material.GOLDEN_SWORD, "aspect_of_the_end")
            .setAbilityName("Téléportation instantanée")
            .setUseCost(30)
            .setAbilityDesc("&7Vous téléporte à 12 blocs")
            .setAbility((player, clickedBlock, hand) -> {
                BlockIterator iterator = new BlockIterator(player, 12);

                Block previous = null;
                while (iterator.hasNext()) {
                    Block block = iterator.next();

                    if (block.getType() != Material.AIR || block.getRelative(BlockFace.UP).getType() != Material.AIR) {
                        if (previous == null) return true;
                        Location location = previous.getLocation().clone();
                        location.setDirection(player.getLocation().getDirection());
                        player.teleport(location.clone().add(0.5, 0, 0.5));
                        break;
                    } else if (!iterator.hasNext()) {
                        Location location = block.getLocation().clone();
                        location.setDirection(player.getLocation().getDirection());
                        player.teleport(location.clone().add(0.5, 0, 0.5));
                    }

                    previous = block;
                }

                return false;
            })
            .setDamage(50)
            .setName("Aspect of the end")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);
    public static final ToolItem testTool1 = (ToolItem) new ToolItem(Material.DIAMOND_AXE, "test_tool_1")
            .setToolTypes(new ToolType[]{ToolType.FORAGING, ToolType.MINING, ToolType.FARMING})
            .setBreakingLevel(1)
            .setName("Outil 1")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);
    public static final ToolItem testTool2 = (ToolItem) new ToolItem(Material.GOLDEN_AXE, "test_tool_2")
            .setToolTypes(new ToolType[]{ToolType.FORAGING})
            .setBreakingLevel(0)
            .setName("Outil 2")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);
    public static final ToolItem testTool3 = (ToolItem) new ToolItem(Material.IRON_AXE, "test_tool_3")
            .setToolTypes(new ToolType[]{})
            .setBreakingLevel(1)
            .setName("Outil 3")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);
    public static final ToolItem testTool4 = (ToolItem) new ToolItem(Material.STONE_AXE, "test_tool_4")
            .setToolTypes(new ToolType[]{})
            .setBreakingLevel(0)
            .setName("Outil 4")
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

        boomItem.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamondBlock, 1),
                new CustomItemStack(coolDiamondSword, 1),
                CraftingRecipe.Direction.VERTICAL
        ));
        dagger.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamondSword, 1),
                new CustomItemStack(coolDiamondBlock, 1),
                CraftingRecipe.Direction.VERTICAL
        ));
        enchantmentLacryma.setRecipe(new CraftingRecipe(1,
                null, new CustomItemStack(coolDiamond, 1), null,
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                null, new CustomItemStack(coolDiamond, 1), null));
        smallEthernanosLacryma.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1),
                null, null, null,
                new CustomItemStack(coolDiamond, 1), null, new CustomItemStack(coolDiamond, 1)
        ));
        mediumEthernanosLacryma.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(smallEthernanosLacryma, 2), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamond, 1)
        ));
        bigEthernanosLacryma.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(coolDiamondBlock, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamondBlock, 1),
                new CustomItemStack(coolDiamond, 1), new CustomItemStack(mediumEthernanosLacryma, 2), new CustomItemStack(coolDiamond, 1),
                new CustomItemStack(coolDiamondBlock, 1), new CustomItemStack(coolDiamond, 1), new CustomItemStack(coolDiamondBlock, 1)
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

        boomItem.register();
        dagger.register();

        enchantmentLacryma.register();
        smallEthernanosLacryma.register();
        mediumEthernanosLacryma.register();
        bigEthernanosLacryma.register();

        piouPiouLaser.register();
        aspectOfTheEnd.register();

        testTool1.register();
        testTool2.register();
        testTool3.register();
        testTool4.register();
    }
}
