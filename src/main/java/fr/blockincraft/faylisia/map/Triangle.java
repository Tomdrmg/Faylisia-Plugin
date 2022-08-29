package fr.blockincraft.faylisia.map;

import org.bukkit.World;

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

    public double area(int x1, int z1, int x2, int z2, int x3, int z3) {
        return Math.abs((x1 * (z2 - z3) + x2 * (z3 - z1) + x3 * (z1 - z2)) / 2.0);
    }

    @Override
    public boolean contain(int x, int y, int z, World world) {
        if (!ignoreHeight) {
            if (y < sy) return false;
            if (y > by) return false;
        }

        double A = area(x1, z1, x2, z2, x3, z3);
        double A1 = area(x, z, x2, z2, x3, z3);
        double A2 = area(x1, z1, x, z, x3, z3);
        double A3 = area(x1, z1, x2, z2, x, z);

        return A == A1 + A2 + A3;
    }
}
