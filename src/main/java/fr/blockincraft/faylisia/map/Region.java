package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    public List<Shape> getAreas() {
        return new ArrayList<>(areas);
    }

    public void setName(String name) {
        if (registered) throw new ChangeRegisteredRegion();
        this.name = name;
    }

    public Region addArea(Shape shape) {
        if (registered) throw new ChangeRegisteredRegion();
        this.areas.add(shape);
        return this;
    }

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

    public boolean hasSubRegion() {
        return !subRegion.isEmpty();
    }

    private void addSubRegion(Region region) {
        subRegion.add(region);
    }

    public Region setParent(Region parent) {
        if (registered) throw new ChangeRegisteredRegion();
        this.parent = parent;
        return this;
    }

    public Region getParent() {
        return parent;
    }

    public void register() {
        if (registered) throw new ChangeRegisteredRegion();
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]!");
        if (registry.regionIdUsed(id)) throw new InvalidBuildException("Id already used!");

        if (parent != null) parent.addSubRegion(this);

        registered = true;
        registry.registerRegion(this);
    }

    private static class ChangeRegisteredRegion extends RuntimeException {
        public ChangeRegisteredRegion() {
            super("You tried to edit a registered region!");
        }
    }

    private static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid region build: " + cause);
        }
    }
}
