package fr.blockincraft.faylisia.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.api.serializer.EntityTypeSerializer;
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
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered = false;

    private final EntityType entityType;
    private final String id;
    private final long maxHealth;
    private final long damage;
    private String name = null;
    private EntitiesRanks rank = EntitiesRanks.E;
    private Region region = Regions.WILDERNESS;
    private long tickBeforeRespawn = 0;
    private Loot[] loots = new Loot[0];

    /**
     * @param entityType entity type used to create this entity (like zombie)
     * @param id unique id of this entity type
     * @param maxHealth maximum health of this entity
     * @param damage damage of this entity
     */
    public CustomEntityType(@NotNull EntityType entityType, @NotNull String id, long maxHealth, long damage) {
        this.entityType = entityType;
        this.id = id;
        this.maxHealth = maxHealth;
        this.damage = damage;
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

    public long getDamage() {
        return damage;
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    @NotNull
    public EntitiesRanks getRank() {
        return rank;
    }

    @NotNull
    public Region getRegion() {
        return region;
    }

    @NotNull
    public Loot[] getLoots() {
        return loots;
    }

    public long getTickBeforeRespawn() {
        return tickBeforeRespawn;
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
     * Change entity rank
     * @param rank new value
     * @return this instance
     */
    @NotNull
    public CustomEntityType setRank(@NotNull EntitiesRanks rank) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.rank = rank;
        return this;
    }

    /**
     * Change entity loots
     * @param loots new value
     * @return this instance
     */
    @NotNull
    public CustomEntityType setLoots(@NotNull Loot... loots) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.loots = loots == null ? new Loot[0] : loots;
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
     * Construct entity display name to display health and rank if it
     * @param health health of the entity
     * @return name to display
     */
    @NotNull
    public String getNameWithHealth(long health) {
        return ColorsUtils.translateAll((rank != null ? rank.prefix + " " : "") + name + " &" + (health < maxHealth / 2 ? "e" : "a") + health + "&8/&a" + maxHealth + " &f" + Stats.HEALTH.bigIcon);
    }

    /**
     * Verify that all requirements are filled and register it in {@link Registry}
     */
    public void register() {
        if (registered) throw new ChangeRegisteredEntityType();

        if (id.isEmpty()) throw new InvalidBuildException("Id cannot be empty/null!");
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]+!");
        if (registry.entityTypeIdUsed(id)) throw new InvalidBuildException("Id already used!");
        if (maxHealth < 1) throw new InvalidBuildException("Max health cannot be equal to 0 or negative!");
        if (damage < 0) throw new InvalidBuildException("Damage cannot be negative!");
        if (name == null) throw new InvalidBuildException("Name cannot be null!");

        registered = true;
        registry.registerEntityType(this);
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
