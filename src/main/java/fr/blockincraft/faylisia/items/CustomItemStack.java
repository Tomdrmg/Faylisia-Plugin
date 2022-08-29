package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.Faylisia;
import org.bukkit.inventory.ItemStack;

public class CustomItemStack {
    private final CustomItem item;
    private final int amount;

    public CustomItemStack(CustomItem item, int amount) {
        if (item == null) throw new RuntimeException("Item cannot be null");

        this.item = item;
        if (amount < 1) amount = 1;
        if (amount > 64) amount = 64;
        this.amount = amount;
    }

    public CustomItem getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public static CustomItemStack fromItemStack(ItemStack model) {
        CustomItem item = Faylisia.getInstance().getRegistry().getByItemStack(model);
        if (item == null) return null;
        int amount = model.getAmount();

        return new CustomItemStack(item, amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomItemStack customItemStack) {
            return item.getId().equals(customItemStack.item.getId()) && amount == customItemStack.amount;
        }
        return false;
    }
}
