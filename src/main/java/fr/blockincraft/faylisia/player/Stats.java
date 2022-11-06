package fr.blockincraft.faylisia.player;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * This enum contain all stats of players except effective health, magical reserve (not the max but the stored) and damages
 */
public enum Stats {
    POWER(0, "Puissance", "&c", '\uE000', '\uE011', 5.0, -1.0),
    HEALTH(1, "Santé Maximale", "&c", '\uE001', '\uE012', 100.0, -1.0),
    VITALITY(2, "Vitalité", "&c", '\uE002', '\uE013', 2.0, 100.0),
    DEFENSE(3, "Défense", "&a", '\uE003', '\uE014', 0.0, -1.0),
    STRENGTH(4, "Force", "&4", '\uE004', '\uE015', 10.0, -1.0),
    CRITICAL_DAMAGE(5, "Dégats Critiques", "&9", '\uE005', '\uE016', 100.0, -1.0),
    CRITICAL_CHANCE(6, "Chance Critique", "&9", '\uE006', '\uE017', 20.0, 100.0),
    MAGICAL_RESERVE(7, "Éthernanos Max", "&d", '\uE007', '\uE018', 0.0, -1.0),
    SPEED(8, "Vitesse", "&f", '\uE008', '\uE019', 100.0, 500.0),
    LUCK(9, "Chance", "&b", '\uE009', '\uE020', 0.0, 1900.0),
    KNOWLEDGE(10, "Savoir", "&e", '\uE010', '\uE021', 0.0, -1.0),
    MINING_SPEED(11, "Mining Speed", "&6", '\uE010', '\uE021', 100.0, -1.0),
    MINING_FORTUNE(12, "Mining Fortune", "&6", '\uE010', '\uE021', 100.0, -1.0);

    public final int index;
    public final String name;
    public final String color;
    public final char icon;
    public final char bigIcon;
    public final double defaultValue;
    public final double maxValue;

    /**
     * @param index index to order them on display
     * @param name display name of the stat
     * @param color color char to use {@link ChatColor}
     * @param icon icon representing the stat
     * @param bigIcon bigger icon version
     * @param defaultValue default value of this stat
     * @param maxValue limit value for this stat
     */
    Stats(int index, @NotNull String name, @NotNull String color, char icon, char bigIcon, double defaultValue, double maxValue) {
        this.index = index;
        this.name = name;
        this.icon = icon;
        this.bigIcon = bigIcon;
        this.color = color;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
    }
}
