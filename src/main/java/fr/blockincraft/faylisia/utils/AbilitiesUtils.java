package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods which can be util to create abilities faster
 */
public class AbilitiesUtils {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    /**
     * Get all custom entities in a radius
     * @param cLocation center radius location
     * @param radius radius
     * @return custom entities in radius
     */
    public static List<CustomEntity> getEntitiesInRadius(Location cLocation, double radius) {
        List<CustomEntity> entities = new ArrayList<>();

        registry.getEntities().forEach(customEntity -> {
            if (customEntity.getEntity() != null && customEntity.getEntity().isValid()) {
                // Check radius
                if (AreaUtils.isInRadius(cLocation, radius, customEntity.getEntity().getLocation())) {
                    entities.add(customEntity);
                }
            }
        });

        return entities;
    }
}
