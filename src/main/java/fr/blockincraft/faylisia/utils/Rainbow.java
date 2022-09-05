package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.utils.colorsexception.InvalidColorException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Rainbow {
    private double minNum;
    private double maxNum;
    private String[] colors;
    private List<ColourGradient> colorGradients;

    /**
     * Initialize size, colors and spectrum
     */
    public Rainbow() {
        try {
            minNum = 0;
            maxNum = 100;
            colors = new String[]{"red", "yellow", "lime", "blue"};
            setSpectrum(colors);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * This method return color at an emplacement
     * @param number emplacement
     * @return color
     */
    public String colorAt(double number) {
        if (colorGradients.size() == 1) {
            return colorGradients.get(0).colorAt(number);
        } else {
            double segment = (maxNum - minNum)/(colorGradients.size());
            int index = (int) Math.min(Math.floor((Math.max(number, minNum) - minNum)/segment), colorGradients.size() - 1);
            return colorGradients.get(index).colorAt(number);
        }
    }

    public void setSpectrum(String ... spectrum) throws Exception {
        if (spectrum.length < 2) {
            throw new Exception("At least two colors for rainbow!");
        } else {
            double increment = (maxNum - minNum) / (spectrum.length - 1);
            ColourGradient firstGradient = new ColourGradient();
            firstGradient.setGradient(spectrum[0], spectrum[1]);
            firstGradient.setNumberRange(minNum, minNum + increment);

            colorGradients = new ArrayList<>();
            colorGradients.add(firstGradient);

            for (int i = 1; i < spectrum.length - 1; i++) {
                ColourGradient colourGradient = new ColourGradient();
                colourGradient.setGradient(spectrum[i], spectrum[i + 1]);
                colourGradient.setNumberRange(minNum + increment * i, minNum + increment * (i + 1));
                colorGradients.add(colourGradient);
            }

            colors = spectrum;
        }
    }

    public void setNumberRange(double minNumber, double maxNumber) throws Exception
    {
        if (maxNumber > minNumber) {
            minNum = minNumber;
            maxNum = maxNumber;
            setSpectrum(colors);
        } else {
            throw new Exception("maxNumber (" + maxNum + ") is not greater than minNumber (" + minNum + ")");
        }
    }

}

class ColourGradient {
    private int[] startColor = {0xff, 0x00, 0x00};
    private int[] endColor = {0x00, 0x00, 0xff};
    private double minNum = 0;
    private double maxNum = 100;

    private static Hashtable<String, int[]> htmlColors;
    static {
        htmlColors = new Hashtable<>();
        htmlColors.put("black", new int[]{0x00, 0x00, 0x00});
        htmlColors.put("navy", new int[]{0x00, 0x00, 0x80});
        htmlColors.put("blue", new int[]{0x00, 0x00, 0xff});
        htmlColors.put("green", new int[]{0x00, 0x80, 0x00});
        htmlColors.put("teal", new int[]{0x00, 0x80, 0x80});
        htmlColors.put("lime", new int[]{0x00, 0xff, 0x00});
        htmlColors.put("aqua", new int[]{0x00, 0xff, 0xff});
        htmlColors.put("maroon", new int[]{0x80, 0x00, 0x00});
        htmlColors.put("purple", new int[]{0x80, 0x00, 0x80});
        htmlColors.put("olive", new int[]{0x80, 0x80, 0x00});
        htmlColors.put("grey", new int[]{0x80, 0x80, 0x80});
        htmlColors.put("gray", new int[]{0x80, 0x80, 0x80});
        htmlColors.put("silver", new int[]{0xc0, 0xc0, 0xc0});
        htmlColors.put("red", new int[]{0xff, 0x00, 0x00});
        htmlColors.put("fuchsia", new int[]{0xff, 0x00, 0xff});
        htmlColors.put("orange", new int[]{0xff, 0x80, 0x00});
        htmlColors.put("yellow", new int[]{0xff, 0xff, 0x00});
        htmlColors.put("white", new int[]{0xff, 0xff, 0xff});
    }

    public String colorAt(double number) {
        return formatHex(calcHex(number, startColor[0], endColor[0])) + formatHex(calcHex(number, startColor[1], endColor[1])) + formatHex(calcHex(number, startColor[2], endColor[2]));
    }

    private int calcHex(double number, int channelStart, int channelEnd) {
        double num = number;
        if (num < minNum) {
            num = minNum;
        }
        if (num > maxNum) {
            num = maxNum;
        }
        double numRange = maxNum - minNum;
        double cPerUnit = (channelEnd - channelStart)/numRange;
        return (int) Math.round(cPerUnit * (num - minNum) + channelStart);
    }

    /**
     * This method translate an int hex to a string hex
     * @param val hex
     * @return string hex
     */
    private String formatHex(int val) {
        String hex = Integer.toHexString(val);
        if (hex.length() == 1) {
            return '0' + hex;
        } else {
            return hex;
        }
    }

    /**
     * This method set number range of this gradient
     * @param minNumber min number
     * @param maxNumber max number
     * @throws Exception if min number greater or equal max number
     */
    public void setNumberRange(double minNumber, double maxNumber) throws Exception {
        if (maxNumber > minNumber) {
            minNum = minNumber;
            maxNum = maxNumber;
        } else {
            throw new Exception("maxNumber (" + maxNum + ") is not greater than minNumber (" + minNum + ")");
        }
    }

    /**
     * Set start and end color
     * @param colorStart start color
     * @param colorEnd end color
     * @throws InvalidColorException if at least one color is invalid
     */
    public void setGradient(String colorStart, String colorEnd) throws InvalidColorException {
        startColor = getHexColor(colorStart);
        endColor = getHexColor(colorEnd);
    }

    /**
     * This method parse a hex color string to a rgb array <br/>
     * This also accept html colors
     * @param s hex string
     * @return rgb array from hex
     * @throws InvalidColorException if color is invalid
     */
    private int[] getHexColor(String s) throws InvalidColorException {
        // Find if color is hex or html
        if (s.matches("^#?[0-9a-fA-F]{6}$")){
            // Parse hex color
            return rgbStringToArray(s.replace("#", ""));
        } else {
            // Get html color to rgb
            int[] rgbArray = htmlColors.get(s.toLowerCase());
            if (rgbArray == null) {
                throw new InvalidColorException(s);
            } else {
                return rgbArray;
            }
        }
    }

    /**
     * This method parse string rgb to rgb array
     * @param s hex string
     * @return rgb array
     */
    private int[] rgbStringToArray(String s) {
        // Parse rgb from hex
        int red = Integer.parseInt(s.substring(0,2), 16);
        int green = Integer.parseInt(s.substring(2,4), 16);
        int blue = Integer.parseInt(s.substring(4,6), 16);
        // Return array
        return new int[]{red, green, blue};
    }
}
