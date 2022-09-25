package fr.blockincraft.faylisia.map.shapes;

import org.bukkit.World;

import javax.annotation.Nullable;

/**
 * Triangle shape
 */
public class Triangle implements Shape {
    private final int x1;
    private final int z1;
    private final int x2;
    private final int z2;
    private final int x3;
    private final int z3;
    private final int by;
    private final int sy;
    private final boolean ignoreHeight;

    /**
     * Constructor where height isn't important
     * @param x1 first point x coordinate
     * @param z1 first point z coordinate
     * @param x2 second point x coordinate
     * @param z2 second point z coordinate
     * @param x3 third point x coordinate
     * @param z3 third point z coordinate
     */
    public Triangle(int x1, int z1, int x2, int z2, int x3, int z3) {
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.x3 = x3;
        this.z3 = z3;
        this.sy = 0;
        this.by = 0;
        this.ignoreHeight = true;
    }

    /**
     * Constructor where height is important
     * @param x1 first point x coordinate
     * @param z1 first point z coordinate
     * @param x2 second point x coordinate
     * @param z2 second point z coordinate
     * @param x3 third point x coordinate
     * @param z3 third point z coordinate
     * @param y1 first y point
     * @param y2 second y point
     */
    public Triangle(int x1, int z1, int x2, int z2, int x3, int z3, int y1, int y2) {
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.x3 = x3;
        this.z3 = z3;
        this.sy = Math.min(y1, y2);
        this.by = Math.max(y1, y2);
        this.ignoreHeight = false;
    }

    /**
     * This method calculate air of a triangle
     * @param x1 first x
     * @param z1 first z
     * @param x2 second x
     * @param z2 second z
     * @param x3 third x
     * @param z3 third z
     * @return air of the triangle
     */
    public double area(int x1, int z1, int x2, int z2, int x3, int z3) {
        return Math.abs((x1 * (z2 - z3) + x2 * (z3 - z1) + x3 * (z1 - z2)) / 2.0);
    }

    @Override
    public boolean contain(int x, int y, int z, @Nullable World world) {
        // If we don't ignore height verify it
        if (!ignoreHeight) {
            if (y < sy) return false;
            if (y > by) return false;
        }

        // Check others coordinates
        double A = area(x1, z1, x2, z2, x3, z3);
        double A1 = area(x, z, x2, z2, x3, z3);
        double A2 = area(x1, z1, x, z, x3, z3);
        double A3 = area(x1, z1, x2, z2, x, z);

        return A == A1 + A2 + A3;
    }
}
