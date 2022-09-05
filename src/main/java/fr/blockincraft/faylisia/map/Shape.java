package fr.blockincraft.faylisia.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Different shapes to create {@link Region} area and know if player is in it
 */
public interface Shape {
    /**
     * Check if a point is in
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param world world in
     * @return if point is in
     */
    boolean contain(int x, int y, int z, @Nullable World world);

    /**
     * Check if a point is in
     * @param location point to check
     * @return if point is in
     */
    default boolean contain(@NotNull Location location) {
        return contain(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }
}
