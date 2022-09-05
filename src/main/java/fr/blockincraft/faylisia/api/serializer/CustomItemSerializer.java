package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.blockincraft.faylisia.items.BaseEnchantedItem;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.DamageItem;
import fr.blockincraft.faylisia.items.StatsItem;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.json.EnchantmentDeserializer;
import fr.blockincraft.faylisia.items.json.EnchantmentSerializer;
import fr.blockincraft.faylisia.player.Stats;

import java.io.IOException;
import java.util.Map;

public class CustomItemSerializer extends JsonSerializer<CustomItem> {
    @Override
    public void serialize(CustomItem value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("id", value.getId());
        gen.writeObjectField("registered", value.isRegistered());
        gen.writeObjectField("material", value.getMaterial());
        gen.writeObjectField("customModelData", value.getCustomModelData());
        gen.writeObjectField("name", value.getName());
        gen.writeObjectField("lore", value.getLore());
        gen.writeObjectField("color", value.getColor());
        gen.writeObjectField("enchantable", value.isEnchantable());
        gen.writeObjectField("disenchantable", value.isDisenchantable());
        gen.writeObjectField("rarity", value.getRarity());
        gen.writeObjectField("recipes", value.getRecipes());
        gen.writeObjectField("category", value.getCategory());

        if (value instanceof DamageItem damageItem) {
            gen.writeObjectField("damage", damageItem.getDamage());
        }

        if (value instanceof StatsItem statsItem) {
            gen.writeArrayFieldStart("stats");

            for (Map.Entry<Stats, Double> entry : statsItem.getStats().entrySet()) {
                gen.writeStartObject();

                gen.writeObjectField(entry.getKey().name(), entry.getValue());

                gen.writeEndObject();
            }

            gen.writeEndArray();
        }

        if (value instanceof ArmorItem armorItem) {
            gen.writeObjectField("armor_set", armorItem.getArmorSet().getId());
        }

        if (value instanceof BaseEnchantedItem enchantedItem) {
            new EnchantmentSerializer().serialize(enchantedItem.getEnchantments(), gen, serializers, false);
        }

        gen.writeEndObject();
    }
}
