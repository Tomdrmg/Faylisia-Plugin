package fr.blockincraft.faylisia.items.event;

import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public interface Handlers {
    default int getLootProbability(Player player, CustomItem item, int probability, boolean isRare, boolean inHand, boolean inArmorSlot) {
        return probability;
    }

    default int getLootAmount(Player player, CustomItem item, int probability, int amount, boolean isRare, boolean inHand, boolean inArmorSlot) {
        return amount;
    }

    default void onInteract(Player player, Material clickedBlock, boolean inHand, boolean inArmorSlot, boolean isRightClick, EquipmentSlot hand) {

    }

    default long onDamage(Player player, CustomEntity customEntity, long damage, boolean inHand, boolean inArmorSlot) {
        return damage;
    }

    default long onTakeDamage(Player player, CustomEntity customEntity, long damageTaken, boolean inHand, boolean inArmorSlot) {
        return damageTaken;
    }

    default double getDefaultStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    default long onRegenHealth(Player player, long regen, boolean inHand, boolean inArmorSlot) {
        return regen;
    }

    default long onRegenMagicalPower(Player player, long regen, boolean inHand, boolean inArmorSlot) {
        return regen;
    }

    default double calculateItemStat(Player player, CustomItem customItem, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    default double getStat(Player player, Stats stat, double value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    default long getDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    default long getRawDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    default long calculateHandRawDamage(Player player, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }

    default long calculateItemRawDamage(Player player, CustomItem customItem, long value, boolean inHand, boolean inArmorSlot) {
        return value;
    }
}
