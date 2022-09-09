package fr.blockincraft.faylisia.entity;

import org.jetbrains.annotations.NotNull;

/**
 * An entity rank, just for display, represent difficulty of kill the entity
 */
public enum EntitiesRanks {
    S("S", "&grad([S] #fc0585 #e705fc)"),
    A("A", "&4[&cA&4]"),
    B("B", "&6[&eB&6]"),
    C("C", "&2[&aC&2]"),
    D("D", "&3[&bD&3]"),
    E("E", "&8[&7E&8]");

    public final String name;
    public final String prefix;

    /**
     * @param name name of the rank
     * @param prefix prefix displayed before entity name
     */
    EntitiesRanks(@NotNull String name, @NotNull String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
}
