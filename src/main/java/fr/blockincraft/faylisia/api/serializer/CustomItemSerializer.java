package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.enchantment.BaseEnchantedItemModel;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.weapons.AbilityItemModel;
import fr.blockincraft.faylisia.items.weapons.DamageItemModel;
import fr.blockincraft.faylisia.items.StatsItemModel;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
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

        if (value instanceof DamageItemModel damageItemModel) {
            gen.writeObjectField("damage", damageItemModel.getDamage());
        }

        if (value instanceof StatsItemModel statsItemModel) {
            gen.writeArrayFieldStart("stats");

            for (Map.Entry<Stats, Double> entry : statsItemModel.getStats().entrySet()) {
                gen.writeStartObject();

                gen.writeObjectField(entry.getKey().name(), entry.getValue());

                gen.writeEndObject();
            }

            gen.writeEndArray();
        }

        if (value instanceof ArmorItem armorItem) {
            gen.writeObjectField("armorSet", armorItem.getArmorSet().getId());
        }

        if (value instanceof BaseEnchantedItemModel enchantedItem) {
            new EnchantmentSerializer().serialize(enchantedItem.getEnchantments(), gen, serializers, false);
        }

        if (value instanceof AbilityItemModel abilityItemModel) {
            gen.writeObjectField("abilityName", abilityItemModel.getAbilityName());
            gen.writeObjectField("abilityDesc", abilityItemModel.getAbilityDesc());
            gen.writeObjectField("abilityUseCost", abilityItemModel.getUseCost());
            gen.writeObjectField("abilityCooldown", abilityItemModel.getCooldown());
        }

        gen.writeEndObject();
    }
}
