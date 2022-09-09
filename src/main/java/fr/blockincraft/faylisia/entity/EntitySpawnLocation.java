package fr.blockincraft.faylisia.entity;

import org.jetbrains.annotations.NotNull;

/**
 * A custom entity spawn location, used to spawn an entity if a player is near
 */
public class EntitySpawnLocation {
    private final int x;
    private final int y;
    private final int z;
    private final CustomEntityType type;
    private long lastKill = 0;

    /**
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param type custom entity type
     */
    public EntitySpawnLocation(int x, int y, int z, @NotNull CustomEntityType type) {
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

    @NotNull
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
