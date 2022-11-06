package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.StatsItemModel;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.TextUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom item with damage and stats that is a weapon
 */
public class WeaponItem extends CustomItem implements DamageItemModel, StatsItemModel {
    private final Map<Stats, Double> stats = new HashMap<>();
    private int damage = 0;

    public WeaponItem(@NotNull Material material, @NotNull String id) throws InvalidBuildException {
        super(material, id);
    }

    /**
     * Add stats and damages to the lore
     * @return text to add
     */
    @Override
    @NotNull
    protected List<String> firstLore(CustomItemStack customItemStack) {
        return TextUtils.genStatsLore(customItemStack, this);
    }

    @Override
    public int getDamage(CustomItemStack customItemStack) {
        int damage = this.damage;

        if (this.isEnchantable(customItemStack)) {
            for (Map.Entry<CustomEnchantments, Integer> entry : customItemStack.getEnchantments().entrySet()) {
                damage += entry.getKey().damageBonus.itemDamage(customItemStack, entry.getValue());
            }
        }

        return damage;
    }

    /**
     * Change item damages
     * @param damage new value
     * @return this instance
     */
    @NotNull
    public WeaponItem setDamage(int damage) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        if (damage < 0) damage = 0;
        this.damage = damage;
        return this;
    }

    @Override
    public boolean validStats(boolean inMainHand, boolean inArmorSlot) {
        return inMainHand;
    }

    @Override
    public double getStat(@NotNull Stats stat, CustomItemStack customItemStack) {
        double value = stats.containsKey(stat) ? stats.get(stat) : 0;

        if (this.isEnchantable(customItemStack)) {
            for (Map.Entry<CustomEnchantments, Integer> entry : customItemStack.getEnchantments().entrySet()) {
                value += entry.getKey().statsBonus.itemStat(customItemStack, stat, entry.getValue());
            }
        }

        return value;
    }

    @Override
    @NotNull
    public Map<Stats, Double> getStats(CustomItemStack customItemStack) {
        return new HashMap<>(stats);
    }

    /**
     * Remove a stat to this item
     * @param stat stat to remove
     * @return this instance
     */
    @NotNull
    public WeaponItem removeStat(@NotNull Stats stat) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        stats.remove(stat);
        return this;
    }

    /**
     * Add or edit a stat on this item
     * @param stat stat to add/edit
     * @param value value of stat
     * @return this instance
     */
    @NotNull
    public WeaponItem setStat(@NotNull Stats stat, double value) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        stats.put(stat, value);
        return this;
    }

    /**
     * Change item type to weapon
     * @return new item type
     */
    @Override
    @NotNull
    protected String getType(CustomItemStack customItemStack) {
        return "ARME";
    }
}
