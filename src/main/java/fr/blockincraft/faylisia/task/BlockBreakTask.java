package fr.blockincraft.faylisia.task;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.blocks.BlockType;
import fr.blockincraft.faylisia.blocks.CustomBlock;
import fr.blockincraft.faylisia.blocks.DiggingBlock;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task which send packets to update break animation and block states respawn
 */
public class BlockBreakTask extends BukkitRunnable {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static BlockBreakTask instance;

    /**
     * Initialize instance and start task
     */
    public static void startTask() {
        if (instance == null) {
            instance = new BlockBreakTask();
            instance.runTaskTimerAsynchronously(Faylisia.getInstance(), 20, 1);
        }
    }

    @Override
    public void run() {
        // Update block break animation
        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());
            if (customPlayer.getCanBreak() || player.getGameMode() == GameMode.CREATIVE) return;

            DiggingBlock block = customPlayer.getDiggingBlock();
            if (block == null) continue;

            int animState = 10;

            if (block.getBlock() != null && block.getBlock().getCurrentState() != null) {
                if (!customPlayer.checkCanBreakBlock(block, true)) {
                    customPlayer.setDiggingBlock(null);
                } else {
                    double miningSpeed = customPlayer.getStat(Stats.MINING_SPEED);

                    block.progress(miningSpeed);
                    if (block.getProgression() >= block.getBlock().getCurrentState().getDurability()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                            block.getBlock().breakBlock(player);
                            block.setProgression(0);
                        });
                    } else {
                        animState = (int) Math.round(9.0 / block.getBlock().getCurrentState().getDurability() * block.getProgression());
                    }
                }
            }

            PlayerUtils.setBlockBreakingState(player, block.getX(), block.getY(), block.getZ(), animState);
        }

        // Update block states
        for (CustomBlock block : registry.getBlocks()) {
            if (block.hasPreviousState()) {
                block.setTickSinceLastState(block.getTickSinceLastState() + 1);
                if (block.getPreviousState().getTickBeforeRespawn() <= block.getTickSinceLastState()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), block::regenBlock);
                }
            }
        }
    }
}
