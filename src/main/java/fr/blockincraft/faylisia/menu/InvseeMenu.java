package fr.blockincraft.faylisia.menu;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InvseeMenu extends ChestMenu {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final int[] slotsEquivalent = new int[]{
            45, 46, 47, 48, 49, 50, 51, 52, 53,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44,
            4, 3, 2, 1, 7
    };
    private final Player player;

    public InvseeMenu(@NotNull Player player) {
        super("&d&lInventaire de " + registry.getOrRegisterPlayer(player.getUniqueId()).getNameToUse(), 6);
        this.player = player;

        this.setEmptySlotsClickable(true);
        this.setPlayerInventoryClickable(true);

        this.addPlayerInventoryClickHandler(e -> {
            switch (e.getAction()) {
                case MOVE_TO_OTHER_INVENTORY, COLLECT_TO_CURSOR -> {
                    return false;
                }
            }

            return true;
        });

        refreshMenu();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void refreshMenu() {
        PlayerInventory inv = player.getInventory();

        for (int i = 0; i <= 40; i++) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack == null) itemStack = new ItemStack(Material.AIR);

            int finalI = i;

            this.replaceExistingItem(slotsEquivalent[i], itemStack.clone(), e -> {
                if (finalI >= 36 && finalI <= 39) {
                    List<Material> placeableItem = switch (finalI) {
                        case 36, 37, 38 -> List.of(
                                Material.LEATHER_CHESTPLATE,
                                Material.LEATHER_LEGGINGS,
                                Material.LEATHER_BOOTS,
                                Material.CHAINMAIL_CHESTPLATE,
                                Material.CHAINMAIL_LEGGINGS,
                                Material.CHAINMAIL_BOOTS,
                                Material.IRON_CHESTPLATE,
                                Material.IRON_LEGGINGS,
                                Material.IRON_BOOTS,
                                Material.GOLDEN_CHESTPLATE,
                                Material.GOLDEN_LEGGINGS,
                                Material.GOLDEN_BOOTS,
                                Material.DIAMOND_CHESTPLATE,
                                Material.DIAMOND_LEGGINGS,
                                Material.DIAMOND_BOOTS,
                                Material.NETHERITE_CHESTPLATE,
                                Material.NETHERITE_LEGGINGS,
                                Material.NETHERITE_BOOTS);
                        case 39 -> List.of(Material.values());
                        default -> List.of();
                    };

                    switch (e.getAction()) {
                        case SWAP_WITH_CURSOR, PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                            if (e.getCursor() != null && (e.getCursor().getAmount() > 1 || !placeableItem.contains(e.getCursor().getType()))) {
                                return false;
                            }
                        }
                        case HOTBAR_SWAP -> {
                            ItemStack itemStackIn = inv.getItem(e.getHotbarButton());
                            if (itemStackIn != null && (itemStackIn.getAmount() > 1 || !placeableItem.contains(itemStackIn.getType()))) {
                                return false;
                            }
                        }
                    }
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                    inv.setItem(finalI, getItemInSlot(slotsEquivalent[finalI]));
                }, 1);
                return true;
            });
        }
    }
}
