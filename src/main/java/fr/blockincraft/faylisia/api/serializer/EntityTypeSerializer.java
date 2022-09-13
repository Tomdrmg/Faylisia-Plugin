package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.entity.CustomEntityType;

import java.io.IOException;

public class EntityTypeSerializer extends JsonSerializer<CustomEntityType> {
    @Override
    public void serialize(CustomEntityType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("id", value.getId());
        gen.writeObjectField("entityType", value.getEntityType());
        gen.writeObjectField("name", value.getName());
        gen.writeObjectField("rank", value.getRank());
        gen.writeObjectField("damage", value.getDamage());
        gen.writeObjectField("maxHealth", value.getMaxHealth());
        gen.writeObjectField("region", value.getRegion());
        gen.writeObjectField("tickBeforeRespawn", value.getTickBeforeRespawn());
        gen.writeObjectField("loots", value.getLoots());

        gen.writeEndObject();
    }
}
