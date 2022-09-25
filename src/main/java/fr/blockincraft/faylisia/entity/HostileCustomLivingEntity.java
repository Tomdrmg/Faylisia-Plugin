package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.entity.interaction.HostileMobEntityType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HostileCustomLivingEntity extends CustomLivingEntity {
    public HostileCustomLivingEntity(@NotNull HostileMobEntityType entityType, @NotNull World world, int x, int y, int z) {
        super(entityType, world, x, y, z);
    }

    @Override
    @NotNull
    public HostileMobEntityType getEntityType() {
        return (HostileMobEntityType) super.getEntityType();
    }

    /**
     * Calculate damage to inflict to a player when hit, this can be useful to create a mob which inflict more
     * damage to player with a diamond armor for example
     * @param player player which will be attacked
     * @return damage to inflict to this player
     */
    public long getDamageFor(@NotNull Player player) {
        return getEntityType().getDamage();
    }
}
