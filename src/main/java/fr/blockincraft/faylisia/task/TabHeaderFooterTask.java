package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.displays.Tab;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that will be activated each tick to update {@link Tab} header and footer of all players
 */
public class TabHeaderFooterTask extends BukkitRunnable {
    private static TabHeaderFooterTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new TabHeaderFooterTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 1);
        }
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Tab.updateFooterAndHeader(p);
        }
    }
}
