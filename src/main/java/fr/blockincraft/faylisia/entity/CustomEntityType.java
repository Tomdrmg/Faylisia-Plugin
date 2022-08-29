package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.map.Regions;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;

import java.util.regex.Pattern;

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

    public CustomEntityType(EntityType entityType, String id, long maxHealth, long damage) {
        this.entityType = entityType;
        this.id = id;
        this.maxHealth = maxHealth;
        this.damage = damage;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDamage() {
        return damage;
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    public EntitiesRanks getRank() {
        return rank;
    }

    public Region getRegion() {
        return region;
    }

    public Loot[] getLoots() {
        return loots;
    }

    public long getTickBeforeRespawn() {
        return tickBeforeRespawn;
    }

    public CustomEntityType setTickBeforeRespawn(long tickBeforeRespawn) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.tickBeforeRespawn = tickBeforeRespawn;
        return this;
    }

    public CustomEntityType setRegion(Region region) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.region = region;
        return this;
    }

    public CustomEntityType setName(String name) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.name = name;
        return this;
    }

    public CustomEntityType setRank(EntitiesRanks rank) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.rank = rank;
        return this;
    }

    public CustomEntityType setLoots(Loot... loots) {
        if (registered) throw new ChangeRegisteredEntityType();
        this.loots = loots == null ? new Loot[0] : loots;
        return this;
    }

    public CustomEntity spawn(int x, int y, int z) {
        if (!registered) throw new RuntimeException("CANNOT SPAWN AN NON REGISTERED ENTITY!");

        World world = Bukkit.getWorld("world");
        if (world == null) return null;

        CustomEntity customEntity = new CustomEntity(this, world, x, y, z);

        return customEntity;
    }

    public String getNameWithHealth(long health) {
        return ColorsUtils.translateAll((rank != null ? rank.prefix + " " : "") + name + " &" + (health < maxHealth / 2 ? "e" : "a") + health + "&8/&a" + maxHealth + " &f" + Stats.HEALTH.bigIcon);
    }

    public void register() {
        if (registered) throw new ChangeRegisteredEntityType();

        if (entityType == null) throw new InvalidBuildException("Entity type cannot be null!");
        if (id == null || id.isEmpty()) throw new InvalidBuildException("Id cannot be empty/null!");
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]+!");
        if (registry.entityTypeIdUsed(id)) throw new InvalidBuildException("Id already used!");
        if (maxHealth < 1) throw new InvalidBuildException("Max health cannot be equal to 0 or negative!");
        if (damage < 0) throw new InvalidBuildException("Damage cannot be negative!");
        if (name == null) throw new InvalidBuildException("Name cannot be null!");

        registered = true;
        registry.registerEntityType(this);
    }

    protected static class ChangeRegisteredEntityType extends RuntimeException {
        public ChangeRegisteredEntityType() {
            super("You tried to edit a registered entity type!");
        }
    }

    protected static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid custom entity type build: " + cause);
        }
    }
}
