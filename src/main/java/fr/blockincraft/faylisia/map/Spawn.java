package fr.blockincraft.faylisia.map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Spawn {
    public static final int x = 0;
    public static final int y = 42;
    public static final int z = 0;

    public static void teleportToSpawn(Player player) {
        player.teleport(new Location(player.getWorld(), x, y, z));
    }
}
