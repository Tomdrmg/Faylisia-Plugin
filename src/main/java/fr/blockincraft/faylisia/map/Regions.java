package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;

/**
 * All regions are created and register here
 */
public class Regions {
    // Create regions
    public static final Region WILDERNESS = new ShapedRegion("wilderness", "&7Wilderness"); // Default region is everywhere, so we don't need to specify an area
    public static final Region CITE = new ColorRegion("cite", "#fc0585Cité (Huguette)")
            .setColor(0xFFFFF200)
            .setParent(WILDERNESS);
    public static final Region R2 = new ColorRegion("r2", "#00FFE52 (frilex5)")
            .setColor(0xFF00FFE5)
            .setParent(WILDERNESS);
    public static final Region R3 = new ColorRegion("r3", "#FF72003")
            .setColor(0xFFFF7200)
            .setParent(WILDERNESS);
    public static final Region R4 = new ColorRegion("r4", "#FFB2004 (THE BLACK KNIGHT)")
            .setColor(0xFFFFB200)
            .setParent(WILDERNESS);
    public static final Region R5 = new ColorRegion("r5", "#A1FF005")
            .setColor(0xFFA1FF00)
            .setParent(WILDERNESS);
    public static final Region R6 = new ColorRegion("r6", "#00FF996 (Newvv_)")
            .setColor(0xFF00FF99)
            .setParent(WILDERNESS);
    public static final Region R7 = new ColorRegion("r7", "#9000FF7")
            .setColor(0xFF9000FF)
            .setParent(WILDERNESS);
    public static final Region R8 = new ColorRegion("r8", "#0072FF8")
            .setColor(0xFF0072FF)
            .setParent(WILDERNESS);
    public static final Region R9 = new ColorRegion("r9", "#00BFFF9")
            .setColor(0xFF00BFFF)
            .setParent(WILDERNESS);
    public static final Region R10 = new ColorRegion("r10", "#FF320010")
            .setColor(0xFFFF3200)
            .setParent(WILDERNESS);
    public static final Region R11 = new ColorRegion("r11", "#FF002E11")
            .setColor(0xFFFF002E)
            .setParent(WILDERNESS);
    public static final Region R12 = new ColorRegion("r12", "#FF7F9212")
            .setColor(0xFFFF7F92)
            .setParent(WILDERNESS);
    public static final Region R13 = new ColorRegion("r13", "#FF9BFF13")
            .setColor(0xFFFF9BFF)
            .setParent(WILDERNESS);
    public static final Region R14 = new ColorRegion("r14", "#00FF3714")
            .setColor(0xFF00FF37)
            .setParent(WILDERNESS);
    public static final Region R15 = new ColorRegion("r15", "#FF00B215")
            .setColor(0xFFFF00B2)
            .setParent(WILDERNESS);
    public static final Region R16 = new ColorRegion("r16", "#E900FF16")
            .setColor(0xFFE900FF)
            .setParent(WILDERNESS);
    public static final Region R17 = new ColorRegion("r17", "#0800FF17")
            .setColor(0xFF0800FF)
            .setParent(WILDERNESS);
    public static final Region R18 = new ColorRegion("r18", "#9B9BFF18")
            .setColor(0xFF9B9BFF)
            .setParent(WILDERNESS);

    // Register regions and set default region
    static {
        WILDERNESS.register();
        CITE.register();
        R2.register();
        R3.register();
        R4.register();
        R5.register();
        R6.register();
        R7.register();
        R8.register();
        R9.register();
        R10.register();
        R11.register();
        R12.register();
        R13.register();
        R14.register();
        R15.register();
        R16.register();
        R17.register();
        R18.register();

        Faylisia.getInstance().getRegistry().setDefaultRegion(WILDERNESS);
    }
}
