package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.utils.ColorsUtils;

/**
 * All regions are created and register here
 */
public class Regions {
    // Create regions
    public static final Region WILDERNESS = new ShapedRegion("wilderness", "&7Wilderness"); // Default region is everywhere, so we don't need to specify an area
    public static final ColorRegion R1 = (ColorRegion) new ColorRegion("r1", "#9eeaffRégion 1")
            .setColor(0xff9eeaff)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R2 = (ColorRegion) new ColorRegion("r2", "#fc7cffRégion 2")
            .setColor(0xfffc7cff)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R3 = (ColorRegion) new ColorRegion("r3", "#edffc9Région 3")
            .setColor(0xffedffc9)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R4 = (ColorRegion) new ColorRegion("r4", "#ff837aRégion 4")
            .setColor(0xffff837a)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ORMON_EMPIRE = (ColorRegion) new ColorRegion("ormon_empire", "#ff00d4Empire d'Ormon")
            .setColor(0xffff00d4)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R6 = (ColorRegion) new ColorRegion("r6", "#00ff90Région 6")
            .setColor(0xff00ff90)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R7 = (ColorRegion) new ColorRegion("r7", "#ffe68cRégion 7")
            .setColor(0xffffe68c)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R8 = (ColorRegion) new ColorRegion("r8", "#b7ffa3Région 8")
            .setColor(0xffb7ffa3)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R9 = (ColorRegion) new ColorRegion("r9", "#aa00ffRégion 9")
            .setColor(0xffaa00ff)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R10 = (ColorRegion) new ColorRegion("r10", "#aab4ffRégion 10")
            .setColor(0xffaab4ff)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R11 = (ColorRegion) new ColorRegion("r11", "#00fff6Région 11")
            .setColor(0xff00fff6)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R12 = (ColorRegion) new ColorRegion("r12", "#ffc9c9Région 12")
            .setColor(0xffffc9c9)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R13 = (ColorRegion) new ColorRegion("r13", "#00ff19Région 13")
            .setColor(0xff00ff19)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R14 = (ColorRegion) new ColorRegion("r14", "#aaffd3Région 14")
            .setColor(0xffaaffd3)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R15 = (ColorRegion) new ColorRegion("r15", "#ff0043Région 15")
            .setColor(0xffff0043)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R16 = (ColorRegion) new ColorRegion("r16", "#e3a3ffRégion 16")
            .setColor(0xffe3a3ff)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R17 = (ColorRegion) new ColorRegion("r17", "#9dff00Région 17")
            .setColor(0xff9dff00)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R18 = (ColorRegion) new ColorRegion("r18", "#ffe100Région 18")
            .setColor(0xffffe100)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion R19 = (ColorRegion) new ColorRegion("r19", "#ff6e00Région 19")
            .setColor(0xffff6e00)
            .setParent(Regions.WILDERNESS)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });

    // Register regions and set default region
    static {
        WILDERNESS.register();

        R1.register();
        R2.register();
        R3.register();
        R4.register();
        ORMON_EMPIRE.register();
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
        R19.register();

        Faylisia.getInstance().getRegistry().setDefaultRegion(WILDERNESS);
    }
}
