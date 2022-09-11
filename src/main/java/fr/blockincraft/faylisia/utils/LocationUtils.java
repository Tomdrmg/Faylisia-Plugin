package fr.blockincraft.faylisia.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All methods used to serialize/deserialize x, y and z coordinates as/from long
 */
public class LocationUtils {
    /**
     * Make a {@link Long} containing all coordinates of a bukkit {@link Location}
     * @param location location
     * @return coordinates as long
     */
    public static long locationFromBukkit(@NotNull Location location) {
        return locationFromCoordinates(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Make a {@link Long} containing all coordinates
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return coordinates as long
     */
    public static long locationFromCoordinates(int x, int y, int z) {
        return (long) (x & 67108863) << 38 | (long) (z & 67108863) << 12 | (long) (y & 4095);
    }

    /**
     * Retrieve a bukkit {@link Location} with a world equal to {@code null} from a {@link Long} containing all coordinates
     * @param location coordinates as long
     * @return bukkit {@link Location}
     */
    @NotNull
    public static Location getAsBukkit(long location) {
        return getAsBukkit(location, null);
    }

    /**
     * Retrieve a bukkit {@link Location} in the specified world from a {@link Long} containing all coordinates
     * @param location coordinates as long
     * @param world world of the location
     * @return bukkit {@link Location}
     */
    @NotNull
    public static Location getAsBukkit(long location, @Nullable World world) {
        return new Location(world, getX(location), getY(location), getZ(location));
    }

    /**
     * Retrieve x coordinate from a {@link Long} containing all coordinates
     * @param location coordinates as long
     * @return x coordinate
     */
    public static int getX(long location) {
        return (int) (location << 26 >> 38);
    }

    /**
     * Retrieve y coordinate from a {@link Long} containing all coordinates
     * @param location coordinates as long
     * @return y coordinate
     */
    public static int getY(long location) {
        return (int) (location >> 38);
    }

    /**
     * Retrieve z coordinate from a {@link Long} containing all coordinates
     * @param location coordinates as long
     * @return z coordinate
     */
    public static int getZ(long location) {
        int y = (int) (location & 4095L);
        return y >= 4000 ? y - 4096 : y;
    }
}
