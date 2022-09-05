package fr.blockincraft.faylisia.core.dto;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.DamageItem;
import fr.blockincraft.faylisia.items.HandlerItem;
import fr.blockincraft.faylisia.items.StatsItem;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.listeners.GameListeners;
import fr.blockincraft.faylisia.player.permission.Ranks;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.player.Classes;
import fr.blockincraft.faylisia.displays.Tab;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.security.SecureRandom;
import java.util.*;

public class CustomPlayerDTO {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final SecureRandom random = new SecureRandom();

    private Classes classes = Classes.HUMAN;
    private Ranks rank = Ranks.PLAYER;
    private boolean canBreak = false;
    private String name;
    private Long discordUserId;

    private final UUID player;
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

    public Handlers getMainHandHandler() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return null;

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);
        if (customItem instanceof HandlerItem handlerItem) {
            if (handlerItem.getHandlers() != null) {
                return handlerItem.getHandlers();
            }
        }

        return null;
    }

    public Handlers[] getArmorSetHandlers() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) return null;

        PlayerInventory inventory = player.getInventory();

        Map<ArmorSet, Integer> armorSets = new HashMap<>();

        if (registry.getCustomItemByItemStack(inventory.getHelmet()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            if (armorSet != null) {
                armorSets.put(armorSet, 1);
            }
        }

        if (registry.getCustomItemByItemStack(inventory.getChestplate()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            if (armorSet != null) {
                armorSets.put(armorSet, armorSets.containsKey(armorSet) ? armorSets.get(armorSet) + 1 : 1);
            }
        }

        if (registry.getCustomItemByItemStack(inventory.getLeggings()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            if (armorSet != null) {
                armorSets.put(armorSet, armorSets.containsKey(armorSet) ? armorSets.get(armorSet) + 1 : 1);
            }
        }

        if (registry.getCustomItemByItemStack(inventory.getBoots()) instanceof ArmorItem armorItem) {
            ArmorSet armorSet = armorItem.getArmorSet();
            if (armorSet != null) {
                armorSets.put(armorSet, armorSets.containsKey(armorSet) ? armorSets.get(armorSet) + 1 : 1);
            }
        }

        List<Handlers> handlers = new ArrayList<>();

        armorSets.forEach((armorSet, pieces) -> {
            for (ArmorSet.Bonus bonus : armorSet.getBonus()) {
                if (bonus.getMinimum() <= pieces) {
                    handlers.add(bonus.getHandlers());
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

        if (registry.getCustomItemByItemStack(inventory.getHelmet()) instanceof HandlerItem handlerItem) {
            if (handlerItem.getHandlers() != null) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        if (registry.getCustomItemByItemStack(inventory.getChestplate()) instanceof HandlerItem handlerItem) {
            if (handlerItem.getHandlers() != null) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        if (registry.getCustomItemByItemStack(inventory.getLeggings()) instanceof HandlerItem handlerItem) {
            if (handlerItem.getHandlers() != null) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        if (registry.getCustomItemByItemStack(inventory.getBoots()) instanceof HandlerItem handlerItem) {
            if (handlerItem.getHandlers() != null) {
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
        if (registry.getCustomItemByItemStack(inventory.getItemInOffHand()) instanceof HandlerItem handlerItem) {
            if (handlerItem.getHandlers() != null) {
                handlers.add(handlerItem.getHandlers());
            }
        }

        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack != mainHandItem && registry.getCustomItemByItemStack(itemStack) instanceof HandlerItem handlerItem) {
                if (handlerItem.getHandlers() != null) {
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

        Handlers mainHandHandlers = getMainHandHandler();
        Handlers[] armorSetHandlers = getArmorSetHandlers();
        Handlers[] armorSlotHandlers = getArmorSlotHandlers();
        Handlers[] othersHandlers = getOthersHandlers();

        // Calculate stats
        for (Stats stat : Stats.values()) {
            double defaultValue = stat.defaultValue;

            if (mainHandHandlers != null) defaultValue = mainHandHandlers.getDefaultStat(player, stat, defaultValue, true, false);
            for (Handlers handlers : armorSetHandlers) {
                defaultValue = handlers.getDefaultStat(player, stat, defaultValue, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                defaultValue = handlers.getDefaultStat(player, stat, defaultValue, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                defaultValue = handlers.getDefaultStat(player, stat, defaultValue, false, false);
            }

            stats.put(stat, defaultValue);
        }
        damage = Stats.handDamage;

        if (mainHandHandlers != null) damage = mainHandHandlers.calculateHandRawDamage(player, damage, true, false);
        for (Handlers handlers : armorSetHandlers) {
            damage = handlers.calculateHandRawDamage(player, damage, false, true);
        }
        for (Handlers handlers : armorSlotHandlers) {
            damage = handlers.calculateHandRawDamage(player, damage, false, true);
        }
        for (Handlers handlers : othersHandlers) {
            damage = handlers.calculateHandRawDamage(player, damage, false, false);
        }

        PlayerInventory inventory = player.getInventory();

        ItemStack mainHandItem = inventory.getItemInMainHand();
        if (registry.getCustomItemByItemStack(mainHandItem) instanceof DamageItem damageItem) {
            CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);
            long itemDamage = damageItem.getDamage();

            if (mainHandHandlers != null) itemDamage = mainHandHandlers.calculateItemRawDamage(player, customItem, itemDamage, true, false);
            for (Handlers handlers : armorSetHandlers) {
                itemDamage = handlers.calculateItemRawDamage(player, customItem, itemDamage, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                itemDamage = handlers.calculateItemRawDamage(player, customItem, itemDamage, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                itemDamage = handlers.calculateItemRawDamage(player, customItem, itemDamage, false, false);
            }

            damage += itemDamage;
        }
        if (registry.getCustomItemByItemStack(mainHandItem) instanceof StatsItem statsItem && statsItem.validStats(true, false)) {
            CustomItem customItem = registry.getCustomItemByItemStack(mainHandItem);
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;

                if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                }

                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getHelmet()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getHelmet());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;

                if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                }

                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getChestplate()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getChestplate());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;

                if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                }

                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getLeggings()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getLeggings());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;

                if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                }

                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getBoots()) instanceof StatsItem statsItem && statsItem.validStats(false, true)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getBoots());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;

                if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                }

                stats.put(stat, stats.get(stat) + val);
            });
        }

        if (registry.getCustomItemByItemStack(inventory.getItemInOffHand()) instanceof StatsItem statsItem && statsItem.validStats(false, false)) {
            CustomItem customItem = registry.getCustomItemByItemStack(inventory.getItemInOffHand());
            statsItem.getStats().forEach((stat, value) -> {
                double val = value;

                if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                }

                stats.put(stat, stats.get(stat) + val);
            });
        }

        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack != mainHandItem && registry.getCustomItemByItemStack(itemStack) instanceof StatsItem statsItem && statsItem.validStats(false, false)) {
                CustomItem customItem = registry.getCustomItemByItemStack(itemStack);
                statsItem.getStats().forEach((stat, value) -> {
                    double val = value;

                    if (mainHandHandlers != null) val = mainHandHandlers.calculateItemStat(player, customItem, stat, val, true, false);
                    for (Handlers handlers : armorSetHandlers) {
                        val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                    }
                    for (Handlers handlers : armorSlotHandlers) {
                        val = handlers.calculateItemStat(player, customItem, stat, val, false, true);
                    }
                    for (Handlers handlers : othersHandlers) {
                        val = handlers.calculateItemStat(player, customItem, stat, val, false, false);
                    }

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
        this.maxEffectiveHealth = (long) (getStat(Stats.HEALTH) * (1 + getStat(Stats.DEFENSE) / 100));
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
            Handlers mainHandHandlers = getMainHandHandler();
            Handlers[] armorSetHandlers = getArmorSetHandlers();
            Handlers[] armorSlotHandlers = getArmorSlotHandlers();
            Handlers[] othersHandlers = getOthersHandlers();

            if (mainHandHandlers != null) damage = mainHandHandlers.getRawDamage(player, damage, true, false);
            for (Handlers handlers : armorSetHandlers) {
                damage = handlers.getRawDamage(player, damage, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                damage = handlers.getRawDamage(player, damage, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                damage = handlers.getRawDamage(player, damage, false, false);
            }
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
            Handlers mainHandHandlers = getMainHandHandler();
            Handlers[] armorSetHandlers = getArmorSetHandlers();
            Handlers[] armorSlotHandlers = getArmorSlotHandlers();
            Handlers[] othersHandlers = getOthersHandlers();

            if (mainHandHandlers != null) damage = mainHandHandlers.getDamage(player, damage, true, false);
            for (Handlers handlers : armorSetHandlers) {
                damage = handlers.getDamage(player, damage, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                damage = handlers.getDamage(player, damage, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                damage = handlers.getDamage(player, damage, false, false);
            }
        }

        return critic ? damage * (1 + criticalDamage / 100) : damage;
    }

    public double getStat(Stats stat) {
        if (stat == null) return 0;
        double value = stats.get(stat);

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            Handlers mainHandHandlers = getMainHandHandler();
            Handlers[] armorSetHandlers = getArmorSetHandlers();
            Handlers[] armorSlotHandlers = getArmorSlotHandlers();
            Handlers[] othersHandlers = getOthersHandlers();

            if (mainHandHandlers != null) value = mainHandHandlers.getStat(player, stat, value, true, false);
            for (Handlers handlers : armorSetHandlers) {
                value = handlers.getStat(player, stat, value, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                value = handlers.getStat(player, stat, value, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                value = handlers.getStat(player, stat, value, false, false);
            }
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
            playerHealth = Math.ceil((double) this.effectiveHealth / 5);
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
            Handlers mainHandHandlers = getMainHandHandler();
            Handlers[] armorSetHandlers = getArmorSetHandlers();
            Handlers[] armorSlotHandlers = getArmorSlotHandlers();
            Handlers[] othersHandlers = getOthersHandlers();

            if (mainHandHandlers != null) regen = mainHandHandlers.onRegenHealth(player, regen, true, false);
            for (Handlers handlers : armorSetHandlers) {
                regen = handlers.onRegenHealth(player, regen, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                regen = handlers.onRegenHealth(player, regen, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                regen = handlers.onRegenHealth(player, regen, false, false);
            }
        }

        setEffectiveHealth(regen);
    }

    public void regenMagicalPower() {
        long regen = (long) (magicalReserve + getStat(Stats.MAGICAL_RESERVE) / 100 * 2);

        Player player = Bukkit.getPlayer(this.player);
        if (player != null) {
            Handlers mainHandHandlers = getMainHandHandler();
            Handlers[] armorSetHandlers = getArmorSetHandlers();
            Handlers[] armorSlotHandlers = getArmorSlotHandlers();
            Handlers[] othersHandlers = getOthersHandlers();

            if (mainHandHandlers != null) regen = mainHandHandlers.onRegenMagicalPower(player, regen, true, false);
            for (Handlers handlers : armorSetHandlers) {
                regen = handlers.onRegenMagicalPower(player, regen, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                regen = handlers.onRegenMagicalPower(player, regen, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                regen = handlers.onRegenMagicalPower(player, regen, false, false);
            }
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
            Handlers mainHandHandlers = getMainHandHandler();
            Handlers[] armorSetHandlers = getArmorSetHandlers();
            Handlers[] armorSlotHandlers = getArmorSlotHandlers();
            Handlers[] othersHandlers = getOthersHandlers();

            if (mainHandHandlers != null) damage = mainHandHandlers.onTakeDamage(player, entity, damage, true, false);
            for (Handlers handlers : armorSetHandlers) {
                damage = handlers.onTakeDamage(player, entity, damage, false, true);
            }
            for (Handlers handlers : armorSlotHandlers) {
                damage = handlers.onTakeDamage(player, entity, damage, false, true);
            }
            for (Handlers handlers : othersHandlers) {
                damage = handlers.onTakeDamage(player, entity, damage, false, false);
            }
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
}
