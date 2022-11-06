package fr.blockincraft.faylisia.items.level;

import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.event.HandlerItemModel;

import java.util.List;

public interface LevelableItemModel extends HandlerItemModel {
    int getLevel(long xp, CustomItemStack customItemStack);

    long getXp(int level, CustomItemStack customItemStack);

    List<Long> getLevels(CustomItemStack customItemStack);
}
