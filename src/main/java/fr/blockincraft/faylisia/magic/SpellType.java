package fr.blockincraft.faylisia.magic;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SpellType {
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered;

    private final String id;
    private String name = "";
    private long useCost = 0;
    private long tickDuration = 0;
    private AnimationFrames frames = (tick, x, y, z, world, player, params) -> false;
    private SpellAction action = (player, x, y, z, world, params) -> {};
    private boolean cancelOnQuit = true;
    private boolean cancelOnMove = false;
    private boolean cancelOnDamage = false;
    private boolean cancelOnTakeDamage = false;
    private boolean cancelOnChangeWorld = true;
    private long cooldown = 0;

    public SpellType(@NotNull String id) {
        this.id = id;
    }

    public boolean isRegistered() {
        return registered;
    }

    @NotNull
    public SpellType setAction(@NotNull SpellAction action) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.action = action;
        return this;
    }

    @NotNull
    public SpellType setFrames(@NotNull AnimationFrames frames) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.frames = frames;
        return this;
    }

    @NotNull
    public SpellType setName(@NotNull String name) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.name = name;
        return this;
    }

    @NotNull
    public SpellType setTickDuration(long tickDuration) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.tickDuration = tickDuration;
        return this;
    }

    @NotNull
    public SpellType setUseCost(long useCost) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.useCost = useCost;
        return this;
    }

    @NotNull
    public SpellType setCancelOnQuit(boolean cancelOnQuit) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.cancelOnQuit = cancelOnQuit;
        return this;
    }

    @NotNull
    public SpellType setCancelOnDamage(boolean cancelOnDamage) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.cancelOnDamage = cancelOnDamage;
        return this;
    }

    @NotNull
    public SpellType setCancelOnMove(boolean cancelOnMove) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.cancelOnMove = cancelOnMove;
        return this;
    }

    @NotNull
    public SpellType setCancelOnTakeDamage(boolean cancelOnTakeDamage) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.cancelOnTakeDamage = cancelOnTakeDamage;
        return this;
    }

    @NotNull
    public SpellType setCancelOnChangeWorld(boolean cancelOnChangeWorld) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.cancelOnChangeWorld = cancelOnChangeWorld;
        return this;
    }

    @NotNull
    public SpellType setCooldown(long cooldown) {
        if (registered) throw new ChangeRegisteredSpellException();
        this.cooldown = cooldown;
        return this;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public long getTickDuration() {
        return tickDuration;
    }

    @NotNull
    public AnimationFrames getFrames() {
        return frames;
    }

    public long getUseCost() {
        return useCost;
    }

    @NotNull
    public SpellAction getAction() {
        return action;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isCancelOnDamage() {
        return cancelOnDamage;
    }

    public boolean isCancelOnMove() {
        return cancelOnMove;
    }

    public boolean isCancelOnTakeDamage() {
        return cancelOnTakeDamage;
    }

    public boolean isCancelOnQuit() {
        return cancelOnQuit;
    }

    public boolean isCancelOnChangeWorld() {
        return cancelOnChangeWorld;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void register() {
        if (registered) throw new ChangeRegisteredSpellException();

        if (id.isEmpty() || !idPattern.matcher(id).matches()) throw new InvalidBuildException("Invalid id ([a-zA-Z_])");
        if (registry.spellTypeIdUsed(id)) throw new InvalidBuildException("Id already used!");

        this.registered = true;
        registry.registerSpellType(this);
    }

    @FunctionalInterface
    public interface AnimationFrames {
        boolean displayFrame(long tick, int x, int y, int z, @NotNull World world, @NotNull Player player, @NotNull Map<Class<?>, List<Object>> params);
    }

    @FunctionalInterface
    public interface SpellAction {
        void end(@NotNull Player player, int x, int y, int z, @NotNull World world, @NotNull Map<Class<?>, List<Object>> params);
    }

    public static class ChangeRegisteredSpellException extends RuntimeException {
        public ChangeRegisteredSpellException() {
            super("You tried to edit a registered spell!");
        }
    }

    protected static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(@NotNull String cause) {
            super("Invalid spell build: " + cause);
        }
    }
}
