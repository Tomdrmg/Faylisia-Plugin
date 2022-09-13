package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.entity.Loot;

import java.io.IOException;

public class LootSerializer extends JsonSerializer<Loot> {
    @Override
    public void serialize(Loot value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("item", value.item().getId());
        gen.writeObjectField("probability", value.probability());
        gen.writeObjectField("on", value.on());

        gen.writeEndObject();
    }
}
