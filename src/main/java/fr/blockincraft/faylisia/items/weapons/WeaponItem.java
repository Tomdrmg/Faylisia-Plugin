package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.DamageItem;
import fr.blockincraft.faylisia.items.StatsItem;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom item with damage and stats that is a weapon
 */
public class WeaponItem extends CustomItem implements DamageItem, StatsItem {
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
    protected List<String> firstLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Puissance: &c+" + damage));

        List<Map.Entry<Stats, Double>> sorted = stats.entrySet().stream().sorted((o1, o2) -> o1.getKey().index - o2.getKey().index).toList();

        sorted.forEach(entry -> {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7" + entry.getKey().name + " &" + entry.getKey().color + "+" + entry.getValue()));
        });

        return lore;
    }

    @Override
    public int getDamage() {
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
    public double getStat(@NotNull Stats stat) {
        return stats.get(stat);
    }

    @Override
    public boolean hasStat(@NotNull Stats stat) {
        return stats.get(stat) != null;
    }

    @Override
    @NotNull
    public Map<Stats, Double> getStats() {
        return new HashMap<>(stats);
    }

    /**
     * Remove a stat to this item
     * @param stat stat to remove
     * @return this instance
     */
    @NotNull
    public WeaponItem removeStat(@NotNull Stats stat) {
        if (isRegistered()) throw new CustomItem.ChangeRegisteredItem();
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
        if (isRegistered()) throw new CustomItem.ChangeRegisteredItem();
        stats.put(stat, value);
        return this;
    }

    /**
     * Change item type to weapon
     * @return new item type
     */
    @Override
    @NotNull
    protected String getType() {
        return "ARME";
    }
}
