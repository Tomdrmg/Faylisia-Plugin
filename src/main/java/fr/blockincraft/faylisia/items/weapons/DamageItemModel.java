package fr.blockincraft.faylisia.items.weapons;

import fr.blockincraft.faylisia.items.CustomItemStack;

/**
 * An item which has damage like weapons
 */
public interface DamageItemModel {
    /**
     * @return damages of item
     */
    int getDamage(CustomItemStack customItemStack);
}
