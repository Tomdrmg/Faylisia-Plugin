package fr.blockincraft.faylisia.listeners;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.Entities;
import fr.blockincraft.faylisia.entity.EntitySpawnLocation;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.map.Spawn;
import fr.blockincraft.faylisia.menu.CraftingMenu;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.displays.Tab;
import fr.blockincraft.faylisia.player.permission.Ranks;
import fr.blockincraft.faylisia.utils.FileUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.time.Instant;
import java.util.*;

public class GameListeners implements Listener {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @EventHandler
    public void handleLogin(PlayerLoginEvent e) {
        if (!Bukkit.getWhitelistedPlayers().contains(e.getPlayer()) && Bukkit.hasWhitelist()) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Faylisia.development ? Messages.NO_JOIN_IN_DEV.get() : Messages.NO_JOIN_IN_MAINTENANCE.get());
        } else if (!Faylisia.isInitialized()) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Messages.NO_JOIN_DURING_STARTING.get());
        }
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent e) {
        try {
            e.getPlayer().setResourcePack(
                    "http://faylis.xyz:11342/resource_pack", FileUtils.calcSHA1(FileUtils.getResourcePack()), true
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%player_name%", e.getPlayer().getName());
        parameters.put("%player_display_name%", e.getPlayer().getDisplayName());

        e.setJoinMessage(Messages.PLAYER_JOIN_MESSAGE.get(parameters));

        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
        customPlayer.refreshStats();
        customPlayer.setEffectiveHealth(customPlayer.getMaxEffectiveHealth());
        Ranks.applyPermissions(e.getPlayer(), customPlayer.getRank());

        registry.refreshItems(e.getPlayer().getInventory());
        if (!Faylisia.getInstance().getScoreBoardManager().hasScoreboard(e.getPlayer())) {
            Faylisia.getInstance().getScoreBoardManager().createScoreboard(e.getPlayer());
        }

        Tab.initPlayersTabPartFor(e.getPlayer());
        Tab.initStatsPartFor(e.getPlayer());
        Tab.refreshStatsPartFor(e.getPlayer());
        Tab.initGuildPartFor(e.getPlayer());
        Tab.refreshGuildPartFor(e.getPlayer());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId() != e.getPlayer().getUniqueId()) {
                Tab.refreshPlayersInTabFor(player);
            }
        }
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent e) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%player_name%", e.getPlayer().getName());
        parameters.put("%player_display_name%", e.getPlayer().getDisplayName());

        e.setQuitMessage(Messages.PLAYER_LEAVE_MESSAGE.get(parameters));

        Faylisia.getInstance().getRegistry().refreshItems(e.getPlayer().getInventory());

        Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId() != e.getPlayer().getUniqueId()) {
                    Tab.refreshPlayersInTabFor(player);
                }
            }
        }, 1);
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent e) {
        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
        if (customPlayer.getCanBreak()) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent e) {
        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
        if (customPlayer.getCanBreak()) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void handleRedstoneUse(BlockRedstoneEvent e) {
        e.setNewCurrent(0);
    }

    @EventHandler
    public void handlePiston(BlockPistonExtendEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handlePiston(BlockPistonRetractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleDispenser(BlockDispenseEvent e) {
        e.setCancelled(true);
    }

    public static void handleRespawn(Player player) {
        CustomPlayerDTO pl = registry.getOrRegisterPlayer(player.getUniqueId());
        pl.onRespawn();
    }

    public static void handleDeath(Player player) {
        CustomPlayerDTO pl = registry.getOrRegisterPlayer(player.getUniqueId());
        pl.onDied();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("%player_name%", player.getName());
        parameters.put("%player_displayname%", player.getDisplayName());

        player.sendMessage(Messages.YOU_ARE_DIED.get(parameters));

        Spawn.teleportToSpawn(player);
        handleRespawn(player);
    }

    @EventHandler
    public void handleMove(PlayerMoveEvent e) {
        Location plLoc = e.getPlayer().getLocation();
        for (Map.Entry<EntitySpawnLocation, CustomEntity> entry : Entities.spawnLocations.entrySet()) {
            if (Date.from(Instant.now()).getTime() - (entry.getKey().getLastKill() + (entry.getKey().getType().getTickBeforeRespawn() < 0 ? 0 : entry.getKey().getType().getTickBeforeRespawn() * 50)) > 0) {
                if (entry.getValue() == null || !entry.getValue().getEntity().isValid()){
                    int x = entry.getKey().getX() - plLoc.getBlockX();
                    int y = entry.getKey().getY() - plLoc.getBlockY();
                    int z = entry.getKey().getZ() - plLoc.getBlockZ();

                    double d1 = Math.sqrt(x * x + z * z);
                    double d2 = Math.sqrt(x * x + y * y);
                    double d3 = Math.sqrt(z * z + y * y);

                    if (d1 <= 75 && d2 <= 75 && d3 <= 75) {
                        Entities.spawnLocations.put(entry.getKey(), entry.getKey().getType().spawn(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()));
                    }
                }
            }
        }

        if (e.getTo() == null) return;

        Region from = registry.getRegionAt(e.getFrom());
        Region to = registry.getRegionAt(e.getTo());

        if (from != to) {
            CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());

            customPlayer.refreshStats();
        }
    }

    @EventHandler
    public void handleTarget(EntityTargetEvent e) {
        if (!(e.getTarget() instanceof Player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void handleDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (e.getEntity() instanceof Player player) {
                handleDeath(player);
                e.setCancelled(true);
            }
            return;
        }

        EntityDamageEvent.DamageCause cause = e.getCause();

        switch (cause) {
            case FALL, DROWNING, SUFFOCATION -> {
                e.setCancelled(true);
                return;
            }
        }

        if (e instanceof EntityDamageByEntityEvent subE) {
            if (subE.getDamager() instanceof Player player && !(subE.getEntity() instanceof Player)) {
                CustomEntity entity = registry.getCustomEntityByEntity(subE.getEntity());
                if (entity == null) {
                    e.setCancelled(true);
                    return;
                }

                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());
                boolean critic = customPlayer.generateCritical();
                long damage = Math.round(customPlayer.getDamage(critic));

                Handlers mainHandHandlers = customPlayer.getMainHandHandler();
                Handlers[] armorSetHandlers = customPlayer.getArmorSetHandlers();
                Handlers[] armorSlotHandlers = customPlayer.getArmorSlotHandlers();
                Handlers[] othersHandlers = customPlayer.getOthersHandlers();

                if (mainHandHandlers != null) damage = mainHandHandlers.onDamage(player, entity, damage, true, false);
                for (Handlers handlers : armorSetHandlers) {
                    damage = handlers.onDamage(player, entity, damage, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    damage = handlers.onDamage(player, entity, damage, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    damage = handlers.onDamage(player, entity, damage, false, false);
                }

                PlayerUtils.spawnDamageIndicator(damage, critic, player, subE.getEntity().getLocation());
                entity.takeDamage(damage, player);
            } else if (subE.getEntity() instanceof Player player && !(subE.getDamager() instanceof Player)) {
                CustomEntity entity = registry.getCustomEntityByEntity(subE.getDamager());
                if (entity == null) {
                    e.setCancelled(true);
                    return;
                }

                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());
                customPlayer.takeDamage(entity.getDamageFor(player), entity);
            }
        }

        e.setDamage(0.0);
    }

    @EventHandler
    public void handleDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        Item item = e.getItemDrop();

        item.setOwner(player.getUniqueId());
    }

    @EventHandler
    public void handlePickup(EntityPickupItemEvent e) {
        if (e.getItem().getOwner() != e.getEntity().getUniqueId()) e.setCancelled(true);
    }

    @EventHandler
    public void handleAdvancementDone(PlayerAdvancementDoneEvent e) {

    }

    @EventHandler
    public void handleInteraction(PlayerInteractEvent e) {
        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
        if (customPlayer.getCanBreak()) {
            return;
        }

        Handlers mainHandHandlers = customPlayer.getMainHandHandler();
        Handlers[] armorSetHandlers = customPlayer.getArmorSetHandlers();
        Handlers[] armorSlotHandlers = customPlayer.getArmorSlotHandlers();
        Handlers[] othersHandlers = customPlayer.getOthersHandlers();

        boolean isRightClick = e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (mainHandHandlers != null) mainHandHandlers.onInteract(e.getPlayer(), e.getClickedBlock(), true, false, isRightClick, e.getHand());
        for (Handlers handlers : armorSetHandlers) {
            handlers.onInteract(e.getPlayer(), e.getClickedBlock(), false, true, isRightClick, e.getHand());
        }
        for (Handlers handlers : armorSlotHandlers) {
            handlers.onInteract(e.getPlayer(), e.getClickedBlock(), false, true, isRightClick, e.getHand());
        }
        for (Handlers handlers : othersHandlers) {
            handlers.onInteract(e.getPlayer(), e.getClickedBlock(), false, false, isRightClick, e.getHand());
        }

        if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CRAFTING_TABLE) {
            new CraftingMenu().open(e.getPlayer());
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void handleEntityInteraction(PlayerInteractEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleSpawnerSpawning(SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @EventHandler
    public void handleCreatureSpawn(CreatureSpawnEvent e) {
        if (cancellableReason.contains(e.getSpawnReason())) {
            e.setCancelled(true);
        }
    }

    List<CreatureSpawnEvent.SpawnReason> cancellableReason = Arrays.asList(
            CreatureSpawnEvent.SpawnReason.BEEHIVE,
            CreatureSpawnEvent.SpawnReason.BREEDING,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            CreatureSpawnEvent.SpawnReason.BUILD_WITHER,
            CreatureSpawnEvent.SpawnReason.DISPENSE_EGG,
            CreatureSpawnEvent.SpawnReason.EGG,
            CreatureSpawnEvent.SpawnReason.ENDER_PEARL,
            CreatureSpawnEvent.SpawnReason.SLIME_SPLIT,
            CreatureSpawnEvent.SpawnReason.SPELL,
            CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE,
            CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION,
            CreatureSpawnEvent.SpawnReason.DROWNED,
            CreatureSpawnEvent.SpawnReason.INFECTION,
            CreatureSpawnEvent.SpawnReason.LIGHTNING,
            CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK,
            CreatureSpawnEvent.SpawnReason.SHEARED,
            CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
            CreatureSpawnEvent.SpawnReason.TRAP,
            CreatureSpawnEvent.SpawnReason.PIGLIN_ZOMBIFIED
    );

    @EventHandler
    public void handleResurrection(EntityResurrectEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleEntityBurnCauseOfDay(EntityCombustEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleInventoryChange(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                CustomPlayerDTO player = registry.getOrRegisterPlayer(p.getUniqueId());
                player.refreshStats();
            });
        }
    }

    @EventHandler
    public void handleInventoryChange(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player p) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                CustomPlayerDTO player = registry.getOrRegisterPlayer(p.getUniqueId());
                player.refreshStats();
                Tab.refreshStatsPartFor(p);
            });
        }
    }

    @EventHandler
    public void handleInventoryChange(PlayerDropItemEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
            CustomPlayerDTO player = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
            player.refreshStats();
            Tab.refreshStatsPartFor(e.getPlayer());
        }, 1);
    }

    @EventHandler
    public void handleInventoryChange(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                CustomPlayerDTO player = registry.getOrRegisterPlayer(p.getUniqueId());
                player.refreshStats();
                Tab.refreshStatsPartFor(p);
            }, 1);
        }
    }

    @EventHandler
    public void handleInventoryChange(PlayerItemHeldEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
            CustomPlayerDTO player = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
            player.refreshStats();
            Tab.refreshStatsPartFor(e.getPlayer());
        }, 1);
    }

    @EventHandler
    public void handleInventoryChange(PlayerSwapHandItemsEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
            CustomPlayerDTO player = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
            player.refreshStats();
            Tab.refreshStatsPartFor(e.getPlayer());
        }, 1);
    }

    @EventHandler
    public void handleHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleFallingBlock(EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof FallingBlock) {
            e.setCancelled(true);
        }
    }
}
