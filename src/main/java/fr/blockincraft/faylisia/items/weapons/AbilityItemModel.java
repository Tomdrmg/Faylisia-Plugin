package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.event.HandlerItemModel;
import fr.blockincraft.faylisia.items.event.Handlers;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Date;

public interface AbilityItemModel extends HandlerItemModel {
    @NotNull
    Ability getAbility(CustomItemStack customItemStack);

    @NotNull
    String getAbilityName(CustomItemStack customItemStack);

    @NotNull
    String[] getAbilityDesc(CustomItemStack customItemStack);

    long getUseCost(CustomItemStack customItemStack);

    int getCooldown(CustomItemStack customItemStack);

    /**
     * Make {@link Handlers} to listen right click to use ability
     * @return created handlers
     */
    @Override
    @NotNull
    default Handlers getHandlers(CustomItemStack customItemStack) {
        if (!(this instanceof CustomItem customItem)) return new Handlers() {};

        return new Handlers() {
            @Override
            public void onInteract(@NotNull Player player, @Nullable Block clickedBlock, boolean isRightClick, @NotNull EquipmentSlot hand, boolean inHand, boolean inArmorSlot, @Nullable CustomItemStack thisItemStack) {
                onHandlerCall();
                CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());

                if (inHand && isRightClick) {
                    Long lastUse = customPlayer.getLastUse(customItem);
                    if (lastUse != null) {
                        if (Date.from(Instant.now()).getTime() - lastUse < 100) {
                            // Do this to prevent double interaction when clicking a block
                            return;
                        } else if (Date.from(Instant.now()).getTime() - lastUse < getCooldown(customItemStack) * 1000L) {
                            // Todo: send message
                            return;
                        }
                    }

                    if (customPlayer.getMagicalReserve() >= getUseCost(customItemStack)) {
                        if (!getAbility(customItemStack).useAbility(player, clickedBlock, hand)) {
                            customPlayer.setMagicalReserve(customPlayer.getMagicalReserve() - getUseCost(customItemStack));
                            customPlayer.use(customItem);
                            //Todo: send message
                        }
                    } else {
                        customPlayer.use(customItem, getCooldown(customItemStack) * 1000L - 100);
                        // Todo: send message
                    }
                }
            }
        };
    }

    @FunctionalInterface
    interface Ability {
        /**
         * Function called when player interact with the item in the hand and if the click is a right click. <br/>
         * If ability was cancelled, it doesn't cost any éthernanos
         * @param player player which use the ability
         * @param clickedBlock block clicked by the player
         * @param hand hand used on click
         * @return if ability need to be cancelled
         */
        boolean useAbility(@NotNull Player player, @Nullable Block clickedBlock, @NotNull EquipmentSlot hand);
    }
}
