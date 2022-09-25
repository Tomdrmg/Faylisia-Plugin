package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomEntityType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
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
            if (!entity.getEntity().isValid()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> registry.removeEntity(entity));
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
            for (World world : Bukkit.getOnlinePlayers().stream().map(Player::getWorld).toList()) {
                for (Entity entity : world.getEntities().stream().filter(entity -> entity.getPersistentDataContainer().has(CustomEntityType.idKey, PersistentDataType.STRING)).toList()) {
                    if (entity instanceof Player) continue;
                    if (!registry.getEntities().stream().map(CustomEntity::getEntity).map(Entity::getUniqueId).toList().contains(entity.getUniqueId())) entity.remove();
                }
            }
        });
    }
}
