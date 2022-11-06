package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.StatsItemModel;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.items.weapons.DamageItemModel;
import fr.blockincraft.faylisia.player.Stats;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TextUtils {
    /**
     * Transform a long like 10000 to a String with commas like "10,000"
     * @param value long to transform
     * @return String with commas
     */
    @NotNull
    public static String valueWithCommas(long value) {
        char[] valueAsChars = String.valueOf(value).toCharArray();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < valueAsChars.length; i++) {
            sb.append(valueAsChars[valueAsChars.length - i - 1]);
            if ((i + 1) % 3 == 0 && i != valueAsChars.length - 1) {
                sb.append(",");
            }
        }

        sb.reverse();
        return sb.toString();
    }

    /**
     * Transform number to roman number like 10 to X and 125 to CXXV
     * @param num number to transform
     * @return roman number
     */
    @NotNull
    public static String intToRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLetters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for(int i = 0; i < values.length; i++) {
            while(num >= values[i]) {
                num = num - values[i];
                roman.append(romanLetters[i]);
            }
        }

        return roman.toString();
    }

    public static List<String> genStatsLore(CustomItemStack customItemStack, CustomItem customItem) {
        List<String> lore = new ArrayList<>();

        if (customItem instanceof StatsItemModel statsItem) {
            List<Stats> stats = Arrays.stream(Stats.values()).sorted((o1, o2) -> o1.index - o2.index).toList();

            stats.forEach(stat -> {
                double totalValue = 0;

                StringBuilder statText = new StringBuilder();
                statText.append(stat.color).append(stat.name);

                double itemValue = statsItem.getStat(stat, customItemStack);
                totalValue += itemValue;

                if (itemValue > 0) {
                    statText.append(" #c9c9c9+").append(itemValue);
                }

                if (customItem.isEnchantable(customItemStack)) {
                    double enchantStatValue = 0;

                    for (Map.Entry<CustomEnchantments, Integer> entry : customItemStack.getEnchantments().entrySet()) {
                        enchantStatValue += entry.getKey().statsBonus.itemStat(customItemStack, stat, entry.getValue());
                    }

                    if (enchantStatValue > 0) {
                        statText.append(" (#ff73f7+").append(enchantStatValue).append(")");
                    }

                    totalValue += enchantStatValue;
                }

                // For modifiers : #ffc02e

                if (totalValue > 0) {
                    lore.add(ColorsUtils.translateAll(statText.toString()));
                }
            });
        }

        return lore;
    }
}
