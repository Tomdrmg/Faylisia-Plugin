package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A region represent one or multiple areas represented by {@link Shape} in the world where a player can be <br/>
 * Setters can only be used before the register was register with method {@link Region#register()}, else a {@link ChangeRegisteredRegion} was thrown
 */
public class Region {
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    public static final char regionChar = '\uE022';

    private final String id;
    private final List<Shape> areas = new ArrayList<>();
    private final List<Region> subRegion = new ArrayList<>();
    private String name;
    private boolean registered;
    private Region parent;

    /**
     * @param id region id
     * @param name region display name
     */
    public Region(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isRegistered() {
        return registered;
    }

    /**
     * @return A copy of the list with all areas
     */
    public List<Shape> getAreas() {
        return new ArrayList<>(areas);
    }

    public void setName(String name) {
        if (registered) throw new ChangeRegisteredRegion();
        this.name = name;
    }

    /**
     * @param shape area to add
     * @return this instance
     */
    public Region addArea(Shape shape) {
        if (registered) throw new ChangeRegisteredRegion();
        this.areas.add(shape);
        return this;
    }

    /**
     * Return subregions of this region
     * @param all if we want all subregions
     * @return if all, returns subregions and their subregions else only this subregions
     */
    public List<Region> getSubRegion(boolean all) {
        if (all) {
            List<Region> allRegions = new ArrayList<>(subRegion);

            for (Region region : subRegion) {
                allRegions.addAll(region.getSubRegion(true));
            }

            return allRegions;
        } else {
            return new ArrayList<>(subRegion);
        }
    }

    /**
     * @return if this region has at least a subregion
     */
    public boolean hasSubRegion() {
        return !subRegion.isEmpty();
    }

    /**
     * Add a subregion to this region, this is private and called on {@link Region#register()}
     * @param region subregion
     */
    private void addSubRegion(Region region) {
        subRegion.add(region);
    }

    /**
     * Set parent region of this region, on {@link Region#register()} add subregion to parent
     * @param parent parent region
     * @return this instance
     */
    public Region setParent(Region parent) {
        if (registered) throw new ChangeRegisteredRegion();
        this.parent = parent;
        return this;
    }

    public Region getParent() {
        return parent;
    }

    /**
     * Register this region in {@link Registry}
     */
    public void register() {
        if (registered) throw new ChangeRegisteredRegion();
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]!");
        if (registry.regionIdUsed(id)) throw new InvalidBuildException("Id already used!");

        if (parent != null) parent.addSubRegion(this);

        registered = true;
        registry.registerRegion(this);
    }

    /**
     * Threw when use setters of a {@link Region#registered} {@link Region}
     */
    private static class ChangeRegisteredRegion extends RuntimeException {
        public ChangeRegisteredRegion() {
            super("You tried to edit a registered region!");
        }
    }

    /**
     * Threw when region has invalid parameter(s) on {@link Region#register()}
     */
    private static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid region build: " + cause);
        }
    }
}
