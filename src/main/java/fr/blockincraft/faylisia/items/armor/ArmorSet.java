package fr.blockincraft.faylisia.items.armor;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ArmorSet {
    private static final Pattern idPattern = Pattern.compile("[a-z1-9_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered = false;

    private final String id;
    private Bonus[] bonus = new Bonus[0];

    public ArmorSet(String id) {
        this.id = id;
    }

    public ArmorSet setBonus(Bonus... bonus) {
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

    public void register() {
        if (registered) throw new ChangeRegisteredArmorSet();

        if (id == null || id.isEmpty()) throw new InvalidBuildException("Id cannot be null or empty!");
        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]!");
        if (registry.itemIdUsed(id)) throw new InvalidBuildException("Id already used!");

        registered = true;
        registry.registerArmorSet(this);
    }

    public static class Bonus {
        private final String name;
        private final String[] description;
        private final int minimum;
        private Handlers handlers;

        public Bonus(String name, int minimum, Handlers handlers, String... description) {
            this.name = name;
            this.description = description == null ? new String[0] : description;
            if (minimum < 1) minimum = 1;
            else if (minimum > 4) minimum = 4;
            this.minimum = minimum;
            this.handlers = handlers;
        }

        public String getName() {
            return name;
        }

        public int getMinimum() {
            return minimum;
        }

        public String[] getDescription() {
            return description;
        }

        public Handlers getHandlers() {
            return handlers;
        }
    }

    private static class ChangeRegisteredArmorSet extends RuntimeException {
        public ChangeRegisteredArmorSet() {
            super("You tried to edit a registered armor set!");
        }
    }

    private static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(String cause) {
            super("Invalid custom item build: " + cause);
        }
    }
}
