package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.items.Items;
import fr.blockincraft.faylisia.map.Regions;
import org.bukkit.entity.EntityType;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

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

    static {
        coolZombie.register();
    }

    public static final Map<EntitySpawnLocation, CustomEntity> spawnLocations = new HashMap<>();

    static {
        spawnLocations.put(new EntitySpawnLocation(10, 42, 10, coolZombie), null);
        spawnLocations.put(new EntitySpawnLocation(-10, 42, 10, coolZombie), null);
        spawnLocations.put(new EntitySpawnLocation(10, 42, -10, coolZombie), null);
        spawnLocations.put(new EntitySpawnLocation(-10, 42, -10, coolZombie), null);
    }
}
