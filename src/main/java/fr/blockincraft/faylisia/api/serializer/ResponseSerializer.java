package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.api.objects.Response;

import java.io.IOException;

public class ResponseSerializer extends JsonSerializer<Response> {
    @Override
    public void serialize(Response value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("success", value.isSuccess());
        if (value.isSuccess()) {
            gen.writeObjectField(value.getElementName(), value.getElement());
        } else {
            gen.writeObjectField("error", value.getError());
        }

        gen.writeEndObject();
    }
}
