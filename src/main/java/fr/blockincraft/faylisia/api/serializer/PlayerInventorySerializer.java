package fr.blockincraft.faylisia.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.blockincraft.faylisia.items.CustomItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;

public class PlayerInventorySerializer extends JsonSerializer<PlayerInventory> {
    @Override
    public void serialize(PlayerInventory value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        serialize(value, gen, true);
    }

    public void serialize(PlayerInventory value, JsonGenerator gen, boolean withObject) throws IOException {
        if (withObject) gen.writeStartObject();

        HumanEntity holder = value.getHolder();
        gen.writeObjectField("owner", holder == null ? null : holder.getUniqueId());

        gen.writeArrayFieldStart("content");

        for (int i = 0; i < value.getSize(); i++) {
            gen.writeStartObject();

            gen.writeObjectField("slot", i);
            gen.writeObjectField("item", CustomItemStack.fromItemStack(value.getItem(i)));

            gen.writeEndObject();
        }

        gen.writeEndArray();

        if (withObject) gen.writeEndObject();
    }
}
