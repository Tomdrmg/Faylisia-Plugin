package fr.blockincraft.faylisia.entity;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.player.Stats;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * A loot is an item with a probability to be harvest when kill mob or destroy block
 */
public record Loot(int rolls, @NotNull CustomItem item, int probability, int on, @NotNull AmountFunction amount) {
    private static final SecureRandom random = new SecureRandom();
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    /**
     * Calculate if an item will be generated or no using probability and rolls of this loot, it will be also calculate
     * how many items will be generated
     * @return items generated
     */
    @NotNull
    public ItemStack[] generate() {
        List<ItemStack> loots = new ArrayList<>();

        // Do all rolls
        for (int i = 0; i < rolls; i++) {
            int r = random.nextInt(on);

            // Calculate if it will be generated
            if (r < probability) {
                // Calculate amount of item
                int amount = this.amount.getAmount();

                while (amount > item.getMaterial().getMaxStackSize()) {
                    loots.add(new CustomItemStack(item, item.getMaterial().getMaxStackSize()).getAsItemStack());
                    amount -= item.getMaterial().getMaxStackSize();
                }

                if (amount > 0) {
                    loots.add(new CustomItemStack(item, amount).getAsItemStack());
                }
            }
        }

        return loots.toArray(new ItemStack[0]);
    }

    /**
     * Calculate if an item will be generated for the player or no using probability and rolls of this loot, it will
     * also calculate how many items will be generated. <br/>
     * By using player we also use player handlers and {@link Stats} of it
     * @return items generated
     */
    @NotNull
    public ItemStack[] generateFor(@NotNull Player player) {
        // Calculate probability of generation with player stats
        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());
        int probability = this.probability;
        boolean rare = 100.0 / on * probability <= 5;
        if (rare) {
            long luck = (long) customPlayer.getStat(Stats.LUCK);
            probability *= 1 + luck / 100;
        }

        // Retrieve player handlers
        Handlers mainHandHandler = customPlayer.getMainHandHandler();
        Handlers[] armorSetHandlers = customPlayer.getArmorSetHandlers();
        Handlers[] armorSlotHandlers = customPlayer.getArmorSlotHandlers();
        Handlers[] othersHandlers = customPlayer.getOthersHandlers();

        // Apply handlers on probability
        if (mainHandHandler != null) {
            probability = mainHandHandler.getLootProbability(player, item, probability, this.rolls, rare, true, false);
        }
        for (Handlers handlers : armorSetHandlers) {
            probability = handlers.getLootProbability(player, item, probability, this.rolls, rare, false, true);
        }
        for (Handlers handlers : armorSlotHandlers) {
            probability = handlers.getLootProbability(player, item, probability, this.rolls, rare, false, true);
        }
        for (Handlers handlers : othersHandlers) {
            probability = handlers.getLootProbability(player, item, probability, this.rolls, rare, false, false);
        }

        List<ItemStack> loots = new ArrayList<>();

        // Calculate rolls and apply handlers on it
        int rolls = this.rolls;

        if (mainHandHandler != null) {
            rolls = mainHandHandler.getLootRolls(player, item, probability, rolls, rare, true, false);
        }
        for (Handlers handlers : armorSetHandlers) {
            rolls = handlers.getLootRolls(player, item, probability, rolls, rare, false, true);
        }
        for (Handlers handlers : armorSlotHandlers) {
            rolls = handlers.getLootRolls(player, item, probability, rolls, rare, false, true);
        }
        for (Handlers handlers : othersHandlers) {
            rolls = handlers.getLootRolls(player, item, probability, rolls, rare, false, false);
        }

        // Do all rolls
        for (int i = 0; i < rolls; i++) {
            int r = random.nextInt(on);

            // Calculate if item will be generated
            if (r < probability) {
                // Then calculate amount and apply handlers on it
                int amount = this.amount.getAmount();
                if (mainHandHandler != null) {
                    amount = mainHandHandler.getLootAmount(player, item, probability, amount, rolls, rare, true, false);
                }
                for (Handlers handlers : armorSetHandlers) {
                    amount = handlers.getLootAmount(player, item, probability, amount, rolls, rare, false, true);
                }
                for (Handlers handlers : armorSlotHandlers) {
                    amount = handlers.getLootAmount(player, item, probability, amount, rolls, rare, false, true);
                }
                for (Handlers handlers : othersHandlers) {
                    amount = handlers.getLootAmount(player, item, probability, amount, rolls, rare, false, false);
                }

                while (amount > item.getMaterial().getMaxStackSize()) {
                    loots.add(new CustomItemStack(item, item.getMaterial().getMaxStackSize()).getAsItemStack());
                    amount -= item.getMaterial().getMaxStackSize();
                }

                if (amount > 0) {
                    loots.add(new CustomItemStack(item, amount).getAsItemStack());
                }
            }
        }

        return loots.toArray(new ItemStack[0]);
    }

    /**
     * Function to get amount of item generated
     */
    public interface AmountFunction {
        int getAmount();
    }
}
