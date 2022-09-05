package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.displays.ScoreboardManager;
import fr.blockincraft.faylisia.displays.Tab;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that will be activated each tick to update {@link ScoreboardManager} for all players
 */
public class ScoreboardRefreshTask extends BukkitRunnable {
    private static final ScoreboardManager manager = Faylisia.getInstance().getScoreBoardManager();
    private static ScoreboardRefreshTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new ScoreboardRefreshTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 1);
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (manager.hasScoreboard(player)) {
                manager.updateScoreboard(player);
            }
        }
    }
}
