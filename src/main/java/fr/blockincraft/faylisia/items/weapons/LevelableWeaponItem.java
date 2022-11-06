package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.items.level.LevelableItemModel;
import fr.blockincraft.faylisia.utils.TextUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LevelableWeaponItem extends WeaponItem implements LevelableItemModel {
    private Handlers levelHandlers = new Handlers() {};
    private List<Long> levels = new ArrayList<>();

    public LevelableWeaponItem(@NotNull Material material, @NotNull String id) throws InvalidBuildException {
        super(material, id);
    }

    @Override
    @NotNull
    protected List<String> moreLore(CustomItemStack customItemStack) {
        List<String> lore = new ArrayList<>();

        int level = getLevel(customItemStack.getExperience(), customItemStack);

        lore.add("");
        lore.add("Niveau " + TextUtils.intToRoman(level) + "/" + TextUtils.intToRoman(levels.size() + 1));
        if (level < levels.size() + 1) {
            lore.add("Prochain Niveau : " + TextUtils.valueWithCommas(customItemStack.getExperience()) + "/" + TextUtils.valueWithCommas(level - 1));
        }

        return lore;
    }

    @Override
    @NotNull
    public Handlers getHandlers(CustomItemStack customItemStack) {
        return levelHandlers;
    }

    @Override
    public int getLevel(long xp, CustomItemStack customItemStack) {
        for (int i = 0; i < levels.size(); i++) {
            if (xp >= levels.get(i)) {
                return i + 1;
            }
        }

        return levels.size() + 1;
    }

    @Override
    public long getXp(int level, CustomItemStack customItemStack) {
        return levels.size() > level ? levels.get(level) : -1;
    }

    @Override
    public List<Long> getLevels(CustomItemStack customItemStack) {
        return levels;
    }

    public void setLevelHandlers(@NotNull Handlers levelHandlers) {
        if (isRegistered()) throw new ChangeRegisteredItem();

        this.levelHandlers = levelHandlers;
    }

    public void setLevels(@NotNull List<Long> levels) {
        if (isRegistered()) throw new ChangeRegisteredItem();

        this.levels = levels.stream().sorted((o1, o2) -> (int) (o1 - o2)).toList();
    }
}
