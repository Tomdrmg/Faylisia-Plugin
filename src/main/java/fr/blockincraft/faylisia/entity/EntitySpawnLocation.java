package fr.blockincraft.faylisia.entity;

public class EntitySpawnLocation {
    private final int x;
    private final int y;
    private final int z;
    private final CustomEntityType type;
    private long lastKill = 0;

    public EntitySpawnLocation(int x, int y, int z, CustomEntityType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public CustomEntityType getType() {
        return type;
    }

    public long getLastKill() {
        return lastKill;
    }

    public void setLastKill(long lastKill) {
        this.lastKill = lastKill;
    }
}
