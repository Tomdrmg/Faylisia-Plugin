package fr.blockincraft.faylisia.magic;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Spell {
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private final String id;
    private String name = "";
    private long useCost = 0;
    private long tickDuration = 0;

    public Spell(@NotNull String id) {
        this.id = id;
    }

    @FunctionalInterface
    public interface AnimationFrames {
        void displayFrame(long tick, int x, int y, int z, @NotNull Player player, Map<Class<?>, List<Object>> idk);
    }
}
