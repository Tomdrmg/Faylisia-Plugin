package fr.blockincraft.faylisia.map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Spawn vars
 */
public class Spawn {
    // Spawn coordinates
    public static final int x = 0;
    public static final int y = 100;
    public static final int z = -28;

    /**
     * Teleport a player to spawn
     * @param player player to teleport
     */
    public static void teleportToSpawn(Player player) {
        player.teleport(new Location(player.getWorld(), x, y, z));
    }
}
