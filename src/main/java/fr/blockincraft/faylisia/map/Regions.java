package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.map.shapes.Rectangle;
import fr.blockincraft.faylisia.utils.ColorsUtils;

/**
 * All regions are created and register here
 */
public class Regions {
    // Create regions
    public static final Region WILDERNESS = new ShapedRegion("wilderness", "#636363None"); // Default region is everywhere, so we don't need to specify an area
    public static final Region OCEAN = new ShapedRegion("ocean", "#046fe0Océan")
            .addArea(new Rectangle(-6820, -4290, 6820, 4290))
            .setParent(Regions.WILDERNESS)
            .setLeaveAction((player, previousRegions, newRegions, thisRegion) -> false);
    public static final ColorRegion HILIPIA = (ColorRegion) new ColorRegion("hilipia", "#9eeaffHilipia")
            .setColor(0xff9eeaff)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion LANORE = (ColorRegion) new ColorRegion("lanore", "#fc7cffLanore")
            .setColor(0xfffc7cff)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion PROVINCE_DE_SUGIS = (ColorRegion) new ColorRegion("province_de_sugis", "#edffc9Province De Sûgis")
            .setColor(0xffedffc9)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ROYAUME_DHOSEG = (ColorRegion) new ColorRegion("royaume_dhoseg", "#ff837aRoyaume D'Hoseg")
            .setColor(0xffff837a)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion EMPIRE_DORMON = (ColorRegion) new ColorRegion("empire_dormon", "#ff00d4Empire D'Ormon")
            .setColor(0xffff00d4)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion PAYS_DE_NEMEE = (ColorRegion) new ColorRegion("pays_de_nemee", "#00ff90Pays De Némée")
            .setColor(0xff00ff90)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ROYAUME_DE_MESTA = (ColorRegion) new ColorRegion("royaume_de_mesta", "#ffe68cRoyaume De Meste")
            .setColor(0xffffe68c)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion TERRE_DE_SOVA = (ColorRegion) new ColorRegion("terre_de_sova", "#b7ffa3Terre De Sova")
            .setColor(0xffb7ffa3)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ROYAUME_DE_LA_LUEUR = (ColorRegion) new ColorRegion("royaume_de_la_lueur", "#aa00ffRoyaume De La Lueur")
            .setColor(0xffaa00ff)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ROYAUME_INTERDIT = (ColorRegion) new ColorRegion("royaume_interdit", "#aab4ffRoyaume Interdit")
            .setColor(0xffaab4ff)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion TERRE_DE_LA_CONFLAGRATION = (ColorRegion) new ColorRegion("terre_de_la_conflagration", "#00fff6Terre De La Conflagration")
            .setColor(0xff00fff6)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion LA_VERDURE_DE_CLIDEDRIA = (ColorRegion) new ColorRegion("la_verdure_de_clideria", "#ffc9c9La Verdure De Clidéria")
            .setColor(0xffffc9c9)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion CONTREE_DE_BRESIA = (ColorRegion) new ColorRegion("contree_de_bresia", "#00ff19Contrée De Brésia")
            .setColor(0xff00ff19)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion LA_CONTREE_ENCHANTEE = (ColorRegion) new ColorRegion("la_contree_enchantee", "#aaffd3La Contrée Enchantée")
            .setColor(0xffaaffd3)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion BIGARRIUM = (ColorRegion) new ColorRegion("bigarrium", "#ff0043Bigarrium")
            .setColor(0xffff0043)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ROYAUME_CORROMPU = (ColorRegion) new ColorRegion("royaume_corrompu", "#e3a3ffRoyaume Corrompu")
            .setColor(0xffe3a3ff)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion ROYAUME_DE_NEHMA = (ColorRegion) new ColorRegion("royaume_de_nehma", "#9dff00Royaume De Néhma")
            .setColor(0xff9dff00)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion JADIA = (ColorRegion) new ColorRegion("jadia", "#ffe100Jadia")
            .setColor(0xffffe100)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });
    public static final ColorRegion TERRE_DE_NOSTRA = (ColorRegion) new ColorRegion("terre_de_nostra", "#ff6e00Terre De Nostra")
            .setColor(0xffff6e00)
            .setParent(Regions.OCEAN)
            .setEnterAction((player, previousRegions, newRegions, thisRegion) -> {
                player.resetTitle();
                player.sendTitle(ColorsUtils.translateAll(thisRegion.getName()), "", 10, 70, 20);
                return true;
            });

    // Register regions and set default region
    static {
        WILDERNESS.register();

        OCEAN.register();

        HILIPIA.register();
        LANORE.register();
        PROVINCE_DE_SUGIS.register();
        ROYAUME_DHOSEG.register();
        EMPIRE_DORMON.register();
        PAYS_DE_NEMEE.register();
        ROYAUME_DE_MESTA.register();
        TERRE_DE_SOVA.register();
        ROYAUME_DE_LA_LUEUR.register();
        ROYAUME_INTERDIT.register();
        TERRE_DE_LA_CONFLAGRATION.register();
        LA_VERDURE_DE_CLIDEDRIA.register();
        CONTREE_DE_BRESIA.register();
        LA_CONTREE_ENCHANTEE.register();
        BIGARRIUM.register();
        ROYAUME_CORROMPU.register();
        ROYAUME_DE_NEHMA.register();
        JADIA.register();
        TERRE_DE_NOSTRA.register();

        Faylisia.getInstance().getRegistry().setDefaultRegion(WILDERNESS);
    }
}
