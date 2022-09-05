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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Loot {
    private static final SecureRandom random = new SecureRandom();
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    private final int rolls;
    private final CustomItem item;
    private final int probability;
    private final int on;
    private final AmountFunction amount;

    public Loot(int rolls, CustomItem item, int probability, int on, AmountFunction amount) {
        this.rolls = rolls;
        this.item = item;
        this.probability = probability;
        this.on = on;
        this.amount = amount;
    }

    public ItemStack[] generate() {
        List<ItemStack> loots = new ArrayList<>();

        for (int i = 0; i < rolls; i++) {
            int r = random.nextInt(on);

            if (r < probability) {
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

    public ItemStack[] generateFor(Player player) {
        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());
        int probability = this.probability;
        boolean rare = 100.0 / on * probability <= 5;
        if (rare) {
            long luck = (long) customPlayer.getStat(Stats.LUCK);
            probability *= 1 + luck / 100;
        }

        Handlers mainHandHandler = customPlayer.getMainHandHandler();
        Handlers[] armorSetHandlers = customPlayer.getArmorSetHandlers();
        Handlers[] armorSlotHandlers = customPlayer.getArmorSlotHandlers();
        Handlers[] othersHandlers = customPlayer.getOthersHandlers();

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

        for (int i = 0; i < rolls; i++) {
            int r = random.nextInt(on);

            if (r < probability) {
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

    public interface AmountFunction {
        int getAmount();
    }
}
