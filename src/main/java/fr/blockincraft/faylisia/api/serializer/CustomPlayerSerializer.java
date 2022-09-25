package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;

import java.io.IOException;

public class CustomPlayerSerializer extends JsonSerializer<CustomPlayerDTO> {
    @Override
    public void serialize(CustomPlayerDTO value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("uuid", value.getPlayer());
        gen.writeObjectField("customName", value.getCustomName());
        gen.writeObjectField("customNameEnabled", value.isCustomNameEnabled());
        gen.writeObjectField("lastName", value.getLastName());
        gen.writeObjectField("rank", value.getRank().name());
        gen.writeObjectField("class", value.getClasses().name());
        gen.writeFieldName("inventory");
        gen.writeRawValue(value.getLastInventoryAsJson());
        gen.writeObjectField("lastUpdate", value.getLastUpdate());

        gen.writeEndObject();
    }
}
