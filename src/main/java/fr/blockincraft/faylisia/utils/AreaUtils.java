package fr.blockincraft.faylisia.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class AreaUtils {
    /**
     * Check if a position is in a radius
     * @param cx center of radius x coordinate
     * @param cy center of radius y coordinate
     * @param cz center of radius z coordinate
     * @param radius radius
     * @param px position to check x coordinate
     * @param py position to check y coordinate
     * @param pz position to check z coordinate
     * @return if position is in radius
     */
    public static boolean isInRadius(double cx, double cy, double cz, double radius, double px, double py, double pz) {
        // Calculate coordinates with player at center
        double x = px - cx;
        double y = py - cy;
        double z = pz - cz;

        // Calculate distance
        double d1 = Math.sqrt(x * x + z * z);
        double d2 = Math.sqrt(x * x + y * y);
        double d3 = Math.sqrt(z * z + y * y);

        // Check if in radius
        return d1 <= radius && d2 <= radius && d3 <= radius;
    }

    /**
     * Check if a position is in a radius and also check world
     * @param cLocation center of radius
     * @param radius radius
     * @param pLocation position to check
     * @return if position is in radius
     */
    public static boolean isInRadius(@NotNull Location cLocation, double radius, @NotNull Location pLocation) {
        if (cLocation.getWorld() == null || pLocation.getWorld() == null) return false;
        if (cLocation.getWorld().getUID() != pLocation.getWorld().getUID()) return false;

        return isInRadius(cLocation.getX(), cLocation.getY(), cLocation.getZ(), radius, pLocation.getX(), pLocation.getY(), pLocation.getZ());
    }
}
