package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Date;
import java.time.Instant;

public class EntityQuitRegionTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static EntityQuitRegionTask instance;

    public static void startTask() {
        if (instance == null) {
            instance = new EntityQuitRegionTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 40);
        }
    }

    @Override
    public void run() {
        for (CustomEntity entity : registry.getEntities()) {
            if (entity.getEntityType().getRegion() != registry.getRegionAt(entity.getEntity().getLocation()) &&
                    Date.from(Instant.now()).getTime() - entity.getLastDamage() > 5000) {
                entity.teleportToSpawn();
            }
        }
    }
}
