package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.armor.ArmorSet;

import java.io.IOException;

public class BonusSerializer extends JsonSerializer<ArmorSet.Bonus> {
    @Override
    public void serialize(ArmorSet.Bonus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("name", value.name());
        gen.writeObjectField("desc", value.description());
        gen.writeObjectField("minimum", value.minimum());

        gen.writeEndObject();
    }
}
