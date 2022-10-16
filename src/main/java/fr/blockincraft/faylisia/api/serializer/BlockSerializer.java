package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.blocks.BlockType;
import fr.blockincraft.faylisia.blocks.CustomBlock;

import java.io.IOException;
import java.util.Arrays;

public class BlockSerializer extends JsonSerializer<CustomBlock> {
    @Override
    public void serialize(CustomBlock value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("x", value.getX());
        gen.writeObjectField("y", value.getY());
        gen.writeObjectField("z", value.getZ());
        gen.writeObjectField("world", value.getWorld().toString());
        gen.writeObjectField("restartAtRegen", value.doRestartAtRegen());
        gen.writeObjectField("finalMaterial", value.getFinalMaterial().name());
        gen.writeObjectField("blockTypes", Arrays.stream(value.getStates()).map(BlockType::getId).toList().toArray(new String[0]));

        gen.writeEndObject();
    }
}
