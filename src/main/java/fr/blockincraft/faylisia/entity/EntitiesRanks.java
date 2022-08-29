package fr.blockincraft.faylisia.entity;

public enum EntitiesRanks {
    S("S", "&grad([S] #fc0585 #e705fc)"),
    A("A", "&4[&cA&4]"),
    B("B", "&6[&eB&6]"),
    C("C", "&2[&aC&2]"),
    D("D", "&3[&bD&3]"),
    E("E", "&8[&7E&8]");

    public final String name;
    public final String prefix;

    EntitiesRanks(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
}
