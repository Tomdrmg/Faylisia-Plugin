package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.items.Items;
import fr.blockincraft.faylisia.map.Regions;
import org.bukkit.entity.EntityType;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * All {@link CustomEntityType} are created here, to get an entity type just call his instance here <br/>
 * We also register and set their locations here
 */
public class Entities {
    public static final CustomEntityType coolZombie = new CustomEntityType(EntityType.ZOMBIE, "cool_zombie", 2000, 20)
            .setName("&dCool Zombie")
            .setRank(EntitiesRanks.D)
            .setRegion(Regions.SPAWN)
            .setTickBeforeRespawn(600)
            .setLoots(
                    new Loot(1, Items.coolDiamond, 1, 1, () -> {
                        return new SecureRandom().nextInt(2) + 1;
                    }),
                    new Loot(1, Items.coolDiamondBlock, 1, 18, () -> {
                        return 1;
                    })
            );

    // Register all entities
    static {
        coolZombie.register();
    }

    // Store all spawn locations
    public static final Map<EntitySpawnLocation, CustomEntity> spawnLocations = new HashMap<>();

    // Initialize all spawn locations
    // Only put mobs that can respawn and that spawn naturally
    static {
        spawnLocations.put(new EntitySpawnLocation(10, 42, 10, coolZombie), null);
        spawnLocations.put(new EntitySpawnLocation(-10, 42, 10, coolZombie), null);
        spawnLocations.put(new EntitySpawnLocation(10, 42, -10, coolZombie), null);
        spawnLocations.put(new EntitySpawnLocation(-10, 42, -10, coolZombie), null);
    }
}
