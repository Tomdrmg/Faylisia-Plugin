package fr.blockincraft.faylisia.items.tools;

public interface ToolItemModel {
    /**
     * @return types of the tool
     */
    ToolType[] getToolTypes();

    /**
     * @return level of block that tool can break
     */
    int getBreakingLevel();
}
