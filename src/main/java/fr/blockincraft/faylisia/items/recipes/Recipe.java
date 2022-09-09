package fr.blockincraft.faylisia.items.recipes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.api.serializer.RecipeSerializer;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.menu.viewer.RecipeViewerMenu;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@JsonSerialize(using = RecipeSerializer.class)
public interface Recipe {
    /**
     * Define an id of menu to use this recipe, used as information in api
     * @return menu id
     */
    @NotNull
    String getMenuId();

    /**
     * @return all items used in the recipe
     */
    @NotNull
    CustomItemStack[] getItems();

    /**
     * @return Amount of item crafted
     */
    int getResultAmount();

    //Return the valid pattern used for the recipe

    /**
     * Check if a list of item stack matches with this recipe <br/>
     * Pattern used could be different for example a sword have three patterns but only one recipe
     * @param recipe items to check
     * @return used pattern or null if no one matches
     */
    @Nullable
    CustomItemStack[] matches(@NotNull ItemStack[] recipe);

    /**
     * Get all items used in the recipe and their slot in the guide, used to display recipe in {@link RecipeViewerMenu}
     * @return items and their associated slot
     */
    @NotNull
    Map<Integer, CustomItemStack> getForDisplay();

    /**
     * @return slot index which will contain result of recipe <br/>
     * Only used to display recipe in {@link RecipeViewerMenu}
     */
    int getResultSlot();
}
