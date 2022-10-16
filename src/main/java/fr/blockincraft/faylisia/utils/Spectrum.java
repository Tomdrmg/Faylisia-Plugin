package fr.blockincraft.faylisia.utils;

public class Spectrum {
    private final int length;
    private final int[] colorStart;
    private final int[] colorEnd;

    public Spectrum(int length, int colorStart, int colorEnd) {
        this.length = length;
        this.colorStart = new int[]{(colorStart >> 16) & 255, (colorStart >> 8) & 255, colorStart & 255};
        this.colorEnd = new int[]{(colorEnd >> 16) & 255, (colorEnd >> 8) & 255, colorEnd & 255};
    }

    public int colorAt(int index) {
        if (index < 0 || index > length) throw new IndexOutOfBoundsException("Color index must be between 0 and " + length + "!");
        return (calcChannel(index, colorStart[0], colorEnd[0]) << 16) | (calcChannel(index, colorStart[1], colorEnd[1]) << 8) | calcChannel(index, colorStart[2], colorEnd[2]);
    }

    private int calcChannel(int index, int chanelStart, int channelEnd) {
        return (channelEnd - chanelStart) / length * index;
    }

    public int getLength() {
        return length;
    }

    public int getColorStart() {
        return (colorStart[0] << 16) | (colorStart[1] << 8) | colorStart[2];
    }

    public int getColorEnd() {
        return (colorEnd[0] << 16) | (colorEnd[1] << 8) | colorEnd[2];
    }
}
