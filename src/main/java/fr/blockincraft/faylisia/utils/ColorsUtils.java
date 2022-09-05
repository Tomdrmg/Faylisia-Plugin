package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.utils.colorsexception.InvalidColorException;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities methods to translate chat colors
 */
public class ColorsUtils {
    // Create patterns
    public static final Pattern hexPattern = Pattern.compile("#[a-fA-F\\d]{6}");
    public static final Pattern gradientPattern = Pattern.compile("&grad\\([^()]+( #[a-fA-F0-9]{6})+\\)");

    /**
     * This method translate all colors (Bukkit {@link org.bukkit.ChatColor}, hex {@link ChatColor} and gradient with method of this class)
     * @param message text to translate
     * @return translated text
     */
    public static String translateAll(String message) {
        // Translate Bukkit colors
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);

        // Translate gradients colors
        Matcher gradMatch = gradientPattern.matcher(message);
        while (gradMatch.find()) {
            // Get color element like "&grad(text #000000 #000000)
            String color = message.substring(gradMatch.start(), gradMatch.end());

            // Split args from color element
            String colorArgs = color.replace("&grad(", "");
            colorArgs = colorArgs.substring(0, colorArgs.length() - 1);
            String[] args = colorArgs.split(" ");

            // Get all colors and place it in an array
            List<String> colors = Arrays.asList(args).subList(1, args.length);
            String[] colorsArray = colors.toArray(new String[0]);

            // Get the word and replace \_ by space to from a sentence
            String word = args[0].replace("\\_", " ");

            // Create replacement var that will be colored only if it
            // contains at least two colors and that colors are valid
            String replacement = word;

            if (colorsArray.length >= 2) {
                // Try to add colors in text
                try {
                    replacement = ColorsUtils.generateGradient(word, colorsArray);
                } catch (InvalidColorException ignored) {

                }
            }

            // Replace color element by replacement var
            message = message.replace(color, replacement);

            // Reset the pattern with the new message²
            gradMatch = ColorsUtils.gradientPattern.matcher(message);
        }

        // Translate hex colors
        message = translateHexColors(message);

        return message;
    }

    /**
     * This method generate a gradient from colours
     * @param message message to transform
     * @param colors color range
     * @return gradient message
     * @throws InvalidColorException if a color isn't a valid color like #342345
     */
    public static String generateGradient(String message, String... colors) throws InvalidColorException {
        // Check if two colors or more are used
        int count = message.length();
        if (Math.min(count, colors.length) < 2) {
            return message;
        }

        // Get all colors of gradiant
        List<String> cols = createGradient(count, colors);

        // Apply all colors to message
        StringBuilder colourCodes = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            colourCodes.append(ChatColor.of(cols.get(i))).append(message.charAt(i));
        }
        return colourCodes.toString();
    }

    /**
     * This method create all colors of a gradiant from the color range
     * @param count amount of color to create
     * @param colors color range
     * @return colors of gradient
     * @throws InvalidColorException
     */
    public static List<String> createGradient(int count, String[] colors) throws InvalidColorException {
        // Create an instance of Rainbow
        Rainbow rainbow = new Rainbow();

        // Try to apply parameters to Rainbow instance
        try {
            // Apply parameters
            rainbow.setNumberRange(1, count);
            rainbow.setSpectrum(colors);
        } catch (InvalidColorException e) {
            // Resend error if it's an invalid color exception
            throw e;
        } catch (Exception e) {
            // Show others exception
            e.printStackTrace();
        }

        // Add all colors and return them
        List<String> hexCodes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            hexCodes.add("#" + rainbow.colorAt(i));
        }
        return hexCodes;
    }

    /**
     * This method translate all hex colors to chat colors
     * @param message message to translate
     * @return translated message
     */
    public static String translateHexColors(String message) {
        // Create matcher and result string builder
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder result = new StringBuilder();

        // Find all hex colors
        while (matcher.find()) {
            // Apply color
            matcher.appendReplacement(result, ChatColor.of(matcher.group()).toString());
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * This method remove all hex colors
     * @param message message to translate
     * @return translated message
     */
    public static String stripHexColors(String message) {
        // Create matcher and result string builder
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder result = new StringBuilder();

        // Find all hex colors
        while (matcher.find()) {
            // Remove color
            matcher.appendReplacement(result, "");
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
