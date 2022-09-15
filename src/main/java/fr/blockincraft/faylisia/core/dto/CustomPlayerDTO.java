package fr.blockincraft.faylisia.core.dto;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.*;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.event.HandlerItem;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.weapons.DamageItem;
import fr.blockincraft.faylisia.listeners.GameListeners;
import fr.blockincraft.faylisia.player.permission.Ranks;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.player.Classes;
import fr.blockincraft.faylisia.displays.Tab;
import fr.blockincraft.faylisia.utils.HandlersUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

public class CustomPlayerDTO {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final SecureRandom random = new SecureRandom();

    // Stored values
    private final UUID player;
    private Classes classes = Classes.HUMAN;
    private Ranks rank = Ranks.PLAYER;
    private boolean canBreak = false;
    private String name;
    private Long discordUserId;

    // Non stored values
    private final Map<CustomItem, Long> lastUse = new HashMap<>();
    private final Map<Stats, Double> stats = new HashMap<>();
    private long effectiveHealth = 0;
    private long maxEffectiveHealth = 0;
    private long magicalReserve = 0;
    private long damage = 0;

    public CustomPlayerDTO(UUID player) {
        this.player = player;
        this.name = Bukkit.getPlayer(player) == null ? "error" : Bukkit.getPlayer(player).getName();
        refreshStats();
        setEffectiveHealth(maxEffectiveHealth);
        setMagicalReserve((long) getStat(Stats.MAGICAL_RESERVE));
        discordUserId = null;
    }

    public CustomPlayerDTO(CustomPlayer model) {
        this.player = model.getPlayer();
        this.classes = model.getClasses();
        this.rank = model.getRank();
        this.canBreak = model.getCanBreak();
        this.name = model.getName();
        this.discordUserId = model.getDiscordUserId();
    }

    public Handlers[] getMainHandHandler() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return new Handlers[0];

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        CustomItemStack customItemStack = CustomItemStack.fromItemStack(mainHandItem);
        if (customItemStack == null) return new Handlers[0];

        List<Handlers> handlers = new ArrayList<>();

        if (customItemStack.getItem() instanceof HandlerItem handlerItem) {
            handlers.add(handlerItem.getHandlers());
        }

        if (customItemStack.getItem().isEnchantable()) {
            customItemStack.getEnchantments().forEach((enchant, level) -> {
                handlers.add(enchant.handlers.withLevel(level));
            });
        }

        return handlers.toArray(new Handlers[0]);
    }

    public Handlers[] getArmorSetHandlers() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return null;

        PlayerInventory inventory = player.getInventory();

        Map<ArmorSet, Integer> armorSets = new HashMap<>();

        if (registry.getCustomItemByItemStack(inventory.getHelmet()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            armorSets.put(armorSet, 1);
        }

        if (registry.getCustomItemByItemStack(inventory.getChestplate()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            armorSets.put(armorSet, armorSets.containsKey(armorSet) ? armorSets.get(armorSet) + 1 : 1);
        }

        if (registry.getCustomItemByItemStack(inventory.getLeggings()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            armorSets.put(armorSet, armorSets.containsKey(armorSet) ? armorSets.get(armorSet) + 1 : 1);
        }

        if (registry.getCustomItemByItemStack(inventory.getBoots()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            armorSets.put(armorSet, armorSets.containsKey(armorSet) ? armorSets.get(armorSet) + 1 : 1);
        }

        List<Handlers> handlers = new ArrayList<>();

        armorSets.forEach((armorSet, pieces) -> {
            for (ArmorSet.Bonus bonus : armorSet.getBonus()) {
                if (bonus.minimum() <= pieces) {
                    handlers.add(bonus.handlers());
                }
            }
        });

        return handlers.toArray(new Handlers[0]);
    }

    public Handlers[] getArmorSlotHandlers() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return null;

        PlayerInventory inventory = player.getInventory();

        List<Handlers> handlers = new ArrayList<>();

        CustomItemStack helmetItemStack = CustomItemStack.fromItemStack(inventory.getHelmet());
        if (helmetItemStack != null) {
            if (helmetItemStack.getItem().isEnchantable()) {
                helmetItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (helmetItemStack.getItem() instanceof HandlerItem handlerItem) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        CustomItemStack chestplateItemStack = CustomItemStack.fromItemStack(inventory.getChestplate());
        if (chestplateItemStack != null) {
            if (chestplateItemStack.getItem().isEnchantable()) {
                chestplateItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (chestplateItemStack.getItem() instanceof HandlerItem handlerItem) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        CustomItemStack leggingsItemStack = CustomItemStack.fromItemStack(inventory.getLeggings());
        if (leggingsItemStack != null) {
            if (leggingsItemStack.getItem().isEnchantable()) {
                leggingsItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (leggingsItemStack.getItem() instanceof HandlerItem handlerItem) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        CustomItemStack bootsItemStack = CustomItemStack.fromItemStack(inventory.getBoots());
        if (bootsItemStack != null) {
            if (bootsItemStack.getItem().isEnchantable()) {
                bootsItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (bootsItemStack.getItem() instanceof HandlerItem handlerItem) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        return handlers.toArray(new Handlers[0]);
    }

    public Handlers[] getOthersHandlers() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return null;

        PlayerInventory inventory = player.getInventory();

        List<Handlers> handlers = new ArrayList<>();

        ItemStack mainHandItem = inventory.getItemInMainHand();

        CustomItemStack offHandItemStack = CustomItemStack.fromItemStack(inventory.getItemInOffHand());
        if (offHandItemStack != null) {
            if (offHandItemStack.getItem().isEnchantable()) {
                offHandItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (offHandItemStack.getItem() instanceof HandlerItem handlerItem) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        for (ItemStack itemStack : inventory.getStorageContents()) {
            CustomItemStack customItemStack = CustomItemStack.fromItemStack(itemStack);
            if (itemStack != mainHandItem && customItemStack != null) {
                if (customItemStack.getItem().isEnchantable()) {
                    customItemStack.getEnchantments().forEach((enchant, level) -> {
                        handlers.add(enchant.handlers.withLevel(level));
                    });
                }

                if (customItemStack.getItem() instanceof HandlerItem handlerItem) {
                    handlers.add(handlerItem.getHandlers());
                }
            }
        }

        return handlers.toArray(new Handlers[0]);
    }

    public void refreshStats() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return;

        Map<Stats, Double> stats = new HashMap<>();
        long damage = 0;

        // Calculate stats
        for (Stats stat : Stats.values()) {
            double defaultValue = stat.defaultValue;
            defaultValue = HandlersUtils.getValueWithHandlers(this, "getDefaultStat", defaultValue, double.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class),
                    new HandlersUtils.Parameter(stat, Stats.class)
            });
            stats.put(stat, defaultValue);
        }

        damage = Stats.handDamage;
        damage = HandlersUtils.getValueWithHandlers(this, "calculateHandRawDamage", damage, long.class, new HandlersUtils.Parameter[]{
                new HandlersUtils.Parameter(player, Player.class)
        });

        PlayerInventory inventory = player.getInventory();

        ItemStack mainHandItem = inventory.getItemInMainHand();
        if (registry.getCustomItemByItemStack(mainHandItem) instanceof DamageItem damageItem) {
            CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);

            long itemDamage = damageItem.getDamage();
            itemDamage = HandlersUtils.getValueWithHandlers(this, "calculateItemRawDamage", itemDamage, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class),
                    new HandlersUtils.Parameter(customItem, CustomItem.class)
            });
            damage += itemDamage;
        }
        if (registry.getCustomItemByItemStack(mainHandItem) instanceof StatsItem statsItem && statsItem.validStats(true, false)) {
            CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getHelmet()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getHelmet());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getChestplate()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getChestplate());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getLeggings()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getLeggings());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getBoots()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getBoots());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getItemInOffHand()) instanceof StatsItem statsItem && statsItem.validStats(false, false)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getItemInOffHand());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack != mainHandItem && registry.getCustomItemByItemStack(itemStack) instanceof StatsItem statsItem && statsItem.validStats(false, false)) {
                CustomItem customItem = registry.getCustomItemByItemStack(itemStack);
                statsItem.getStats().forEach((stat, value) -> {
                    double val = value;
                    val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                            new HandlersUtils.Parameter(player, Player.class),
                            new HandlersUtils.Parameter(customItem, CustomItem.class),
                            new HandlersUtils.Parameter(stat, Stats.class)
                    });
                    stats.put(stat, stats.get(stat) + val);
                });
            }
        }

        stats.forEach((stat, value) -> {
            if (stat.maxValue >= 0 && stat.maxValue < value) {
                stats.put(stat, stat.maxValue);
            }
        });

        this.damage = damage;
        this.stats.putAll(stats);

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getStat(Stats.HEALTH) / 5 > 40 ? 40 : Math.ceil(getStat(Stats.HEALTH) / 5.0));

        long previousMaxEffectiveHealth = this.maxEffectiveHealth;
        this.maxEffectiveHealth = (long) (getStat(Stats.HEALTH) * (1.0 + getStat(Stats.DEFENSE) / 100.0));
        if (this.effectiveHealth > 0) {
            this.setEffectiveHealth((long) (((double) this.effectiveHealth) / ((double) previousMaxEffectiveHealth) * ((double) this.maxEffectiveHealth)));
        }

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);
        applySpeed();
    }

    public long getRawDamage() {
        long damage = this.damage;

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            damage = HandlersUtils.getValueWithHandlers(this, "getRawDamage", damage, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class)
            });
        }

        return damage;
    }

    public void applySpeed() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return;

        float speed = (float) (getStat(Stats.SPEED) / 1000) * 2;
        player.setWalkSpeed(speed);
    }

    public boolean generateCritical() {
        int criticalChance = (int) Math.round(getStat(Stats.CRITICAL_CHANCE));
        int r = random.nextInt(100);

        return criticalChance - 1 > 0 && criticalChance - 1 >= r;
    }

    public final double getDamage(boolean critic) {
        double criticalDamage = getStat(Stats.CRITICAL_DAMAGE);
        double strength = getStat(Stats.STRENGTH);
        long damage = getRawDamage();

        damage *= 1 + strength / 100;

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            damage = HandlersUtils.getValueWithHandlers(this, "getDamage", damage, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class)
            });
        }

        return critic ? damage * (1 + criticalDamage / 100) : damage;
    }

    public double getStat(Stats stat) {
        if (stat == null) return 0;
        double value = stats.get(stat);

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            Map<Object, Class<?>> params = new HashMap<>();

            params.put(player, Player.class);
            params.put(stat, Stats.class);

            value = HandlersUtils.getValueWithHandlers(this, "getStat", value, double.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class),
                    new HandlersUtils.Parameter(stat, Stats.class)
            });
        }

        return value;
    }

    public long getEffectiveHealth() {
        return effectiveHealth;
    }

    public long getMaxEffectiveHealth() {
        return maxEffectiveHealth;
    }

    public long getHealth() {
        return (long) (effectiveHealth / (1 + getStat(Stats.DEFENSE) / 100));
    }

    public long getMagicalReserve() {
        return magicalReserve;
    }

    public void setEffectiveHealth(long effectiveHealth) {
        this.effectiveHealth = effectiveHealth;
        if (this.effectiveHealth < 0) this.effectiveHealth = 0;
        if (this.effectiveHealth > maxEffectiveHealth) this.effectiveHealth = maxEffectiveHealth;

        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return;

        double playerHealth;

        if (this.effectiveHealth == 0) {
            playerHealth = 0;
        } else if (getStat(Stats.HEALTH) <= 200) {
            playerHealth = Math.ceil((double) getHealth() / 5);
            if (playerHealth == 0) playerHealth = 1;
        } else {
            playerHealth = Math.ceil(40.0 / getStat(Stats.HEALTH) * getHealth());
            if (playerHealth > 40) playerHealth = 40;
            if (playerHealth == 0) playerHealth = 1;
        }

        if (playerHealth == 0) {
            GameListeners.handleDeath(player);
        } else {
            player.setHealth(playerHealth);
        }
    }

    public void setMagicalReserve(long magicalReserve) {
        this.magicalReserve = magicalReserve;
        if (this.magicalReserve < 0) this.magicalReserve = 0;
        if (this.magicalReserve > getStat(Stats.MAGICAL_RESERVE)) this.magicalReserve = (long) getStat(Stats.MAGICAL_RESERVE);
    }

    public void regenHealth() {
        long regen = (long) (effectiveHealth + maxEffectiveHealth / 100 * getStat(Stats.VITALITY));

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            regen = HandlersUtils.getValueWithHandlers(this, "onRegenHealth", regen, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class)
            });
        }

        setEffectiveHealth(regen);
    }

    public void regenMagicalPower() {
        long regen = (long) (magicalReserve + getStat(Stats.MAGICAL_RESERVE) / 100 * 2);

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            regen = HandlersUtils.getValueWithHandlers(this, "onRegenMagicalPower", regen, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class)
            });
        }

        setMagicalReserve(regen);
    }

    public void onRespawn() {
        setEffectiveHealth(maxEffectiveHealth);
    }

    public void onDied() {

    }

    public void takeDamage(long damage, CustomEntity entity) {
        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            Map<Object, Class<?>> params = new HashMap<>();

            params.put(player, Player.class);
            params.put(entity, CustomEntity.class);

            damage = HandlersUtils.getValueWithHandlers(this, "onTakeDamage", damage, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class),
                    new HandlersUtils.Parameter(entity, CustomEntity.class),
                    new HandlersUtils.Parameter(DamageType.MELEE_DAMAGE, DamageType.class)
            });
        }

        setEffectiveHealth(effectiveHealth - damage);
    }

    public Classes getClasses() {
        return classes;
    }

    public void setClasses(Classes classes) {
        if (classes == null) return;
        this.classes = classes;
        registry.applyModification(this);

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            Tab.refreshStatsPartFor(player);
            for (Player playerIn : Bukkit.getOnlinePlayers()) {
                Tab.refreshPlayerSkinOfFor(player, playerIn);
                Tab.refreshPlayersInTabFor(playerIn);
            }
        }
    }

    public UUID getPlayer() {
        return player;
    }

    public Ranks getRank() {
        return rank;
    }

    public void setRank(Ranks rank) {
        if (rank == null) return;
        this.rank = rank;
        registry.applyModification(this);

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            Tab.refreshStatsPartFor(player);
        }

        for (Player playerIn : Bukkit.getOnlinePlayers()) {
            Tab.refreshPlayersInTabFor(playerIn);
        }
    }

    public boolean getCanBreak() {
        return canBreak;
    }

    public void setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
        registry.applyModification(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        registry.applyModification(this);
    }

    public Long getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(Long discordUserId) {
        this.discordUserId = discordUserId;
        registry.applyModification(this);
    }

    public Long getLastUse(CustomItem item) {
        return lastUse.get(item);
    }

    public void use(CustomItem item) {
        lastUse.put(item, Date.from(Instant.now()).getTime());
    }

    public void use(CustomItem item, long minus) {
        lastUse.put(item, Date.from(Instant.now()).getTime() - (minus > 0 ? minus : 0));
    }
}
