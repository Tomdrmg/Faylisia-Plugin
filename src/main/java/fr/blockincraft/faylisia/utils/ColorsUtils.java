package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.utils.colorsexception.InvalidColorException;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorsUtils {
    public static final Pattern hexPattern = Pattern.compile("#[a-fA-F\\d]{6}");
    public static final Pattern gradientPattern = Pattern.compile("&grad\\([^()]+( #[a-fA-F0-9]{6})+\\)");

    public static String translateAll(String message) {
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);

        Matcher gradMatch = gradientPattern.matcher(message);
        while (gradMatch.find()) {
            String color = message.substring(gradMatch.start(), gradMatch.end());

            String colorArgs = color.replace("&grad(", "");
            colorArgs = colorArgs.substring(0, colorArgs.length() - 1);
            String[] args = colorArgs.split(" ");

            List<String> colors = Arrays.asList(args).subList(1, args.length);
            String[] colorsArray = colors.toArray(new String[0]);

            String word = args[0].replace("\\_", " ");

            String replacement = word;

            if (colorsArray.length >= 2) {
                try {
                    replacement = ColorsUtils.generateGradient(word, colorsArray);
                } catch (InvalidColorException ignored) {

                }
            }

            message = message.replace(color, replacement);

            gradMatch = ColorsUtils.gradientPattern.matcher(message);
        }

        message = translateHexColors(message);

        return message;
    }

    public static String generateGradient(String message, String... colours) throws InvalidColorException {
        int count = message.length();
        if (Math.min(count, colours.length) < 2) {
            return message;
        }

        List<String> cols = createGradient(count, colours);

        StringBuilder colourCodes = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            colourCodes.append(ChatColor.of(cols.get(i))).append(message.charAt(i));
        }
        return colourCodes.toString();
    }

    public static List<String> createGradient(int count, String[] colours) throws InvalidColorException {
        Rainbow rainbow = new Rainbow();

        try {
            rainbow.setNumberRange(1, count);
            rainbow.setSpectrum(colours);
        } catch (InvalidColorException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> hexCodes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            hexCodes.add("#" + rainbow.colourAt(i));
        }
        return hexCodes;
    }

    public static String translateHexColors(String s) {
        Matcher matcher = hexPattern.matcher(s);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(result, ChatColor.of(matcher.group()).toString());
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public static String stripHexColors(String s) {
        Matcher matcher = hexPattern.matcher(s);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(result, "");
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
