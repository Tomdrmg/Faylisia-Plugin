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
import fr.blockincraft.faylisia.items.tools.StatsToolItem;
import fr.blockincraft.faylisia.items.tools.ToolItem;
import fr.blockincraft.faylisia.items.tools.ToolType;
import fr.blockincraft.faylisia.items.weapons.WeaponAbilityItem;
import fr.blockincraft.faylisia.items.weapons.WeaponItem;
import fr.blockincraft.faylisia.magic.Spell;
import fr.blockincraft.faylisia.magic.SpellTypes;
import fr.blockincraft.faylisia.magic.wrapper.SpiralExplosionSpellWrapper;
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
import org.jetbrains.annotations.Nullable;

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
                        public double calculateItemStat(@NotNull Player player, @NotNull CustomItemStack customItemStack, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot, @Nullable CustomItemStack thisItemStack) {
                            onHandlerCall();
                            return inArmorSlot && stat == Stats.STRENGTH ? value * 1.5 : value;
                        }

                        @Override
                        public double getDefaultStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot, @Nullable CustomItemStack thisItemStack) {
                            onHandlerCall();
                            return inArmorSlot && stat == Stats.STRENGTH ? value * 1.5 : value;
                        }
                    }, "&7Vous gagnez &c+50% &7de force", "&7quand il est actif."
                    ),
                    new ArmorSet.Bonus(
                            "Divine protection", 3, new Handlers() {
                        @Override
                        public double getStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot, @Nullable CustomItemStack thisItemStack) {
                            onHandlerCall();
                            return stat == Stats.DEFENSE && inArmorSlot ? value + 100 : value;
                        }
                    }, "&7Vous gagnez &c+100 &7de défense", "&7quand il est actif."
                    ),
                    new ArmorSet.Bonus(
                            "Hulk Power", 4, new Handlers() {
                        @Override
                        public double getDamage(@NotNull Player player, double value, boolean inHand, boolean inArmorSlot, @Nullable CustomItemStack thisItemStack) {
                            onHandlerCall();
                            return inArmorSlot ? value * 2 : value;
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
            .setCategory(Categories.TEST)
            .setRarity(Rarity.COSMIC);
    public static final CustomItem coolDiamondBlock = new CustomItem(Material.DIAMOND_BLOCK, "cool_diamond_block")
            .setName("Cool Diamond Block")
            .setLore("&eA legendary diamond block", "&erefined block from the", "&ecoolest ore!")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setCategory(Categories.TEST)
            .setRarity(Rarity.NOTHINGNESS);
    public static final WeaponItem coolDiamondSword = (WeaponItem) new WeaponItem(Material.DIAMOND_SWORD, "cool_diamond_sword")
            .setStat(Stats.POWER, 50)
            .setName("Cool Diamond Sword")
            .setLore("&eA beautiful sword forged", "&ewith the coolest ore!")
            .setEnchantable(true)
            .setDisenchantable(true)
            .setCategory(Categories.TEST)
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
            .setCategory(Categories.TEST)
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
            .setCategory(Categories.TEST)
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
            .setCategory(Categories.TEST)
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
            .setCategory(Categories.TEST)
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
            .setStat(Stats.POWER, 20)
            .setStat(Stats.MAGICAL_RESERVE, 100)
            .setName("Boom Item")
            .setRarity(Rarity.COSMIC);
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
            .setStat(Stats.POWER, 100)
            .setStat(Stats.MAGICAL_RESERVE, 200)
            .setStat(Stats.STRENGTH, 50)
            .setStat(Stats.CRITICAL_DAMAGE, 50)
            .setStat(Stats.CRITICAL_CHANCE, 30)
            .setName("Dague")
            .setRarity(Rarity.COSMIC);
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
            .setStat(Stats.POWER, 520)
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
            .setStat(Stats.POWER, 50)
            .setName("Aspect of the end")
            .setEnchantable(false)
            .setDisenchantable(false)
            .setRarity(Rarity.RARE);
    public static final WeaponAbilityItem spellItem = (WeaponAbilityItem) new WeaponAbilityItem(Material.NETHERITE_SWORD, "spell_item")
            .setAbility((player, clickedBlock, hand) -> {
                Spell spell = new SpiralExplosionSpellWrapper(player, 15, 0x7bf542, 0xf54242);
                spell.start();
                return false;
            })
            .setCooldown((int) SpellTypes.spiralExplosion.getCooldown())
            .setAbilityName("Explosion spiral piou piou")
            .setAbilityDesc("&7Fait une spiral de couleur qui termine", "&7avec une explosion! boum! boum!")
            .setStat(Stats.POWER, 100)
            .setEnchantable(true)
            .setDisenchantable(true)
            .setRarity(Rarity.NOTHINGNESS)
            .setName("Spirale Boom");

    public static final ArmorItem SPEED_BOOTS = (ArmorItem) new ArmorItem(Material.DIAMOND_BOOTS, "speed_boots")
            .setStat(Stats.SPEED, 1000)
            .setName("Bottes de speed")
            .setRarity(Rarity.NOTHINGNESS);


    public static final CustomItem BARK = new CustomItem(Material.OAK_PLANKS, "bark")
            .setName("Écorce")
            .setRarity(Rarity.COMMON)
            .setCustomModelData(1)
            .setCategory(Categories.RESOURCES);
    public static final CustomItem WOODEN_TOOL_HANDLE = new CustomItem(Material.STICK, "wooden_tool_handle")
            .setName("Manche d'outils en bois")
            .setRarity(Rarity.COMMON)
            .setCategory(Categories.TOOLS)
            .setCustomModelData(1);
    public static final CustomItem WOODEN_TOOL_BINDING = new CustomItem(Material.STICK, "wooden_tool_binding")
            .setName("Reliure d'outils en bois")
            .setRarity(Rarity.COMMON)
            .setCategory(Categories.TOOLS)
            .setCustomModelData(1);
    public static final StatsToolItem WOODEN_AXE = (StatsToolItem) new StatsToolItem(Material.WOODEN_AXE, "wooden_axe")
            .setStat(Stats.MINING_SPEED, 50)
            .setToolTypes(new ToolType[]{ToolType.FORAGING})
            .setBreakingLevel(1)
            .setName("Hache en bois")
            .setRarity(Rarity.COMMON)
            .setCategory(Categories.TOOLS)
            .setCustomModelData(1);
    public static final StatsToolItem WOODEN_PICKAXE = (StatsToolItem) new StatsToolItem(Material.WOODEN_PICKAXE, "wooden_pickaxe")
            .setStat(Stats.MINING_SPEED, 50)
            .setToolTypes(new ToolType[]{ToolType.MINING})
            .setBreakingLevel(1)
            .setName("Pioche en bois")
            .setRarity(Rarity.COMMON)
            .setCategory(Categories.TOOLS)
            .setCustomModelData(1);
    public static final EnchantmentLacrymaItem ENCHANTMENT_LACRYMA = (EnchantmentLacrymaItem) new EnchantmentLacrymaItem(Material.ENCHANTED_BOOK, "enchantment_lacryma")
            .setName("Lacryma D'enchantement")
            .setLore("&bUne lacryma magique qui peut", "&bstocker un ou plusieurs", "&benchantements")
            .setRarity(Rarity.RARE);

    // Set recipes here
    static {
        WOODEN_TOOL_HANDLE.setRecipe(new CraftingRecipe(2,
                new CustomItemStack(BARK, 1), null, new CustomItemStack(BARK, 1),
                null, new CustomItemStack(BARK, 1), null,
                new CustomItemStack(BARK, 1), null, new CustomItemStack(BARK, 1)
        ));
        WOODEN_TOOL_BINDING.setRecipe(new CraftingRecipe(2,
                new CustomItemStack(BARK, 1), null, new CustomItemStack(BARK, 1),
                null, new CustomItemStack(BARK, 1), null,
                new CustomItemStack(BARK, 1), null, new CustomItemStack(BARK, 1)
        ));
        WOODEN_AXE.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(BARK, 1), new CustomItemStack(BARK, 1),
                new CustomItemStack(WOODEN_TOOL_BINDING, 1), new CustomItemStack(BARK, 1),
                new CustomItemStack(WOODEN_TOOL_HANDLE, 1), null,
                CraftingRecipe.Direction.VERTICAL
        ));
        WOODEN_PICKAXE.setRecipe(new CraftingRecipe(1,
                new CustomItemStack(BARK, 1), new CustomItemStack(BARK, 1), new CustomItemStack(BARK, 1),
                null, new CustomItemStack(WOODEN_TOOL_BINDING, 1), null,
                null, new CustomItemStack(WOODEN_TOOL_HANDLE, 1), null
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
        aspectOfTheEnd.register();
        piouPiouLaser.register();
        dagger.register();
        bigEthernanosLacryma.register();
        spellItem.register();
        SPEED_BOOTS.register();

        BARK.register();
        WOODEN_TOOL_BINDING.register();
        WOODEN_TOOL_HANDLE.register();
        WOODEN_AXE.register();
        WOODEN_PICKAXE.register();

        ENCHANTMENT_LACRYMA.register();
    }
}
