package fr.blockincraft.faylisia.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PacketFilterManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.armor.ArmorItem;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.player.Stats;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerUtils {
    private static final ProtocolManager protocolManager = Faylisia.getInstance().getProtocolManager();
    private static final SecureRandom random = new SecureRandom();

    public static final void giveOrDrop(Player player, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        if (player == null) return;

        PlayerInventory inventory = player.getInventory();

        int maxToAdd = 0;

        for (ItemStack slot : inventory.getStorageContents()) {
            if (slot == null || slot.getType() == Material.AIR) {
                maxToAdd = itemStack.getAmount();
                break;
            } else {
                if (itemStack.isSimilar(slot)) {
                    maxToAdd += itemStack.getMaxStackSize() - slot.getAmount();
                    if (maxToAdd >= itemStack.getAmount()) {
                        maxToAdd = itemStack.getAmount();
                        break;
                    }
                }
            }
        }

        ItemStack copy = itemStack.clone();
        copy.setAmount(maxToAdd);
        inventory.addItem(copy);

        if (itemStack.getAmount() - maxToAdd > 0) {
            itemStack.setAmount(itemStack.getAmount() - maxToAdd);
            Item item = player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
            item.setOwner(player.getUniqueId());
        }
    }

    public static void spawnDamageIndicator(long damage, boolean critic, Player player, Location location) {
        StringBuilder sb = new StringBuilder();
        sb.append(damage);
        int l = sb.length();
        int commas = 0;
        for (int i = 0; i < l; i++) {
            sb.insert(i + i * 2 + commas * 3, (i + 1) % 3 == 0 ? "&f" : (i + 2) % 3 == 0 ? "&e" : "&6");
            if ((l - 1 - i) % 3 == 0 && i + 1 < l) {
                sb.insert(i + 2 + 1 + i * 2 + commas * 3, "&7,");
                commas++;
            }
        }

        Location finalLocation = location.add((double) (random.nextInt(100) - 1) / 100, ((double) (random.nextInt(100) - 1) / 100) + 0.5, (double) (random.nextInt(100) - 1) / 100);

        int id = (int) (Math.random() * Integer.MAX_VALUE);

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        // Entity ID
        packet.getIntegers().write(0, id);
        // Set optional velocity (/8000)
        packet.getIntegers().write(1, 0);
        packet.getIntegers().write(2, 0);
        packet.getIntegers().write(3, 0);
        // Set yaw pitch
        packet.getIntegers().write(4, 0);
        packet.getIntegers().write(5, 0);
        // Set object data
        packet.getIntegers().write(6, 0);
        // Set location
        packet.getDoubles().write(0, finalLocation.getX());
        packet.getDoubles().write(1, finalLocation.getY());
        packet.getDoubles().write(2, finalLocation.getZ());
        // Set UUID
        packet.getUUIDs().write(0, UUID.randomUUID());
        // Set type
        packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);

        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

        WrappedDataWatcher metadata = new WrappedDataWatcher();
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', (critic ? "&6⁂ " : "") + sb)).getHandle()));
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker

        metadataPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());

        metadataPacket.getIntegers().write(0, id);

        try {
            protocolManager.sendServerPacket(player, packet);
            protocolManager.sendServerPacket(player, metadataPacket);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                PacketContainer removePacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                List<Integer> ids = new ArrayList<>();

                ids.add(id);

                removePacket.getIntLists().write(0, ids);

                try {
                    protocolManager.sendServerPacket(player, removePacket);
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }, 10);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
