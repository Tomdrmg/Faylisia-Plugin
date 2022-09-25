package fr.blockincraft.faylisia.map.shapes;

import org.bukkit.World;

/**
 * Rectangle shape
 */
public class Rectangle implements Shape {
    private final int bx;
    private final int bz;
    private final int sx;
    private final int sz;
    private final int sy;
    private final int by;
    private final boolean ignoreHeight;

    /**
     * Constructor where height isn't important
     * @param x1 first point x coordinate
     * @param z1 first point z coordinate
     * @param x2 second point x coordinate
     * @param z2 second point z coordinate
     */
    public Rectangle(int x1, int z1, int x2, int z2) {
        this.bx = Math.max(x1, x2);
        this.bz = Math.max(z1, z2);
        this.sx = Math.min(x1, x2);
        this.sz = Math.min(z1, z2);
        sy = 0;
        by = 0;
        ignoreHeight = true;
    }

    /**
     * Constructor where height is important
     * @param x1 first point x coordinate
     * @param z1 first point z coordinate
     * @param x2 second point x coordinate
     * @param z2 second point z coordinate
     * @param y1 first y point
     * @param y2 second y point
     */
    public Rectangle(int x1, int z1, int x2, int z2, int y1, int y2) {
        this.bx = Math.max(x1, x2);
        this.bz = Math.max(z1, z2);
        this.sx = Math.min(x1, x2);
        this.sz = Math.min(z1, z2);
        this.by = Math.max(y1, y2);
        this.sy = Math.min(y1, y2);
        ignoreHeight = false;
    }

    @Override
    public boolean contain(int x, int y, int z, World world) {
        // If we don't ignore height verify it
        if (!ignoreHeight) {
            if (y < sy) return false;
            if (y > by) return false;
        }

        // Check others coordinates
        return x <= bx && x >= sx && z <= bz && z >= sz;
    }
}
