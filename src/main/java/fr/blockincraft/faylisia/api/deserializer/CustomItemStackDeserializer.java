package fr.blockincraft.faylisia.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.items.json.EnchantmentDeserializer;
import fr.blockincraft.faylisia.items.specificitems.EnchantmentLacrymaItem;

import java.io.IOException;
import java.util.Map;

public class CustomItemStackDeserializer extends StdDeserializer<CustomItemStack> {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    public CustomItemStackDeserializer() {
        this(null);
    }

    public CustomItemStackDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CustomItemStack deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonNode node = p.readValueAsTree();

        CustomItem item = registry.getItemsById().get(node.get("item").asText(""));
        int amount = node.get("amount").asInt(-1);

        if (item == null || amount == -1) return null;

        CustomItemStack customItemStack = new CustomItemStack(item, amount);

        if (item.isEnchantable()) {
            Map<CustomEnchantments, Integer> enchantments = new EnchantmentDeserializer().deserialize(node, "enchantments");

            if (enchantments != null) {
                enchantments.forEach(customItemStack::addEnchantment);
            }
        }

        if (item instanceof EnchantmentLacrymaItem) {
            Map<CustomEnchantments, Integer> storedEnchantments = new EnchantmentDeserializer().deserialize(node, "stored-enchantments");

            if (storedEnchantments != null) {
                storedEnchantments.forEach(customItemStack::addStoredEnchantment);
            }
        }

        return customItemStack;
    }
}
