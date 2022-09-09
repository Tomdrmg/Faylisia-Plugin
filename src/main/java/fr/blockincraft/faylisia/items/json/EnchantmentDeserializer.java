package fr.blockincraft.faylisia.items.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Deserializer to convert {@link String} to a list of enchantments of a {@link CustomItemStack}
 */
public class EnchantmentDeserializer extends StdDeserializer<Map<CustomEnchantments, Integer>> {
    public EnchantmentDeserializer() {
        this(null);
    }

    public EnchantmentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<CustomEnchantments, Integer> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        Map<CustomEnchantments, Integer> enchants = new HashMap<>();

        JsonNode node = parser.readValueAsTree();

        ArrayNode array = (ArrayNode) node.get("enchantments");

        for (JsonNode n : array) {
            String enchantName = n.get("enchant").asText();
            int level = n.get("level").asInt();

            for (CustomEnchantments enchant : CustomEnchantments.values()) {
                if (enchant.name().equals(enchantName)) {
                    enchants.put(enchant, level);
                }
            }
        }

        return enchants;
    }
}
