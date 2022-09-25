package fr.blockincraft.faylisia.map;

import fr.blockincraft.faylisia.map.shapes.Shape;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ShapedRegion extends Region {
    private final List<Shape> areas = new ArrayList<>();

    public ShapedRegion(String id, String name) {
        super(id, name);
    }

    @Override
    public boolean isIn(int x, int y, int z, World world) {
        for (Shape area : areas) {
            if (area.contain(x, y, z, world)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return A copy of the list with all areas
     */
    public List<Shape> getAreas() {
        return new ArrayList<>(areas);
    }

    /**
     * @param shape area to add
     * @return this instance
     */
    public ShapedRegion addArea(Shape shape) {
        if (isRegistered()) throw new ChangeRegisteredRegion();
        this.areas.add(shape);
        return this;
    }
}
