package fr.blockincraft.faylisia.core.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.api.serializer.CustomPlayerSerializer;
import fr.blockincraft.faylisia.api.serializer.PlayerInventorySerializer;
import fr.blockincraft.faylisia.blocks.BlockType;
import fr.blockincraft.faylisia.blocks.CustomBlock;
import fr.blockincraft.faylisia.blocks.DiggingBlock;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.*;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.event.HandlerItemModel;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.tools.ToolItemModel;
import fr.blockincraft.faylisia.items.tools.ToolType;
import fr.blockincraft.faylisia.items.weapons.DamageItemModel;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@JsonSerialize(using = CustomPlayerSerializer.class)
public class CustomPlayerDTO {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final SecureRandom random = new SecureRandom();

    // Stored values
    private final UUID player;
    private Classes classes = Classes.EXPLORER;
    private Ranks rank = Ranks.PLAYER;
    private boolean canBreak = false;
    private String lastName;
    private boolean customNameEnabled;
    private String customName;
    private String lastInventoryAsJson;
    private Long discordUserId;
    private long lastUpdate = 0;
    private long money = 0;
    private boolean sendMessagesToDiscord = true;

    // Non stored values
    private final Map<CustomItem, Long> lastUse = new HashMap<>();
    private final Map<Stats, Double> stats = new HashMap<>();
    private long effectiveHealth = 0;
    private long maxEffectiveHealth = 0;
    private long magicalReserve = 0;
    private long damage = 0;
    private DiggingBlock diggingBlock;

    public CustomPlayerDTO(UUID player) {
        Player pl = Bukkit.getPlayer(player);

        this.player = player;
        this.lastName = pl == null ? "error" : pl.getName();
        this.customName = pl == null ? "error" : pl.getName();
        refreshStats();
        setEffectiveHealth(maxEffectiveHealth);
        setMagicalReserve((long) getStat(Stats.MAGICAL_RESERVE));
        discordUserId = null;
        lastUpdate = Date.from(Instant.now()).getTime();
    }

    public CustomPlayerDTO(CustomPlayer model) {
        this.player = model.getPlayer();
        this.classes = model.getClasses();
        this.rank = model.getRank();
        this.canBreak = model.isCanBreak();
        this.lastName = model.getLastName();
        this.customNameEnabled = model.isCustomNameEnabled();
        this.customName = model.getCustomName();
        this.lastInventoryAsJson = model.getLastInventoryAsJson();
        this.discordUserId = model.getDiscordUserId();
        this.lastUpdate = model.getLastUpdate();
        this.money = model.getMoney();
        this.sendMessagesToDiscord = model.isSendMessagesToDiscord();
    }

    public Handlers[] getMainHandHandler() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return new Handlers[0];

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        CustomItemStack customItemStack = CustomItemStack.fromItemStack(mainHandItem);
        if (customItemStack == null) return new Handlers[0];

        List<Handlers> handlers = new ArrayList<>();

        if (customItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
            handlers.add(handlerItemModel.getHandlers());
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

            if (helmetItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
                handlers.add(handlerItemModel.getHandlers());
            }
        }

        CustomItemStack chestplateItemStack = CustomItemStack.fromItemStack(inventory.getChestplate());
        if (chestplateItemStack != null) {
            if (chestplateItemStack.getItem().isEnchantable()) {
                chestplateItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (chestplateItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
                handlers.add(handlerItemModel.getHandlers());
            }
        }

        CustomItemStack leggingsItemStack = CustomItemStack.fromItemStack(inventory.getLeggings());
        if (leggingsItemStack != null) {
            if (leggingsItemStack.getItem().isEnchantable()) {
                leggingsItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (leggingsItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
                handlers.add(handlerItemModel.getHandlers());
            }
        }

        CustomItemStack bootsItemStack = CustomItemStack.fromItemStack(inventory.getBoots());
        if (bootsItemStack != null) {
            if (bootsItemStack.getItem().isEnchantable()) {
                bootsItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (bootsItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
                handlers.add(handlerItemModel.getHandlers());
            }
        }

        return handlers.toArray(new Handlers[0]);
    }

    public Handlers[] getOthersHandlers() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return null;

        PlayerInventory inventory = player.getInventory();

        List<Handlers> handlers = new ArrayList<>();

        CustomItemStack offHandItemStack = CustomItemStack.fromItemStack(inventory.getItemInOffHand());
        if (offHandItemStack != null) {
            if (offHandItemStack.getItem().isEnchantable()) {
                offHandItemStack.getEnchantments().forEach((enchant, level) -> {
                    handlers.add(enchant.handlers.withLevel(level));
                });
            }

            if (offHandItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
                handlers.add(handlerItemModel.getHandlers());
            }
        }

        for (int i = 0; i < inventory.getStorageContents().length; i++) {
            ItemStack itemStack = inventory.getStorageContents()[i];
            CustomItemStack customItemStack = CustomItemStack.fromItemStack(itemStack);

            if (i != inventory.getHeldItemSlot() && customItemStack != null) {
                if (customItemStack.getItem().isEnchantable()) {
                    customItemStack.getEnchantments().forEach((enchant, level) -> {
                        handlers.add(enchant.handlers.withLevel(level));
                    });
                }

                if (customItemStack.getItem() instanceof HandlerItemModel handlerItemModel) {
                    handlers.add(handlerItemModel.getHandlers());
                }
            }
        }

        return handlers.toArray(new Handlers[0]);
    }

    public Handlers[] getMiscHandlers() {
        List<Handlers> handlers = new ArrayList<>();

        handlers.add(classes.handlers);

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
        if (registry.getCustomItemByItemStack(mainHandItem) instanceof DamageItemModel damageItemModel) {
            CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);

            long itemDamage = damageItemModel.getDamage();
            itemDamage = HandlersUtils.getValueWithHandlers(this, "calculateItemRawDamage", itemDamage, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class),
                    new HandlersUtils.Parameter(customItem, CustomItem.class)
            });
            damage += itemDamage;
        }
        if (registry.getCustomItemByItemStack(mainHandItem) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(true, false)) {
            CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);
            statsItemModel.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getHelmet()) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getHelmet());
            statsItemModel.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getChestplate()) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getChestplate());
            statsItemModel.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getLeggings()) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getLeggings());
            statsItemModel.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getBoots()) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getBoots());
            statsItemModel.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getItemInOffHand()) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(false, false)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getItemInOffHand());
            statsItemModel.getStats().forEach((stat, value) -> {
                double val = value;
                val = HandlersUtils.getValueWithHandlers(this, "calculateItemStat", val, double.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(customItem, CustomItem.class),
                        new HandlersUtils.Parameter(stat, Stats.class)
                });
                stats.put(stat, stats.get(stat) + val);
            });
        }

        for (int i = 0; i < inventory.getStorageContents().length; i++) {
            ItemStack itemStack = inventory.getStorageContents()[i];

            if (i != inventory.getHeldItemSlot() && registry.getCustomItemByItemStack(itemStack) instanceof StatsItemModel statsItemModel && statsItemModel.validStats(false, false)) {
                CustomItem customItem = registry.getCustomItemByItemStack(itemStack);
                statsItemModel.getStats().forEach((stat, value) -> {
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
        if (regen == 0) regen = 1;

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            
            regen = HandlersUtils.getValueWithHandlers(this, "onRegenHealth", regen, long.class, new HandlersUtils.Parameter[]{
                    new HandlersUtils.Parameter(player, Player.class)
            });
        }

        setEffectiveHealth(regen);
    }

    public void regenMagicalPower() {
        long regen = (long) (magicalReserve + getStat(Stats.MAGICAL_RESERVE) / 100 * getStat(Stats.VITALITY));
        if (regen == 0) regen = 1;

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
        applyUpdate();

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
        applyUpdate();

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
        applyUpdate();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
        applyUpdate();
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
        applyUpdate();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Tab.refreshPlayersInTabFor(player);
            Tab.refreshRealsPlayersInTabFor(player);
        }
    }

    public boolean isCustomNameEnabled() {
        return customNameEnabled;
    }

    public void setCustomNameEnabled(boolean customNameEnabled) {
        this.customNameEnabled = customNameEnabled;
        applyUpdate();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Tab.refreshPlayersInTabFor(player);
            Tab.refreshRealsPlayersInTabFor(player);
        }
    }

    public String getLastInventoryAsJson() {
        return lastInventoryAsJson;
    }

    public void setLastInventoryAsJson(String lastInventoryAsJson) {
        this.lastInventoryAsJson = lastInventoryAsJson;
        applyUpdate();
    }

    public Long getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(Long discordUserId) {
        this.discordUserId = discordUserId;
        applyUpdate();
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

    public String getNameToUse() {
        return customNameEnabled ? customName : lastName;
    }

    public void updateLastInventory() {
        Player pl = Bukkit.getPlayer(player);
        if (pl == null) return;

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(PlayerInventory.class, new PlayerInventorySerializer());
        mapper.registerModule(module);

        try {
            setLastInventoryAsJson(mapper.writeValueAsString(pl.getInventory()));
        } catch (Exception ignored) {

        }

        applyUpdate();
    }

    private void applyUpdate() {
        lastUpdate = Date.from(Instant.now()).getTime();
        registry.applyModification(this);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    @Nullable
    public DiggingBlock getDiggingBlock() {
        return diggingBlock;
    }

    public void setDiggingBlock(@Nullable DiggingBlock diggingBlock) {
        this.diggingBlock = diggingBlock;
    }

    public boolean checkCanBreakBlock(@NotNull DiggingBlock diggingBlock, boolean sendMessage) {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return false;

        if (diggingBlock.getBlock() != null && diggingBlock.getBlock().getCurrentState() != null) {
            CustomBlock block = diggingBlock.getBlock();
            BlockType state = block.getCurrentState();

            CustomItemStack customItemStack = CustomItemStack.fromItemStack(player.getInventory().getItemInMainHand());

            int breakingLevel = 0;
            if (customItemStack != null && customItemStack.getItem() instanceof ToolItemModel toolItemModel) {
                breakingLevel = toolItemModel.getBreakingLevel();
            }

            if (breakingLevel < state.getLevel()) {
                if (sendMessage) {
                    // Todo : send a message
                }
                return false;
            }

            List<ToolType> toolTypes = new ArrayList<>(List.of(ToolType.HAND));
            if (customItemStack != null && customItemStack.getItem() instanceof ToolItemModel toolItemModel) {
                toolTypes.addAll(List.of(toolItemModel.getToolTypes()));
            }

            boolean hasRequiredTool = false;

            for (ToolType toolType : state.getToolTypes()) {
                if (toolTypes.contains(toolType)) {
                    hasRequiredTool = true;
                    break;
                }
            }

            if (!hasRequiredTool) {
                if (sendMessage) {
                    // Todo : send a message
                }
                return false;
            }
        }

        return true;
    }

    public void setMoney(long money) {
        this.money = money;
        applyUpdate();
    }

    public boolean hasMoney(long needed) {
        return money >= needed;
    }

    public long getMoney() {
        return money;
    }

    public void setSendMessagesToDiscord(boolean sendMessagesToDiscord) {
        this.sendMessagesToDiscord = sendMessagesToDiscord;
        applyUpdate();
    }

    public boolean isSendMessagesToDiscord() {
        return sendMessagesToDiscord;
    }
}
