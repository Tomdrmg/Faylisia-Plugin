package fr.blockincraft.faylisia.blocks;

import fr.blockincraft.faylisia.entity.loot.Loot;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CustomBlock {
    private final int x;
    private final int y;
    private final int z;
    private final UUID world;
    private final boolean restartAtRegen;
    private final Material finalMaterial;
    private final BlockType[] states;
    private int state = 0;
    private long tickSinceLastState = 0;

    public CustomBlock(int x, int y, int z, @NotNull UUID world, boolean restartAtRegen, @NotNull Material finalMaterial, @NotNull BlockType... states) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.restartAtRegen = restartAtRegen;
        this.finalMaterial = finalMaterial;
        this.states = states;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @NotNull
    public UUID getWorld() {
        return world;
    }

    public boolean doRestartAtRegen() {
        return restartAtRegen;
    }

    @NotNull
    public Material getFinalMaterial() {
        return finalMaterial;
    }

    @NotNull
    public BlockType[] getStates() {
        return states;
    }

    public int getState() {
        return state;
    }

    public long getTickSinceLastState() {
        return tickSinceLastState;
    }

    public boolean hasPreviousState() {
        return state > 0;
    }

    @NotNull
    public BlockType getPreviousState() {
        return states[state - 1];
    }

    @Nullable
    public BlockType getCurrentState() {
        return state >= states.length ? null : states[state];
    }

    public void setTickSinceLastState(long tickSinceLastState) {
        this.tickSinceLastState = tickSinceLastState;
    }

    public void breakBlock(Player player) {
        World world = Bukkit.getWorld(this.world);

        if (state >= states.length || world == null) return;

        for (Loot loot : states[state].getLoots()) {
            for (ItemStack itemStack : loot.generateFor(player)) {
                PlayerUtils.giveOrDrop(player, itemStack);
            }
        }

        state++;

        if (state == states.length) {
            world.setBlockData(x, y, z, finalMaterial.createBlockData());
        } else {
            world.setBlockData(x, y, z, states[state].getMaterial().createBlockData());
        }

        tickSinceLastState = 0;
    }

    public void regenBlock() {
        World world = Bukkit.getWorld(this.world);

        if (state <= 0 || world == null) return;

        if (restartAtRegen) state = 0;
        else state --;

        world.setBlockData(x, y, z, states[state].getMaterial().createBlockData());
        tickSinceLastState = 0;
    }
}
