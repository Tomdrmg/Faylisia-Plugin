package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WeaponAbilityItem extends WeaponItem implements AbilityItem {
    private Ability ability = (player, clickedBlock, hand) -> {};
    private String abilityName = "";
    private String[] abilityDesc = new String[0];
    private long useCost = 0;
    private int cooldown = 0;

    public WeaponAbilityItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    /**
     * Add ability information
     * @return text to add
     */
    @Override
    @NotNull
    protected List<String> moreLore() {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ColorsUtils.translateAll("&dAbilité - " + abilityName + ":"));

        for (String descPart : abilityDesc) {
            lore.add(ColorsUtils.translateAll(descPart));
        }

        if (useCost > 0) lore.add(ColorsUtils.translateAll("&8Coût d'éthernano: &c" + useCost));
        if (cooldown > 0) lore.add(ColorsUtils.translateAll("&8Délai: " + cooldown + "s"));

        return lore;
    }

    /**
     * Change item ability
     * @param ability new value
     * @return this instance
     */
    @NotNull
    public WeaponAbilityItem setAbility(@NotNull Ability ability) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        this.ability = ability;
        return this;
    }

    /**
     * Change item ability name
     * @param abilityName new value
     * @return this instance
     */
    @NotNull
    public WeaponAbilityItem setAbilityName(@NotNull String abilityName) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        this.abilityName = abilityName;
        return this;
    }

    /**
     * Change item ability description
     * @param abilityDesc new value
     * @return this instance
     */
    @NotNull
    public WeaponAbilityItem setAbilityDesc(@NotNull String... abilityDesc) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        this.abilityDesc = abilityDesc == null ? new String[0] : abilityDesc;
        return this;
    }

    /**
     * Change item ability use cost
     * @param useCost new value
     * @return this instance
     */
    @NotNull
    public WeaponAbilityItem setUseCost(long useCost) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        if (useCost < 0) useCost = 0;
        this.useCost = useCost;
        return this;
    }

    /**
     * Change item ability cooldown in seconds
     * @param cooldown new value
     * @return this instance
     */
    public WeaponAbilityItem setCooldown(int cooldown) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        if (cooldown < 0) cooldown = 0;
        this.cooldown = cooldown;
        return this;
    }

    @NotNull
    public Ability getAbility() {
        return ability;
    }

    @NotNull
    public String getAbilityName() {
        return abilityName;
    }

    @NotNull
    public String[] getAbilityDesc() {
        return abilityDesc;
    }

    public long getUseCost() {
        return useCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    /**
     * Change item type to weapon
     * @return new item type
     */
    @Override
    @NotNull
    protected String getType() {
        return "ARME MAGIQUE";
    }
}
