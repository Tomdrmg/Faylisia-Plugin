package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityRemoveTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static EntityRemoveTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new EntityRemoveTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 5);
        }
    }

    @Override
    public void run() {
        for (CustomEntity entity : registry.getEntities()) {
            if (!entity.getEntity().isValid() || entity.getEntity() == null) {
                registry.removeEntity(entity);
            }
        }
    }
}
