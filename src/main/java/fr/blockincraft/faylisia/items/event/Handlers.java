package fr.blockincraft.faylisia.items.event;

import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.Loot;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.DamageItem;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * In order, the handlers are executed from the main hand items, armor sets, armor slots items then others items. <br/>
 * inHand equals if the item which has the handler is in main hand
 */
public interface Handlers {
    /**
     * Event called when probability to drop a {@link Loot} <br/>
     * This event was called after applying the <b>luck</b> {@link Stats}
     * @param player player which will get the loot
     * @param item item generated
     * @param probability current probability
     * @param baseRolls amount of rolls before applying any modifiers
     * @param isRare if base probability <= 5%
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return probability of the loot
     */
    default int getLootProbability(Player player, CustomItem item, int probability, int baseRolls, boolean isRare, boolean inHand, boolean inArmorSlot) {
        return probability;
    }

    /**
     * Event called when amount of a {@link Loot}
     * @param player player which will get the loot
     * @param item item generated
     * @param probability probability to get the loot
     * @param rolls amount of rolls
     * @param isRare if base probability <= 5%
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return rolls of the loot
     */
    default int getLootRolls(Player player, CustomItem item, int probability, int rolls, boolean isRare, boolean inHand, boolean inArmorSlot) {
        return rolls;
    }

    /**
     * Event called when amount of a {@link Loot} <br/>
     * This event was called after applying the {@link Loot.AmountFunction} of the loot
     * @param player player which will get the loot
     * @param item item generated
     * @param probability probability to get the loot
     * @param amount current amount (so the {@link Loot.AmountFunction} result)
     * @param rolls amount of rolls
     * @param isRare if base probability <= 5%
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return amount of the loot
     */
    default int getLootAmount(Player player, CustomItem item, int probability, int amount, int rolls, boolean isRare, boolean inHand, boolean inArmorSlot) {
        return amount;
    }

    /**
     * This event was called when a {@link Player} interact <br/>
     * It can be util to create abilities...
     * @param player player which interact
     * @param clickedBlock block interacted
     * @param inHand if the item witch has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @param isRightClick if the click is a right click
     * @param hand the hand of the interaction
     */
    default void onInteract(Player player, Block clickedBlock, boolean inHand, boolean inArmorSlot, boolean isRightClick, EquipmentSlot hand) {

    }

    /**
     * This event was called when a {@link Player} damage a {@link CustomEntity}
     * @param player player which do damages
     * @param customEntity entity which will receive damages
     * @param damage damages to do
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return damages to deal
     */
    default long onDamage(Player player, CustomEntity customEntity, long damage, boolean inHand, boolean inArmorSlot) {
        return damage;
    }

    /**
     * This event was called when a {@link Player} take damage from a {@link CustomEntity}
     * @param player player which receive the damages
     * @param customEntity entity which do damages
     * @param damageTaken damages to take
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return damages to receive
     */
    default long onTakeDamage(Player player, CustomEntity customEntity, long damageTaken, boolean inHand, boolean inArmorSlot) {
        return damageTaken;
    }

    /**
     * This event was called during stats calculation of a {@link CustomPlayer}, it was called when we get {@code defaultValue} of a {@link Stats}
     * @param player player which we calculate the stats
     * @param stat {@link Stats} which we get the {@code defaultValue}
     * @param value current value of the stat
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return value of the stat
     */
    default double getDefaultStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    /**
     * This event was called when a player regen his health
     * @param player player which regen
     * @param regen effective health which will be gain
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return effective health to gain
     */
    default long onRegenHealth(Player player, long regen, boolean inHand, boolean inArmorSlot) {
        return regen;
    }

    /**
     * This event was called when a player regen his magical power
     * @param player player which regen
     * @param regen magical power which will be gain
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return magical power to gain
     */
    default long onRegenMagicalPower(Player player, long regen, boolean inHand, boolean inArmorSlot) {
        return regen;
    }

    /**
     * This event was called when we get the stat of a {@link CustomItem}
     * @param player player which has the item
     * @param customItem item which we get the stat
     * @param stat {@link Stats} of the item which we want to get
     * @param value value of this stat
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return new value of the stat
     */
    default double calculateItemStat(Player player, CustomItem customItem, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    /**
     * This event was called when we get the stat of a {@link CustomPlayer}
     * @param player player which we get the stat
     * @param stat {@link Stats} of the player which we want to get
     * @param value value of the stat
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return value to return
     */
    default double getStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    /**
     * This event was called when we get damages of a {@link CustomPlayer} <br/>
     * {@link Stats} like strength, critic damages (if critic) are already applied
     * @param player player which we get the damages
     * @param value damages of the player
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return damages to return
     */
    default long getDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    /**
     * This event was called when we get the raw damages of a {@link CustomPlayer} <br/>
     * Raw damages are damages without applying {@link Stats}
     * @param player player which we get the raw damages
     * @param value raw damages of the player
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return raw damages to return
     */
    default long getRawDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    /**
     * This event was called when we get the hand raw damages (defined in {@link Stats} class) <br/>
     * Raw damages are damages without applying {@link Stats}
     * @param player player which we get the hand damages
     * @param value raw damages of hand
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return hand raw damages to return
     */
    default long calculateHandRawDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    /**
     * This event was called when we get item raw damages of a {@link DamageItem} <br/>
     * Raw damages are damages without applying {@link Stats}
     * @param player player which has the item
     * @param customItem item which we get the raw damages
     * @param value raw damages of the item
     * @param inHand if the item which has the handler is in hand
     * @param inArmorSlot if the item which has the handler is in an armor slot
     * @return raw damages to return
     */
    default long calculateItemRawDamage(Player player, CustomItem customItem, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }
}
