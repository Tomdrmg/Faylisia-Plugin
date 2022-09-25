package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.ChatColor;
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

    /**
     * Constructor also spawn the entity
     * @param entityType custom entity type
     * @param world world where entity will spawn
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public CustomEntity(@NotNull CustomEntityType entityType, @NotNull World world, int x, int y, int z) {
        this.entityType = entityType;
        this.spawnLoc = new Location(world, x, y, z);
        this.entity = world.spawnEntity(spawnLoc, entityType.getEntityType(), false);
        entity.setCustomNameVisible(true);
        entity.setCustomName(ColorsUtils.translateAll(entityType.getName()));
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
        entity.setCustomName(ColorsUtils.translateAll(entityType.getName()));
    }

    /**
     * @return bukkit's entity associated to this
     */
    @NotNull
    public Entity getEntity() {
        return entity;
    }

    @NotNull
    public CustomEntityType getEntityType() {
        return entityType;
    }

    public void teleportToSpawn() {
        this.entity.teleport(spawnLoc);
    }
}
