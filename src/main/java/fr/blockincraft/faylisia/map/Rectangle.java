package fr.blockincraft.faylisia.map;

import org.bukkit.World;

public class Rectangle implements Shape {
    private final int bx;
    private final int bz;
    private final int sx;
    private final int sz;
    private final int sy;
    private final int by;
    private final boolean ignoreHeight;

    public Rectangle(int x1, int z1, int x2, int z2) {
        this.bx = Math.max(x1, x2);
        this.bz = Math.max(z1, z2);
        this.sx = Math.min(x1, x2);
        this.sz = Math.min(z1, z2);
        sy = 0;
        by = 0;
        ignoreHeight = true;
    }

    public Rectangle(int x1, int z1, int x2, int z2, int y1, int y2) {
        this.bx = Math.max(x1, x2);
        this.bz = Math.max(z1, z2);
        this.sx = Math.min(x1, x2);
        this.sz = Math.min(z1, z2);
        this.by = Math.max(y1, y2);
        this.sy = Math.min(y1, y2);
        ignoreHeight = false;
    }

    public int getBx() {
        return bx;
    }

    public int getBz() {
        return bz;
    }

    public int getSx() {
        return sx;
    }

    public int getSz() {
        return sz;
    }

    @Override
    public boolean contain(int x, int y, int z, World world) {
        if (!ignoreHeight) {
            if (y < sy) return false;
            if (y > by) return false;
        }

        return x <= bx && x >= sx && z <= bz && z >= sz;
    }
}
