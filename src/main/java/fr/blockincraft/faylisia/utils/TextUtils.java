package fr.blockincraft.faylisia.utils;

import org.jetbrains.annotations.NotNull;

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
}
