package fr.blockincraft.faylisia.items.recipes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.api.serializer.RecipeSerializer;
import fr.blockincraft.faylisia.items.CustomItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@JsonSerialize(using = RecipeSerializer.class)
public interface Recipe {
    String getMenuId();

    CustomItemStack[] getItems();

    int getResultAmount();

    //Return the valid pattern used for the recipe
    CustomItemStack[] matches(ItemStack[] recipe);

    Map<Integer, CustomItemStack> getForDisplay();

    int getResultSlot();
}
