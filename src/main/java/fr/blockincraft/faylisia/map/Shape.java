package fr.blockincraft.faylisia.map;

import org.bukkit.Location;
import org.bukkit.World;

public interface Shape {
    boolean contain(int x, int y, int z, World world);

    default boolean contain(Location location) {
        return contain(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }
}
