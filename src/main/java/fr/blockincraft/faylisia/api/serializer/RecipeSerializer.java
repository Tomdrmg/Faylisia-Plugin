package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.recipes.CraftingRecipe;
import fr.blockincraft.faylisia.items.recipes.Recipe;

import java.io.IOException;

public class RecipeSerializer extends JsonSerializer<Recipe> {
    @Override
    public void serialize(Recipe value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("menu_id", value.getMenuId());
        if (value instanceof CraftingRecipe craftingRecipe) {
            gen.writeObjectField("pattern_type", craftingRecipe.getPatternType());
            if (craftingRecipe.getDirection() != null) {
                gen.writeObjectField("direction", craftingRecipe.getDirection());
            }
        }
        gen.writeArrayFieldStart("items");

        for (CustomItemStack item : value.getItems()) {
            if (item != null) {
                gen.writeStartObject();

                gen.writeObjectField("id", item.getItem().getId());
                gen.writeObjectField("amount", item.getAmount());

                gen.writeEndObject();
            } else {
                gen.writeObject(null);
            }
        }

        gen.writeEndArray();
        gen.writeObjectField("result_amount", value.getResultAmount());

        gen.writeEndObject();
    }
}
