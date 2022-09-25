package fr.blockincraft.faylisia.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.api.serializer.EntityTypeSerializer;
import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.map.Regions;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.task.EntityQuitRegionTask;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@JsonSerialize(using = EntityTypeSerializer.class)
public class CustomEntityType {
    public static final NamespacedKey idKey = new NamespacedKey(Faylisia.getInstance(), "custom-id");
    private static final Pattern idPattern = Pattern.compile("[a-z\\d_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered = false;

    private final EntityType entityType;
    private final String id;
    private String name = null;
    private Region region = Regions.WILDERNESS;
    private long tickBeforeRespawn = 0;

    /**
     * @param entityType entity type used to create this entity (like zombie)
     * @param id unique id of this entity type
     */
    public CustomEntityType(@NotNull EntityType entityType, @NotNull String id) {
        this.entityType = entityType;
        this.id = id;
    }

    @NotNull
    public EntityType getEntityType() {
        return entityType;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Region getRegion() {
        return region;
    }

    public long getTickBeforeRespawn() {
        return tickBeforeRespawn;
    }

    public boolean isRegistered() {
        return registered;
    }

    /**
     * Change tick before respawn
     * @param tickBeforeRespawn new value
     * @return this instance
     */
    @NotNull
    public CustomEntityType setTickBeforeRespawn(long tickBeforeRespawn) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.tickBeforeRespawn = tickBeforeRespawn;
        return this;
    }

    /**
     * Change region of this mob, it can't leave his region (See {@link EntityQuitRegionTask})
     * @param region new value
     * @return this instance
     */
    @NotNull
    public CustomEntityType setRegion(@NotNull Region region) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.region = region;
        return this;
    }

    /**
     * Change entity display name
     * @param name new value
     * @return this instance
     */
    @NotNull
    public CustomEntityType setName(@NotNull String name) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.name = name;
        return this;
    }

    /**
     * Spawn an entity at this coordinates
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return entity created
     */
    @Nullable
    public CustomEntity spawn(int x, int y, int z) {
        if (!registered) throw new RuntimeException("CANNOT SPAWN AN NON REGISTERED ENTITY!");

        World world = Bukkit.getWorld("world");
        if (world == null) return null;

        CustomEntity customEntity = new CustomEntity(this, world, x, y, z);

        return customEntity;
    }

    /**
     * Verify that all requirements are filled and register it in {@link Registry}
     */
    public void register() {
        if (registered) throw new ChangeRegisteredEntityType();

        if (id.isEmpty()) throw new InvalidBuildException("Id cannot be empty/null!");
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]+!");
        if (registry.entityTypeIdUsed(id)) throw new InvalidBuildException("Id already used!");
        if (name == null) throw new InvalidBuildException("Name cannot be null!");

        registerOthers();

        registered = true;
        registry.registerEntityType(this);
    }

    /**
     * Used by subclass to register other params
     */
    public void registerOthers() {

    }

    /**
     * Thrown if we class setters of a registered entity type
     */
    protected static class ChangeRegisteredEntityType extends RuntimeException {
        public ChangeRegisteredEntityType() {
            super("You tried to edit a registered entity type!");
        }
    }

    /**
     * Thrown if an error was encountered during execution of {@link CustomEntityType#register()} method
     */
    protected static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid custom entity type build: " + cause);
        }
    }
}
