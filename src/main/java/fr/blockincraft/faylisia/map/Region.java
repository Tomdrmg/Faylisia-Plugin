package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.map.shapes.Shape;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A region represent one or multiple areas represented by {@link Shape} in the world where a player can be <br/>
 * Setters can only be used before the register was register with method {@link Region#register()}, else a {@link ChangeRegisteredRegion} was thrown
 */
public abstract class Region {
    private static final Pattern idPattern = Pattern.compile("[a-z\\d_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private final String id;
    private final List<Region> subRegion = new ArrayList<>();
    private String name;
    private boolean registered;
    private Region parent;
    private EnterRegionAction enterAction = (player, previousRegions, newRegions) -> true;
    private LeaveRegionAction leaveAction = (player, previousRegions, newRegions) -> true;

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

    public EnterRegionAction getEnterAction() {
        return enterAction;
    }

    public LeaveRegionAction getLeaveAction() {
        return leaveAction;
    }

    /**
     * Check if position is in a region
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param world world
     * @return if position is in
     */
    public abstract boolean isIn(int x, int y, int z, World world);

    /**
     * Change name of the region
     * @param name new value
     * @return this instance
     */
    public Region setName(String name) {
        if (registered) throw new ChangeRegisteredRegion();
        this.name = name;
        return this;
    }

    /**
     * Change enter action of the region
     * @param enterAction new value
     * @return this instance
     */
    public Region setEnterAction(EnterRegionAction enterAction) {
        if (registered) throw new ChangeRegisteredRegion();
        this.enterAction = enterAction;
        return this;
    }

    /**
     * Change leave action of the region
     * @param leaveAction new value
     * @return this instance
     */
    public Region setLeaveAction(LeaveRegionAction leaveAction) {
        if (registered) throw new ChangeRegisteredRegion();
        this.leaveAction = leaveAction;
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
    protected static class ChangeRegisteredRegion extends RuntimeException {
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

    @FunctionalInterface
    public interface EnterRegionAction {
        /**
         * Action to do when a player enter the region
         * @param player player which enter
         * @param previousRegions regions at previous player location
         * @param newRegions regions at new player location
         * @return if player can enter
         */
        boolean onEnter(@NotNull Player player, @NotNull Set<Region> previousRegions, @NotNull Set<Region> newRegions);
    }

    @FunctionalInterface
    public interface LeaveRegionAction {
        /**
         * Action to do when a player enter the region
         * @param player player which enter
         * @param previousRegions regions at previous player location
         * @param newRegions regions at new player location
         * @return if player can leave
         */
        boolean onLeave(@NotNull Player player, @NotNull Set<Region> previousRegions, @NotNull Set<Region> newRegions);
    }
}
