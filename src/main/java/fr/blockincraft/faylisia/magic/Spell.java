package fr.blockincraft.faylisia.magic;

import fr.blockincraft.faylisia.Faylisia;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Spell {
    private final SpellType type;
    private final SpellParam<?>[] params;
    private final Player player;
    private final int x;
    private final int y;
    private final int z;
    private final World world;
    private final Listener listener;
    private final BukkitRunnable runnable;
    private boolean cancelled = false;
    private long tick = 0;

    public Spell(@NotNull SpellType spellType, SpellParam<?>[] params, Player player) {
        this.type = spellType;
        this.params = params;
        this.player = player;
        this.x = player.getLocation().getBlockX();
        this.y = player.getLocation().getBlockY();
        this.z = player.getLocation().getBlockZ();
        this.world = player.getWorld();
        this.listener = new Listener() {
            @EventHandler
            public void playerQuit(PlayerQuitEvent e) {
                if (spellType.isCancelOnQuit() && e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    cancel();
                }
            }

            @EventHandler
            public void playerChangeWorld(PlayerChangedWorldEvent e) {
                if (spellType.isCancelOnChangeWorld() && e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    cancel();
                }
            }

            @EventHandler
            public void playerAttack(EntityDamageByEntityEvent e) {
                if (spellType.isCancelOnDamage() && e.getDamager().getUniqueId().equals(player.getUniqueId())) {
                    cancel();
                }
            }

            @EventHandler
            public void playerTakeDamage(EntityDamageByEntityEvent e) {
                if (spellType.isCancelOnTakeDamage() && e.getEntity().getUniqueId().equals(player.getUniqueId())) {
                    cancel();
                }
            }

            @EventHandler
            public void playerMove(PlayerMoveEvent e) {
                if (spellType.isCancelOnMove() && e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    cancel();
                }
            }
        };
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Map<Class<?>, List<Object>> spellParams = new HashMap<>();
                for (SpellParam<?> param : params) {
                    if (!spellParams.containsKey(param.getType())) {
                        spellParams.put(param.getType(), new ArrayList<>());
                    }

                    spellParams.get(param.getType()).add(param.getValue());
                }

                if (tick >= type.getTickDuration()) {
                    type.getAction().end(Spell.this.player, x, y, z, world, spellParams);
                    runnable.cancel();
                    return;
                }

                if (type.getFrames().displayFrame(tick, x , y, z, world, Spell.this.player, spellParams)) {
                    cancel();
                }
                tick++;
            }
        };
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(listener, Faylisia.getInstance());
        this.runnable.runTaskTimer(Faylisia.getInstance(), 0, 1);
    }

    public void cancel() {
        this.cancelled = true;
    }

    public SpellType getType() {
        return type;
    }

    public SpellParam<?>[] getParams() {
        return params;
    }

    public Player getPlayer() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public Listener getListener() {
        return listener;
    }

    public BukkitRunnable getRunnable() {
        return runnable;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public long getTick() {
        return tick;
    }

    public static class SpellParam<T> {
        private final Class<T> type;
        private final Callable<? extends T> value;
        private final T defaultValue;

        public SpellParam(Class<T> type, Callable<? extends T> value, T defaultValue) {
            this.type = type;
            this.value = value;
            this.defaultValue = defaultValue;
        }

        public Class<T> getType() {
            return type;
        }

        public T getValue() {
            try {
                return value.call();
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }
}
