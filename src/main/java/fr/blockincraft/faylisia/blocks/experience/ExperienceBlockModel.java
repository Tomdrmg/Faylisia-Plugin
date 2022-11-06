package fr.blockincraft.faylisia.blocks.experience;

import fr.blockincraft.faylisia.items.level.ExperienceTarget;
import fr.blockincraft.faylisia.items.level.ExperienceType;
import org.jetbrains.annotations.NotNull;

public interface ExperienceBlockModel {
    @NotNull
    ExperienceProvider getExperienceProvider();

    @FunctionalInterface
    interface ExperienceProvider {
        long getExperience(@NotNull ExperienceTarget experienceTarget, @NotNull ExperienceType[] experienceTypes);
    }
}
