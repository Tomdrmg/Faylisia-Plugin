package fr.blockincraft.faylisia.api.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.blocks.BlockType;
import fr.blockincraft.faylisia.blocks.BlockTypes;
import fr.blockincraft.faylisia.blocks.CustomBlock;
import org.bukkit.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockDeserializer extends StdDeserializer<CustomBlock> {
    public BlockDeserializer() {
        this(null);
    }

    public BlockDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CustomBlock deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.readValueAsTree();

        int x = node.get("x").asInt();
        int y = node.get("y").asInt();
        int z = node.get("z").asInt();
        UUID world = UUID.fromString(node.get("world").asText());
        boolean restart = node.get("restartAtRegen").asBoolean();
        String finalMaterialId = node.get("finalMaterial").asText();
        Material finalMaterial = null;

        for (Material m : Material.values()) {
            if (m.name().equals(finalMaterialId)) {
                finalMaterial = m;
                break;
            }
        }

        if (finalMaterial == null) throw new RuntimeException("Final material must be specified!");
        List<BlockType> types = new ArrayList<>();
        for (JsonNode n : ((ArrayNode) node.get("blockTypes"))) {
            BlockType type = Faylisia.getInstance().getRegistry().getBlockTypeById(n.asText());
            if (type == null) throw new RuntimeException("Block type not found! (\"" + n.asText() + "\")");
            types.add(type);
        }

        return new CustomBlock(x, y, z, world, restart, finalMaterial, types.toArray(new BlockType[0]));
    }
}
