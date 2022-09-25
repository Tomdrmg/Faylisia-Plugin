package fr.blockincraft.faylisia.items.armor;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.StatsItemModel;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArmorItem extends CustomItem implements StatsItemModel {
    // All materials that are armor items
    private static final Material[] armorMaterials = new Material[]{
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,

            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,

            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,

            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,

            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,

            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,

            Material.TURTLE_HELMET,

            Material.PLAYER_HEAD
    };

    private ArmorSet armorSet = null;
    private final Map<Stats, Double> stats = new HashMap<>();

    public ArmorItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    @Override
    @NotNull
    public List<String> firstLore() {
        List<String> lore = new ArrayList<>();

        List<Map.Entry<Stats, Double>> sorted = stats.entrySet().stream().sorted((o1, o2) -> o1.getKey().index - o2.getKey().index).toList();

        sorted.forEach(entry -> {
            lore.add(ColorsUtils.translateAll("&7" + entry.getKey().name + " &" + entry.getKey().color + "+" + entry.getValue()));
        });

        return lore;
    }

    @Override
    @NotNull
    protected List<String> moreLore() {
        List<String> lore = new ArrayList<>();

        for (ArmorSet.Bonus bonus : armorSet.getBonus()) {
            lore.add("");
            lore.add(ColorsUtils.translateAll("&d" + bonus.minimum() + " Pieces bonus - " + bonus.name() + "&d:"));
            for (String descPart : bonus.description()) {
                lore.add(ColorsUtils.translateAll(descPart));
            }
        }

        return lore;
    }

    /**
     * Change an armor set to add set bonus when wear
     * @param armorSet new value
     * @return this instance
     */
    public ArmorItem setArmorSet(@Nullable ArmorSet armorSet) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        this.armorSet = armorSet;
        return this;
    }

    @NotNull
    public ArmorSet getArmorSet() {
        return armorSet;
    }

    @Override
    protected void registerOthers() {
        if (!Arrays.asList(armorMaterials).contains(getMaterial())) throw new InvalidBuildException("Armor item can only be an armor material");
        if (!armorSet.isRegistered()) throw new InvalidBuildException("Armor set must be registered before armor item");
    }

    @Override
    public boolean validStats(boolean inMainHand, boolean inArmorSlot) {
        return inArmorSlot;
    }

    @Override
    public double getStat(@NotNull Stats stat) {
        return stats.get(stat);
    }

    @Override
    public boolean hasStat(@NotNull Stats stat) {
        return stats.get(stat) != null;
    }

    /**
     * Remove a stat of this item
     * @param stat stat to remove
     * @return this instance
     */
    public ArmorItem removeStat(@NotNull Stats stat) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        stats.remove(stat);
        return this;
    }

    /**
     * Add or edit a stat of this item
     * @param stat stat to add/edit
     * @param value value of this stat
     * @return this instance
     */
    public ArmorItem setStat(@NotNull Stats stat, double value) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        stats.put(stat, value);
        return this;
    }

    @Override
    @NotNull
    public Map<Stats, Double> getStats() {
        return new HashMap<>(stats);
    }

    @Override
    @NotNull
    protected String getType() {
        return "PIECE D'ARMURE";
    }
}
