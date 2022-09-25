package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.entity.interaction.HostileMobEntityType;
import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.Items;
import fr.blockincraft.faylisia.items.event.DamageType;
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
    public static final HostileMobEntityType coolZombie = (HostileMobEntityType) new HostileMobEntityType(EntityType.ZOMBIE, "cool_zombie")
            .setDamage(20)
            .setDamageType(DamageType.MELEE_DAMAGE)
            .setLevel(30)
            .setMaxHealth(2000)
            .setLoots(
                    new Loot(1, new CustomItemStack(Items.coolDiamond, 1), 1, 1, () -> {
                        return new SecureRandom().nextInt(2) + 1;
                    }, Loot.LootType.MOB),
                    new Loot(1, new CustomItemStack(Items.coolDiamond, 1), 1, 18, () -> {
                        return 1;
                    }, Loot.LootType.MOB)
            )
            .setName("&dCool Zombie")
            .setRegion(Regions.CITE)
            .setTickBeforeRespawn(200);

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
