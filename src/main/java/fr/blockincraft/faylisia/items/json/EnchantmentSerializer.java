package fr.blockincraft.faylisia.items.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;

import java.io.IOException;
import java.util.Map;

public class EnchantmentSerializer extends JsonSerializer<Map<CustomEnchantments, Integer>> {
    @Override
    public void serialize(Map<CustomEnchantments, Integer> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        serialize(value, gen, serializers, true);
    }

    public void serialize(Map<CustomEnchantments, Integer> value, JsonGenerator gen, SerializerProvider serializers, boolean withObject) throws IOException {
        if (withObject) gen.writeStartObject();

        gen.writeArrayFieldStart("enchantments");

        for (Map.Entry<CustomEnchantments, Integer> entry : value.entrySet()) {
            gen.writeStartObject();

            gen.writeObjectField("enchant", entry.getKey().name());
            gen.writeObjectField("level", entry.getValue());

            gen.writeEndObject();
        }

        gen.writeEndArray();

        if (withObject) gen.writeEndObject();
    }
}
