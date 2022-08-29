package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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

    public CustomEntity(CustomEntityType entityType, World world, int x, int y, int z) {
        health = entityType.getMaxHealth();
        this.entityType = entityType;
        this.spawnLoc = new Location(world, x, y, z);
        this.entity = world.spawnEntity(spawnLoc, entityType.getEntityType(), false);
        entity.setCustomNameVisible(true);
        entity.setCustomName(entityType.getNameWithHealth(health));
        entity.getPersistentDataContainer().set(CustomEntityType.idKey, PersistentDataType.STRING, entityType.getId());

        registry.addEntity(this);
    }

    public void remove() {
        registry.removeEntity(this);
        entity.remove();
    }

    public void updateName() {
        entity.setCustomName(entityType.getNameWithHealth(health < 0 ? 0 : health));
    }

    public Entity getEntity() {
        return entity;
    }

    public CustomEntityType getEntityType() {
        return entityType;
    }

    public void death(Player killer) {
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

    public void takeDamage(long damage, Player attacker) {
        health -= damage;
        if (health <= 0) {
            death(attacker);
        }
        updateName();
        lastDamage = Date.from(Instant.now()).getTime();
    }

    public long getDamageFor(Player player) {
        return entityType.getDamage();
    }

    public void teleportToSpawn() {
        this.entity.teleport(spawnLoc);
    }
}
