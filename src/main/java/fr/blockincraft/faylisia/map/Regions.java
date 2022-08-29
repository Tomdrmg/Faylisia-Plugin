package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;

public class Regions {
    public static final Region WILDERNESS = new Region("wilderness", "&7Wilderness");
    public static final Region SPAWN = new Region("spawn", "&grad(Cité #fc0585 #e705fc)")
            .addArea(new Rectangle(150, 150, -150, -150))
            .setParent(WILDERNESS);

    static {
        WILDERNESS.register();
        SPAWN.register();

        Faylisia.getInstance().getRegistry().setDefaultRegion(WILDERNESS);
    }
}
