package fr.blockincraft.faylisia.entity.interaction;

import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomLivingEntity;
import fr.blockincraft.faylisia.entity.HostileCustomLivingEntity;
import fr.blockincraft.faylisia.items.event.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HostileMobEntityType extends MobEntityType implements HostileEntityModel {
    private long damage = 0;
    private DamageType damageType = DamageType.MELEE_DAMAGE;

    public HostileMobEntityType(@NotNull EntityType entityType, @NotNull String id) {
        super(entityType, id);
    }

    /**
     * Change entity damages
     * @param damage new value
     * @return this instance
     */
    public HostileMobEntityType setDamage(long damage) {
        if (isRegistered()) throw new ChangeRegisteredEntityType();

        if (damage < 0) damage = 0;

        this.damage = damage;
        return this;
    }

    /**
     * Change entity damage type
     * @param damageType new value
     * @return this instance
     */
    public HostileMobEntityType setDamageType(@NotNull DamageType damageType) {
        if (isRegistered()) throw new ChangeRegisteredEntityType();

        this.damageType = damageType;
        return this;
    }

    @Override
    public long getDamage() {
        return damage;
    }

    @Override
    @NotNull
    public DamageType getDamageType() {
        return damageType;
    }

    @Override
    @NotNull
    public HostileCustomLivingEntity spawn(int x, int y, int z, @NotNull World world) {
        if (!isRegistered()) throw new RuntimeException("CANNOT SPAWN AN NON REGISTERED ENTITY!");

        return new HostileCustomLivingEntity(this, world, x, y, z);
    }
}
