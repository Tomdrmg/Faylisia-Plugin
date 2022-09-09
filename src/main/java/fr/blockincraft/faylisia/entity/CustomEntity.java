package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.time.Instant;

public class CustomEntity {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final NamespacedKey idKey = new NamespacedKey(Faylisia.getInstance(), "entity_id");

    private final Entity entity;
    private final CustomEntityType entityType;
    private final Location spawnLoc;
    private long health;
    private long lastDamage = 0;

    /**
     * Constructor also spawn the entity
     * @param entityType custom entity type
     * @param world world where entity will spawn
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public CustomEntity(@NotNull CustomEntityType entityType, @NotNull World world, int x, int y, int z) {
        health = entityType.getMaxHealth();
        this.entityType = entityType;
        this.spawnLoc = new Location(world, x, y, z);
        this.entity = world.spawnEntity(spawnLoc, entityType.getEntityType(), false);
        entity.setCustomNameVisible(true);
        entity.setCustomName(entityType.getNameWithHealth(health));
        entity.getPersistentDataContainer().set(CustomEntityType.idKey, PersistentDataType.STRING, entityType.getId());

        registry.addEntity(this);
    }

    /**
     * Remove entity from the world and from the registry
     */
    public void remove() {
        registry.removeEntity(this);
        entity.remove();
    }

    /**
     * Update entity name, used to update entity health points
     */
    public void updateName() {
        entity.setCustomName(entityType.getNameWithHealth(health < 0 ? 0 : health));
    }

    /**
     * @return bukkit's entity associated to this
     */
    @Nullable
    public Entity getEntity() {
        return entity;
    }

    @NotNull
    public CustomEntityType getEntityType() {
        return entityType;
    }

    /**
     * Method to 'kill' entity
     */
    public void death(@Nullable Player killer) {
        if (entity instanceof LivingEntity living) {
            living.setHealth(0);
        } else {
            entity.remove();
        }
        registry.removeEntity(this);

        if (killer != null) {
            for (Loot loot : entityType.getLoots()) {
                for (ItemStack stack : loot.generateFor(killer)) {
                    PlayerUtils.giveOrDrop(killer, stack);
                }
            }
        }

        long tickBeforeRespawn = entityType.getTickBeforeRespawn();
        if (tickBeforeRespawn < 0) tickBeforeRespawn = 0;
        Entities.spawnLocations.forEach((spawnLoc, entity) -> {
            if (entity == this) {
                spawnLoc.setLastKill(Date.from(Instant.now()).getTime());
            }
        });
    }

    public long getLastDamage() {
        return lastDamage;
    }

    /**
     * Inflict damage to the entity, you can set an attacker to drop loot if entity will dead after this hit or to add
     *  effects like a thorns
     */
    public void takeDamage(long damage, @Nullable Player attacker) {
        health -= damage;
        if (health <= 0) {
            death(attacker);
        }
        lastDamage = Date.from(Instant.now()).getTime();
    }

    /**
     * Calculate damage to inflict to a player when hit, this can be useful to create a mob which inflict more
     * damage to player with a diamond armor for example
     * @param player player which will be attacked
     * @return damage to inflict to this player
     */
    public long getDamageFor(@NotNull Player player) {
        return entityType.getDamage();
    }

    public void teleportToSpawn() {
        this.entity.teleport(spawnLoc);
    }
}
