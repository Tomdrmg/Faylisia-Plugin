package fr.blockincraft.faylisia.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.entity.CustomEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utilities methods to interact with players
 */
public class PlayerUtils {
    // Initialize plugin unique elements
    private static final ProtocolManager protocolManager = Faylisia.getInstance().getProtocolManager();
    private static final SecureRandom random = new SecureRandom();

    /**
     * Give a bukkit {@link ItemStack} to a player or drop it if {@link PlayerInventory} is full
     * @param player {@link Player} player which will receive items
     * @param itemStack bukkit {@link ItemStack} to give
     */
    public static void giveOrDrop(@NotNull Player player, @NotNull ItemStack itemStack) {
        // Check if bukkit item stack is AIR
        if (itemStack.getType() == Material.AIR) return;

        // Get inventory
        PlayerInventory inventory = player.getInventory();

        // Calculate how many items we can add in inventory
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

        // Create a copy of the bukkit item stack
        ItemStack copy = itemStack.clone();
        copy.setAmount(maxToAdd);
        inventory.addItem(copy);

        // If we need to add more items we drop them
        if (itemStack.getAmount() - maxToAdd > 0) {
            itemStack.setAmount(itemStack.getAmount() - maxToAdd);
            Item item = player.getWorld().dropItem(player.getLocation(), itemStack);
            item.setOwner(player.getUniqueId());
        }
    }

    /**
     * Spawn a client side only {@link ArmorStand} with custom name to show damage dealt to an {@link CustomEntity}
     * @param damage damage dealt
     * @param critic if hit is critic (to change display)
     * @param player bukkit {@link Player} which will see the {@link ArmorStand}
     * @param location location of the {@link ArmorStand} will spawn
     */
    public static void spawnDamageIndicator(long damage, boolean critic, @NotNull Player player, @NotNull Location location) {
        // Create the name of entity with colors and commas
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

        // Create new location with random values
        Location finalLocation = location.add((double) (random.nextInt(100) - 1) / 100, ((double) (random.nextInt(100) - 1) / 100) + 0.5, (double) (random.nextInt(100) - 1) / 100);

        // Generate entity id
        int id = (int) (Math.random() * Integer.MAX_VALUE);

        // Create packet to spawn entity
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

        // Create second packet to change entity metadata
        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

        // Apply all data
        WrappedDataWatcher metadata = new WrappedDataWatcher();
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromText(ColorsUtils.translateAll((critic ? "&6⁂ " : "") + sb)).getHandle()));
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker

        // Add data in packet
        metadataPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());

        // Set entity id
        metadataPacket.getIntegers().write(0, id);

        try {
            // Send packets
            protocolManager.sendServerPacket(player, packet);
            protocolManager.sendServerPacket(player, metadataPacket);

            // Then create a new packet to remove entity after 0.5 seconds
            Bukkit.getScheduler().scheduleSyncDelayedTask(Faylisia.getInstance(), () -> {
                // Create packet to remove entity
                PacketContainer removePacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                List<Integer> ids = new ArrayList<>();

                // Add entity id
                ids.add(id);

                // Set ids
                removePacket.getIntLists().write(0, ids);

                try {
                    // Send packet
                    protocolManager.sendServerPacket(player, removePacket);
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }, 10);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send a packet to the player to change the current block state to another one (animation view)
     * @param player player which will receive packet
     * @param x x coordinate of block
     * @param y x coordinate of block
     * @param z x coordinate of block
     * @param state new value (0 - 9 for animation and 10 or other to remove animation)
     */
    public static void setBlockBreakingState(Player player, int x, int y, int z, int state) {
        PacketContainer packet = Faylisia.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);

        packet.getIntegers().write(0, ((x & 0xFFF) << 20) | ((z & 0xFFF) << 8) | (y & 0xFF));
        packet.getIntegers().write(1, state);
        packet.getBlockPositionModifier().write(0, new BlockPosition(x, y, z));

        try {
            Faylisia.getInstance().getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
