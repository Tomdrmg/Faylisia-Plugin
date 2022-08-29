package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.displays.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardRefreshTask extends BukkitRunnable {
    private static final ScoreboardManager manager = Faylisia.getInstance().getScoreBoardManager();
    private static ScoreboardRefreshTask instance;

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
