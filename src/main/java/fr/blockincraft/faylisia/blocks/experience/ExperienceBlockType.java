package fr.blockincraft.faylisia.blocks.experience;

import fr.blockincraft.faylisia.blocks.BlockType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ExperienceBlockType extends BlockType implements ExperienceBlockModel {
    public ExperienceBlockType(@NotNull String id, @NotNull Material material) {
        super(id, material);
    }

    @Override
    @NotNull
    public ExperienceProvider getExperienceProvider() {
        return (experienceTarget, experienceTypes) -> 0;
    }
}
