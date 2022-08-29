package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.DamageItem;
import fr.blockincraft.faylisia.items.StatsItem;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponItem extends CustomItem implements DamageItem, StatsItem {
    private final Map<Stats, Double> stats = new HashMap<>();
    private int damage = 0;

    public WeaponItem(Material material, String id) throws InvalidBuildException {
        super(material, id);
    }

    @Override
    public List<String> firstLore() {
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
    public double getStat(Stats stat) {
        return stats.get(stat);
    }

    @Override
    public boolean hasStat(Stats stat) {
        return stats.get(stat) != null;
    }

    @Override
    public Map<Stats, Double> getStats() {
        return new HashMap<>(stats);
    }

    public WeaponItem removeStat(Stats stat) {
        if (isRegistered()) throw new CustomItem.ChangeRegisteredItem();
        stats.remove(stat);
        return this;
    }

    public WeaponItem setStat(Stats stat, double value) {
        if (isRegistered()) throw new CustomItem.ChangeRegisteredItem();
        stats.put(stat, value);
        return this;
    }

    @Override
    protected String getType() {
        return "ARME";
    }
}
