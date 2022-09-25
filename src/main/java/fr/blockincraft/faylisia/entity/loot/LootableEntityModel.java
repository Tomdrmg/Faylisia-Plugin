package fr.blockincraft.faylisia.entity.loot;

import org.jetbrains.annotations.NotNull;

public interface LootableEntityModel {
    @NotNull
    Loot[] getLoots();
}
