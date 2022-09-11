package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.utils.AreaUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that will be activated each tick to update {@link CustomEntity} target <br/>
 * Because of mobs continue to target creative/spectator players and to place a limit to 20 blocks distance
 */
public class EntityTargetTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static EntityTargetTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new EntityTargetTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 1);
        }
    }

    @Override
    public void run() {
        // Retrieve all custom entities
        for (CustomEntity entity : registry.getEntities()) {
            // Remove them if they are removed
            if (entity.getEntity() == null || !entity.getEntity().isValid()) {
                registry.removeEntity(entity);
            } else {
                if (entity.getEntity() instanceof Mob mob) {
                    // Only continue if target is a player because we prevent mob to
                    // target others mobs in GameListeners
                    if (mob.getTarget() instanceof Player player) {
                        // Remove target if player was in creative or spectator
                        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                            mob.setTarget(null);
                            continue;
                        }

                        // Check distance
                        if (!AreaUtils.isInRadius(mob.getLocation(), 20.0, player.getLocation())) {
                            mob.setTarget(null);
                        }
                    }
                }
            }
        }
    }
}
