package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.armor.ArmorSet;

import java.io.IOException;

public class ArmorSetSerializer extends JsonSerializer<ArmorSet> {
    @Override
    public void serialize(ArmorSet value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("id", value.getId());
        gen.writeObjectField("bonus", value.getBonus());

        gen.writeEndObject();
    }
}
