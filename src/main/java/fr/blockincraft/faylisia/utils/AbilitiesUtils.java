package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomLivingEntity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public static List<CustomEntity> getEntitiesInRadius(@NotNull Location cLocation, double radius) {
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

    /**
     * Get all living custom entities in a radius
     * @param cLocation center radius location
     * @param radius radius
     * @return living custom entities in radius
     */
    @NotNull
    public static List<CustomLivingEntity> getLivingEntitiesInRadius(@NotNull Location cLocation, double radius) {
        return getEntitiesInRadius(cLocation, radius).stream().filter(customEntity -> customEntity instanceof CustomLivingEntity).map(customEntity -> (CustomLivingEntity) customEntity).toList();
    }
}
