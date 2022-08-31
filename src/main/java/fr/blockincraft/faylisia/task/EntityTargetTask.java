package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityTargetTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static EntityTargetTask instance;

    public static void startTask() {
        if (instance == null) {
            instance = new EntityTargetTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 1);
        }
    }

    @Override
    public void run() {
        for (CustomEntity entity : registry.getEntities()) {
            if (!entity.getEntity().isValid() || entity.getEntity() == null) {
                registry.removeEntity(entity);
            } else {
                if (entity.getEntity() instanceof Mob mob) {
                    if (mob.getTarget() instanceof Player player) {
                        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                            mob.setTarget(null);
                            continue;
                        }

                        double x1 = mob.getLocation().getX();
                        double z1 = mob.getLocation().getZ();
                        double x2 = player.getLocation().getX();
                        double z2 = player.getLocation().getZ();

                        double d = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));

                        if (d > 20) {
                            mob.setTarget(null);
                        }
                    }
                }
            }
        }
    }
}
