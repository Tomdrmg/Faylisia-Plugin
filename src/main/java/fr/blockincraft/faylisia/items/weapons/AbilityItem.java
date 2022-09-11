package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.event.HandlerItem;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbilityItem extends WeaponItem implements HandlerItem {
    private Ability ability = (player, clickedBlock, hand) -> {};
    private String abilityName = "";
    private String[] abilityDesc = new String[0];
    private long useCost = 0;
    private int cooldown = 0;

    public AbilityItem(@NotNull Material material, @NotNull String id) {
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
    public AbilityItem setAbility(@NotNull Ability ability) {
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
    public AbilityItem setAbilityName(@NotNull String abilityName) {
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
    public AbilityItem setAbilityDesc(@NotNull String... abilityDesc) {
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
    public AbilityItem setUseCost(long useCost) {
        if (isRegistered()) throw new ChangeRegisteredItem();
        if (useCost < 0) useCost = 0;
        this.useCost = useCost;
        return this;
    }

    /**
     * Change item ability cooldown
     * @param cooldown new value
     * @return this instance
     */
    public AbilityItem setCooldown(int cooldown) {
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

    /**
     * Make {@link Handlers} to listen right click to use ability
     * @return created handlers
     */
    @Override
    @NotNull
    public Handlers getHandlers() {
        return new Handlers() {
            @Override
            public void onInteract(@NotNull Player player, @Nullable Block clickedBlock, boolean isRightClick, @NotNull EquipmentSlot hand, boolean inHand, boolean inArmorSlot) {
                CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());

                if (inHand && isRightClick) {
                    Long lastUse = customPlayer.getLastUse(AbilityItem.this);
                    if (lastUse != null) {
                        if (Date.from(Instant.now()).getTime() - lastUse < 100) {
                            // Do this to prevent double interaction when clicking a block
                            return;
                        } else if (Date.from(Instant.now()).getTime() - lastUse < cooldown * 1000L) {
                            // Todo: send message
                            return;
                        }
                    }

                    if (customPlayer.getMagicalReserve() >= useCost) {
                        customPlayer.setMagicalReserve(customPlayer.getMagicalReserve() - useCost);
                        ability.useAbility(player, clickedBlock, hand);
                        customPlayer.use(AbilityItem.this);
                        // Todo: send message
                    } else {
                        customPlayer.use(AbilityItem.this, cooldown * 1000L - 100);
                        // Todo: send message
                    }
                }
            }
        };
    }

    @FunctionalInterface
    public interface Ability {
        /**
         * Function called when player interact with the item in the hand and if the click is a right click
         * @param player player which use the ability
         * @param clickedBlock block clicked by the player
         * @param hand hand used on click
         */
        void useAbility(@NotNull Player player, @Nullable Block clickedBlock, @NotNull EquipmentSlot hand);
    }
}
