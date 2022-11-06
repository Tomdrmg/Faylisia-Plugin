package fr.blockincraft.faylisia.entity.interaction;

import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomEntityType;
import fr.blockincraft.faylisia.entity.CustomLivingEntity;
import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.entity.loot.LootableEntityModel;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobEntityType extends CustomEntityType implements DifficultyEntityModel, DamageableEntityModel, LootableEntityModel {
    private long maxHealth = 1;
    private int level = 0;
    private Loot[] loots = new Loot[0];

    public MobEntityType(@NotNull EntityType entityType, @NotNull String id) {
        super(entityType, id);
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    public int getLevel() {
        return level;
    }

    @NotNull
    public Loot[] getLoots() {
        return loots;
    }

    /**
     * Change entity level
     * @param level new value
     * @return this instance
     */
    @NotNull
    public MobEntityType setLevel(int level) {
        if (isRegistered()) throw new ChangeRegisteredEntityType();
        this.level = level;
        return this;
    }

    /**
     * Change entity max health
     * @param maxHealth new value
     * @return this instance
     */
    @NotNull
    public MobEntityType setMaxHealth(int maxHealth) {
        if (isRegistered()) throw new ChangeRegisteredEntityType();

        if (maxHealth < 1) maxHealth = 1;

        this.maxHealth = maxHealth;
        return this;
    }

    /**
     * Change entity loots
     * @param loots new value
     * @return this instance
     */
    @NotNull
    public MobEntityType setLoots(@NotNull Loot... loots) {
        if (isRegistered()) throw new ChangeRegisteredEntityType();
        this.loots = loots == null ? new Loot[0] : loots;
        return this;
    }

    /**
     * Construct entity display name to display health and rank if it
     * @param health health of the entity
     * @return name to display
     */
    @NotNull
    public String getNameWithHealth(long health) {
        return ColorsUtils.translateAll("&7[Niveau " + level + "] " + getName() + " &" + (health < maxHealth / 2 ? "e" : "a") + health + "&8/&a" + maxHealth + " &f" + Stats.HEALTH.bigIcon);
    }

    @Override
    @NotNull
    public CustomLivingEntity spawn(int x, int y, int z, @NotNull World world) {
        if (!isRegistered()) throw new RuntimeException("CANNOT SPAWN AN NON REGISTERED ENTITY!");

        return new CustomLivingEntity(this, world, x, y, z);
    }
}
