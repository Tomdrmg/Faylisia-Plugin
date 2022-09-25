package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.interaction.MobEntityType;
import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.time.Instant;

public class CustomLivingEntity extends CustomEntity {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private long health;
    private long lastDamage = 0;

    public CustomLivingEntity(@NotNull MobEntityType entityType, @NotNull World world, int x, int y, int z) {
        super(entityType, world, x, y, z);
        health = entityType.getMaxHealth();
        updateName();
    }

    @Override
    @NotNull
    public MobEntityType getEntityType() {
        return (MobEntityType) super.getEntityType();
    }

    @Override
    public void updateName() {
        getEntity().setCustomName(getEntityType().getNameWithHealth(health < 0 ? 0 : health));
    }

    /**
     * Method to 'kill' entity
     */
    public void death(@Nullable Player killer) {
        if (getEntity() instanceof LivingEntity living) {
            living.setHealth(0);
        } else {
            getEntity().remove();
        }
        registry.removeEntity(this);

        if (killer != null) {
            for (Loot loot : getEntityType().getLoots()) {
                for (ItemStack stack : loot.generateFor(killer)) {
                    PlayerUtils.giveOrDrop(killer, stack);
                }
            }
        }

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
        updateName();
    }

    public long getHealth() {
        return health;
    }
}
