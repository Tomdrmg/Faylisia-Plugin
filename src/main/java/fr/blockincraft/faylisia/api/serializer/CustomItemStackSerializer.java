package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.json.EnchantmentSerializer;
import fr.blockincraft.faylisia.items.specificitems.EnchantmentLacrymaItem;

import java.io.IOException;

public class CustomItemStackSerializer extends JsonSerializer<CustomItemStack> {
    @Override
    public void serialize(CustomItemStack value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("item", value.getItem().getId());
        gen.writeObjectField("amount", value.getAmount());

        if (value.getItem().isEnchantable()) new EnchantmentSerializer().serialize(value.getEnchantments(), gen, serializers, false, "enchantments");
        if (value.getItem() instanceof EnchantmentLacrymaItem) new EnchantmentSerializer().serialize(value.getStoredEnchantments(), gen, serializers, false, "stored-enchantments");

        gen.writeEndObject();
    }
}
