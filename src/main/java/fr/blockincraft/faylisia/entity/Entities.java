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
    // Register all entities
    static {

    }

    // Store all spawn locations
    public static final Map<EntitySpawnLocation, CustomEntity> spawnLocations = new HashMap<>();

    // Initialize all spawn locations
    // Only put mobs that can respawn and that spawn naturally
    static {

    }
}
