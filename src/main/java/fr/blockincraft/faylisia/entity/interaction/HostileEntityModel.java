package fr.blockincraft.faylisia.entity.interaction;

import fr.blockincraft.faylisia.items.event.DamageType;
import org.jetbrains.annotations.NotNull;

public interface HostileEntityModel {
    long getDamage();

    @NotNull
    DamageType getDamageType();
}
