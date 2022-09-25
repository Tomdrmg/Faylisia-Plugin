package fr.blockincraft.faylisia.blocks;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.items.tools.ToolType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class BlockType {
    private static final Pattern idPattern = Pattern.compile("[a-z\\d_-]+");
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private boolean registered = false;

    private final String id;
    private final Material material;
    private ToolType[] toolTypes = ToolType.values();
    private int level = 0;
    private long knowledgeNeeded = 0;
    private double durability = 0;
    private long tickBeforeRespawn = 0;
    private Loot[] loots = new Loot[0];

    /**
     * Default constructor
     * @param id id of the block type
     */
    public BlockType(@NotNull String id, @NotNull Material material) {
        if (!material.isBlock() || material.isAir()) throw new RuntimeException("Cannot create a block type with the material: " + material.name());

        this.id = id;
        this.material = material;
    }

    /**
     * Change block type knowledge needed
     * @param knowledgeNeeded new value
     * @return this instance
     */
    @NotNull
    public BlockType setKnowledgeNeeded(long knowledgeNeeded) {
        if (registered) throw new ChangeRegisteredBlockType();

        this.knowledgeNeeded = knowledgeNeeded;
        return this;
    }

    /**
     * Change block type durability
     * @param durability new value
     * @return this instance
     */
    @NotNull
    public BlockType setDurability(double durability) {
        if (registered) throw new ChangeRegisteredBlockType();

        this.durability = durability;
        return this;
    }

    /**
     * Change block type tool types
     * @param toolTypes new value
     * @return this instance
     */
    @NotNull
    public BlockType setToolTypes(@NotNull ToolType[] toolTypes) {
        if (registered) throw new ChangeRegisteredBlockType();

        this.toolTypes = toolTypes;
        return this;
    }

    /**
     * Change block type level
     * @param level new value
     * @return this instance
     */
    @NotNull
    public BlockType setLevel(int level) {
        if (registered) throw new ChangeRegisteredBlockType();

        this.level = level;
        return this;
    }

    /**
     * Change block type tick before respawn
     * @param tickBeforeRespawn new value
     * @return this instance
     */
    @NotNull
    public BlockType setTickBeforeRespawn(long tickBeforeRespawn) {
        if (registered) throw new ChangeRegisteredBlockType();

        this.tickBeforeRespawn = tickBeforeRespawn;
        return this;
    }

    /**
     * Change block type loots
     * @param loots new value
     * @return this instance
     */
    @NotNull
    public BlockType setLoots(Loot[] loots) {
        if (registered) throw new ChangeRegisteredBlockType();

        this.loots = loots;
        return this;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * @return knowledge needed to break and harvest the block
     */
    public long getKnowledgeNeeded() {
        return knowledgeNeeded;
    }

    public double getDurability() {
        return durability;
    }

    @NotNull
    public ToolType[] getToolTypes() {
        return toolTypes;
    }

    public int getLevel() {
        return level;
    }

    public long getTickBeforeRespawn() {
        return tickBeforeRespawn;
    }

    @NotNull
    public Loot[] getLoots() {
        return loots;
    }

    public boolean isRegistered() {
        return registered;
    }

    /**
     * Valid all parameters of the item and then register it in {@link Registry} <br/>
     * Registered items can't be edited
     */
    public void register() {
        if (registered) throw new ChangeRegisteredBlockType();

        if (!idPattern.matcher(id).matches()) throw new InvalidBuildException("Id can only contains pattern [a-z1-9_-]+!");
        if (registry.blockTypeIdUsed(id)) throw new InvalidBuildException("Id already used!");

        // Call this to subclasses
        registerOthers();

        registered = true;
        registry.registerBlockType(this);
    }

    /**
     * Method only used in subclasses to do actions and valid more parameters in it
     */
    protected void registerOthers() {

    }

    /**
     * Thrown when we use a Setter of this class on an item which is {@link BlockType#registered}
     */
    protected static class ChangeRegisteredBlockType extends RuntimeException {
        public ChangeRegisteredBlockType() {
            super("You tried to edit a registered block type!");
        }
    }

    /**
     * Thrown when an error occurred during item registration in method {@link BlockType#register()}
     */
    protected static class InvalidBuildException extends RuntimeException {
        public InvalidBuildException(@NotNull String cause) {
            super("Invalid custom block type build: " + cause);
        }
    }
}
