package fr.blockincraft.faylisia.items.armor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.items.event.Handlers;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * An armor set is one or multiple bonus given if player wear required amount of piece with the same armor set
 */
public class ArmorSet {
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered = false;

    private final String id;
    private Bonus[] bonus = new Bonus[0];

    /**
     * @param id unique id of this armor set
     */
    public ArmorSet(@NotNull String id) {
        this.id = id;
    }

    /**
     * Change bonus of the armor set
     * @param bonus new value
     * @return this instance
     */
    public ArmorSet setBonus(@NotNull Bonus... bonus) {
        if (registered) throw new ChangeRegisteredArmorSet();
        this.bonus = bonus == null ? new Bonus[0] : bonus;
        return this;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getId() {
        return id;
    }

    public Bonus[] getBonus() {
        return bonus;
    }

    /**
     * Verify all parameters requirements and then register this armor set in {@link Registry}
     */
    public void register() {
        if (registered) throw new ChangeRegisteredArmorSet();

        if (id.isEmpty()) throw new InvalidBuildException("Id cannot be null or empty!");
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]!");
        if (registry.armorSetIdUsed(id)) throw new InvalidBuildException("Id already used!");

        registered = true;
        registry.registerArmorSet(this);
    }

    /**
     * Bonus object represent bonus given to a player if requirements are filled
     */
    public record Bonus(String name, int minimum, Handlers handlers, String... description) {
        /**
         * Constructor needed to set minimum bigger than zero and smaller than four
         * @param name display name
         * @param minimum minimum piece to wear
         * @param handlers bonus to give
         * @param description description of this bonus
         */
        public Bonus(String name, int minimum, Handlers handlers, String... description) {
            this.name = name;
            this.description = description == null ? new String[0] : description;
            if (minimum < 1) minimum = 1;
            else if (minimum > 4) minimum = 4;
            this.minimum = minimum;
            this.handlers = handlers;
        }
    }

    /**
     * Thrown if we use setters of a registered armor set
     */
    private static class ChangeRegisteredArmorSet extends RuntimeException {
        public ChangeRegisteredArmorSet() {
            super("You tried to edit a registered armor set!");
        }
    }

    /**
     * Thrown if an error was encountered during execution of {@link ArmorSet#register()} method
     */
    private static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid custom item build: " + cause);
        }
    }
}
