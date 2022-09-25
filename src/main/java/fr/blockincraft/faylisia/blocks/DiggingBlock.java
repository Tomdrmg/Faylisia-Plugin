package fr.blockincraft.faylisia.blocks;

import org.jetbrains.annotations.Nullable;

public class DiggingBlock {
    private final int x;
    private final int y;
    private final int z;
    private final CustomBlock block;
    private double progression = 0;

    public DiggingBlock(int x, int y, int z, @Nullable CustomBlock block) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
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

    @Nullable
    public CustomBlock getBlock() {
        return block;
    }

    public double getProgression() {
        return progression;
    }

    public void setProgression(double progression) {
        this.progression = progression;
    }

    public void progress(double progression) {
        this.progression += progression;
    }
}
