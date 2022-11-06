package fr.blockincraft.faylisia.entity.loot;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.utils.HandlersUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * A loot is an item with a probability to be harvest when kill mob or destroy block
 */
public record Loot(int rolls, @NotNull CustomItemStack item, int probability, int on, @NotNull AmountFunction amount, @NotNull LootType lootType) {
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

            CustomItemStack copy = item.clone();

            // Calculate if it will be generated
            if (r < probability) {
                // Calculate amount of item
                int amount = this.amount.getAmount();

                while (amount > item.getItem().getMaterial().getMaxStackSize()) {
                    copy.setAmount(item.getItem().getMaterial().getMaxStackSize());
                    loots.add(copy.getAsItemStack());
                    amount -= item.getItem().getMaterial().getMaxStackSize();
                }

                if (amount > 0) {
                    copy.setAmount(amount);
                    loots.add(copy.getAsItemStack());
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

        probability = HandlersUtils.getValueWithHandlers(customPlayer, "getLootProbability", probability, int.class, new HandlersUtils.Parameter[]{
                new HandlersUtils.Parameter(player, Player.class),
                new HandlersUtils.Parameter(item, CustomItemStack.class),
                new HandlersUtils.Parameter(on, int.class),
                new HandlersUtils.Parameter(this.rolls, int.class),
                new HandlersUtils.Parameter(rare, boolean.class),
                new HandlersUtils.Parameter(lootType, LootType.class)
        });

        List<ItemStack> loots = new ArrayList<>();

        // Calculate rolls and apply handlers on it
        int rolls = this.rolls;

        rolls = HandlersUtils.getValueWithHandlers(customPlayer, "getLootRolls", rolls, int.class, new HandlersUtils.Parameter[]{
                new HandlersUtils.Parameter(player, Player.class),
                new HandlersUtils.Parameter(item, CustomItemStack.class),
                new HandlersUtils.Parameter(probability, int.class),
                new HandlersUtils.Parameter(on, int.class),
                new HandlersUtils.Parameter(rare, boolean.class),
                new HandlersUtils.Parameter(lootType, LootType.class)
        });

        // Do all rolls
        for (int i = 0; i < rolls; i++) {
            int r = random.nextInt(on);

            // Calculate if item will be generated
            if (r < probability) {
                // Then calculate amount and apply handlers on it
                int amount = this.amount.getAmount();
                if (lootType == LootType.BLOCK) {
                    amount = (int) (amount * (customPlayer.getStat(Stats.MINING_FORTUNE) / 100.0));
                }

                amount = HandlersUtils.getValueWithHandlers(customPlayer, "getLootAmount", amount, int.class, new HandlersUtils.Parameter[]{
                        new HandlersUtils.Parameter(player, Player.class),
                        new HandlersUtils.Parameter(item, CustomItemStack.class),
                        new HandlersUtils.Parameter(probability, int.class),
                        new HandlersUtils.Parameter(on, int.class),
                        new HandlersUtils.Parameter(rolls, int.class),
                        new HandlersUtils.Parameter(rare, boolean.class),
                        new HandlersUtils.Parameter(lootType, LootType.class)
                });

                CustomItemStack copy = item.clone();

                while (amount > item.getItem().getMaterial().getMaxStackSize()) {
                    copy.setAmount(item.getItem().getMaterial().getMaxStackSize());
                    loots.add(copy.getAsItemStack());
                    amount -= item.getItem().getMaterial().getMaxStackSize();
                }

                if (amount > 0) {
                    copy.setAmount(amount);
                    loots.add(copy.getAsItemStack());
                }
            }
        }

        return loots.toArray(new ItemStack[0]);
    }

    public enum LootType {
        MOB,
        BLOCK
    }

    /**
     * Function to get amount of item generated
     */
    public interface AmountFunction {
        int getAmount();
    }
}
