package fr.blockincraft.faylisia.items.tools;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ToolItem extends CustomItem implements ToolItemModel {
    private ToolType[] toolTypes = new ToolType[]{};
    private int breakingLevel = 0;

    public ToolItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    /**
     * Add breaking level to lore
     * @return text to add
     */
    @Override
    protected @NotNull List<String> firstLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ColorsUtils.translateAll("&7Niveau de forage: &6" + breakingLevel));

        return lore;
    }

    public ToolItem setToolTypes(@NotNull ToolType[] toolTypes) {
        if (isRegistered()) throw new ChangeRegisteredItem();

        this.toolTypes = toolTypes;
        return this;
    }

    public ToolItem setBreakingLevel(int breakingLevel) {
        if (isRegistered()) throw new ChangeRegisteredItem();

        this.breakingLevel = breakingLevel;
        return this;
    }

    @Override
    @NotNull
    public ToolType[] getToolTypes() {
        return toolTypes;
    }

    @Override
    public int getBreakingLevel() {
        return breakingLevel;
    }

    @Override
    protected @NotNull String getType() {
        return "OUTIL " + ToolType.nameOfMultiple(toolTypes, "ET", true);
    }
}
