package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;
import org.bukkit.World;

import java.awt.image.BufferedImage;

public class ColorRegion extends Region {
    private static final BufferedImage image = Faylisia.getInstance().getRegionsImage();
    private int color = 0x00000000;

    public ColorRegion(String id, String name) {
        super(id, name);
    }

    @Override
    public boolean isIn(int x, int y, int z, World world) {
        if (image == null) return false;
        int imageX = x + image.getWidth() / 2;
        int imageY = z + image.getHeight() / 2;

        if (imageX < 0 || imageX >= image.getWidth()) return false;
        if (imageY < 0 || imageY >= image.getHeight()) return false;

        return image.getRGB(imageX, imageY) == color;
    }

    public int getColor() {
        return color;
    }

    /**
     * Change region color
     * @param color new value
     * @return this instance
     */
    public ColorRegion setColor(int color) {
        if (isRegistered()) throw new ChangeRegisteredRegion();
        this.color = color;
        return this;
    }
}
