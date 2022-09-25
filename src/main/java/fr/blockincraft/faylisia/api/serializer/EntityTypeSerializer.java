package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.entity.CustomEntityType;
import fr.blockincraft.faylisia.entity.interaction.DamageableEntityModel;
import fr.blockincraft.faylisia.entity.interaction.DifficultyEntityModel;
import fr.blockincraft.faylisia.entity.interaction.HostileEntityModel;
import fr.blockincraft.faylisia.entity.loot.LootableEntityModel;

import java.io.IOException;

public class EntityTypeSerializer extends JsonSerializer<CustomEntityType> {
    @Override
    public void serialize(CustomEntityType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("id", value.getId());
        gen.writeObjectField("entityType", value.getEntityType());
        gen.writeObjectField("name", value.getName());
        gen.writeObjectField("region", value.getRegion());
        gen.writeObjectField("tickBeforeRespawn", value.getTickBeforeRespawn());

        if (value instanceof DamageableEntityModel damageable) {
            gen.writeObjectField("maxHealth", damageable.getMaxHealth());
        }

        if (value instanceof DifficultyEntityModel difficulty) {
            gen.writeObjectField("level", difficulty.getLevel());
        }

        if (value instanceof HostileEntityModel hostile) {
            gen.writeObjectField("damage", hostile.getDamage());
        }

        if (value instanceof LootableEntityModel lootable) {
            gen.writeObjectField("loots", lootable.getLoots());
        }

        gen.writeEndObject();
    }
}
