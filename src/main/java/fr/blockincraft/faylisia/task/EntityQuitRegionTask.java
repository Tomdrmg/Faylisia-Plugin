package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomLivingEntity;
import fr.blockincraft.faylisia.map.Region;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Date;
import java.time.Instant;

/**
 * Task that will be activated all two seconds to respawn {@link CustomEntity} when they are leaved their {@link Region}
 */
public class EntityQuitRegionTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static EntityQuitRegionTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new EntityQuitRegionTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 40);
        }
    }

    @Override
    public void run() {
        // Retrieve all custom entities
        for (CustomEntity entity : registry.getEntities()) {
            // Only respawn if they aren't be hit in the last five seconds
            if (entity.getEntityType().getRegion() != registry.getRegionAt(entity.getEntity().getLocation()) && (!(entity instanceof CustomLivingEntity living) || Date.from(Instant.now()).getTime() - living.getLastDamage() > 5000)) {
                entity.teleportToSpawn();
            }
        }
    }
}
