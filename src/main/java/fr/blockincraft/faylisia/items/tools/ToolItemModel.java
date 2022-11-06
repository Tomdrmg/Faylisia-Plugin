package fr.blockincraft.faylisia.items.tools;

import fr.blockincraft.faylisia.items.CustomItemStack;

public interface ToolItemModel {
    /**
     * @return types of the tool
     */
    ToolType[] getToolTypes(CustomItemStack customItemStack);

    /**
     * @return level of block that tool can break
     */
    int getBreakingLevel(CustomItemStack customItemStack);
}
