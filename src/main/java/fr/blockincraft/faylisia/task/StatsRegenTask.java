package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that will be activated each seconds to regen health and magical power of all online players
 */
public class StatsRegenTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static StatsRegenTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new StatsRegenTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 20);
        }
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO player = registry.getOrRegisterPlayer(p.getUniqueId());

            player.regenHealth();
            player.regenMagicalPower();
        }
    }
}
