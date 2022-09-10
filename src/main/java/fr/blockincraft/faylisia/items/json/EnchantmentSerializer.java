package fr.blockincraft.faylisia.items.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;

import java.io.IOException;
import java.util.Map;

/**
 * Serializer to convert enchantments of a {@link CustomItemStack} to a {@link String}
 */
public class EnchantmentSerializer extends JsonSerializer<Map> {
    @Override
    public void serialize(Map value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        serialize((Map<CustomEnchantments, Integer>) value, gen, serializers, true);
    }

    /**
     * Serialize item enchantments
     * @param withObject if we start with '{' and finish with '}'
     */
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
