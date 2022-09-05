package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;

/**
 * All regions are created and register here
 */
public class Regions {
    // Create regions
    public static final Region WILDERNESS = new Region("wilderness", "&7Wilderness"); // Default region is everywhere, so we don't need to specify an area
    public static final Region SPAWN = new Region("spawn", "&grad(Cité #fc0585 #e705fc)")
            .addArea(new Rectangle(150, 150, -150, -150))
            .setParent(WILDERNESS);

    // Register regions and set default region
    static {
        WILDERNESS.register();
        SPAWN.register();

        Faylisia.getInstance().getRegistry().setDefaultRegion(WILDERNESS);
    }
}
