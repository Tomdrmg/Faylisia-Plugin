package fr.blockincraft.faylisia;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.blockincraft.faylisia.blocks.BlockType;
import fr.blockincraft.faylisia.blocks.CustomBlock;
import fr.blockincraft.faylisia.core.dto.DiscordTicketDTO;
import fr.blockincraft.faylisia.core.service.CustomPlayerService;
import fr.blockincraft.faylisia.core.service.DiscordTicketService;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.CustomEntityType;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.armor.ArmorSet;
import fr.blockincraft.faylisia.items.management.Categories;
import fr.blockincraft.faylisia.magic.SpellType;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.map.shapes.Shape;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the Registry of the plugin, when it was enabled, all data is stored here <br/>
 * {@link Faylisia} class initialize it with the init() method and store the instance <br/>
 * Differences between DTO and object are explained in {@link CustomPlayerDTO} and {@link DiscordTicketDTO}
 */
public class Registry {
    // Initialize plugin unique elements
    private static final Logger logger = Faylisia.getInstance().getLogger();
    private static final CustomPlayerService customPlayerService = new CustomPlayerService();
    private static final DiscordTicketService ticketService = new DiscordTicketService();
    public static File blocksFile;

    // Initialize registry in-game data
    // Custom players are stored in database
    // Others are created when plugin start
    private final Map<UUID, CustomPlayerDTO> players = new HashMap<>();
    private final Map<String, CustomItem> itemsById = new HashMap<>();
    private final List<CustomItem> items = new ArrayList<>();
    private final Map<String, ArmorSet> armorSetsById = new HashMap<>();
    private final List<ArmorSet> armorSets = new ArrayList<>();
    private final Map<String, CustomEntityType> entityTypesById = new HashMap<>();
    private final List<CustomEntityType> entityTypes = new ArrayList<>();
    private final Map<Entity, CustomEntity> entitiesByEntity = new HashMap<>();
    private final List<CustomEntity> entities = new ArrayList<>();
    private final Map<String, BlockType> blockTypesById = new HashMap<>();
    private final List<BlockType> blockTypes = new ArrayList<>();
    private final Map<String, SpellType> spellTypesById = new HashMap<>();
    private final List<SpellType> spellTypes = new ArrayList<>();
    private final List<CustomBlock> blocks = new ArrayList<>();
    private final Map<String, Region> regionsById = new HashMap<>();
    private final List<Region> regions = new ArrayList<>();
    private Region defaultRegion = null;

    // Initialize registry discord data
    // Tickets are stored in database
    // Tokens aren't stored because they expire after two minutes,
    // and we consider that they are expired after a restart
    private final Map<Long, DiscordTicketDTO> tickets = new HashMap<>();
    private final Map<String, Member> tokensToMember = new HashMap<>();

    /**
     * This method is called to initialize data, it loads data from the mysql database
     */
    public void init() {
        // Initialize custom players
        for (CustomPlayerDTO customPlayer : customPlayerService.getAllCustomPlayer()) {
            logger.log(Level.INFO, "Load player data of " + customPlayer.getPlayer());
            players.put(customPlayer.getPlayer(), customPlayer);
        }

        // Initialize discord tickets
        for (DiscordTicketDTO ticket : ticketService.getAllDiscordTickets()) {
            logger.log(Level.INFO, "Load ticket in channel " + ticket.getChannelId() + " of user " + ticket.getUserId());
            tickets.put(ticket.getChannelId(), ticket);
        }
    }

    /**
     * This method create a token to link a {@link Member} to a {@link CustomPlayerDTO} <br/>
     * Token can contain digits, lowercase/uppercase letters and some special characters, its length is between 15 and 20 <br/>
     * The token is store and can be validated by the validateToken() method but never get <br/>
     * The token expire two minutes after her creation
     * @param member {@link Member} associated to the token
     * @return token
     */
    @NotNull
    public String createToken(@NotNull Member member) {
        // Create random instance, content array and token length
        Random r = new SecureRandom();
        char[] content = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ()[]{}:/-+=#@".toCharArray();

        int length = r.nextInt(6) + 15;

        StringBuilder sb;

        // Generate a new token and restart if token already exist
        // This may not cause problems even if we have a lot of members
        do {
            sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(content[r.nextInt(content.length)]);
            }
        } while (tokensToMember.containsKey(sb.toString()));

        // Keep the token
        String token = sb.toString();

        // Save it in this registry
        tokensToMember.put(token, member);
        // Schedule a new task to remove it two minutes after
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                tokensToMember.remove(token);
            }
        }, 120000);

        return token;
    }

    /**
     * Return if a {@link Member} already have a token
     * @param member {@link Member} to check
     * @return if he has a token
     */
    public boolean hasToken(@NotNull Member member) {
        return tokensToMember.containsValue(member);
    }

    /**
     * This method return if the token is valid, then if it was valid it will be considered like used and will be deleted
     * @param token token to check
     * @return associated {@link Member} / null if invalid token
     */
    @Nullable
    public Member validateToken(@NotNull String token) {
        // Check if token is valid
        if (!tokensToMember.containsKey(token)) return null;

        // Then keep account and remove it of this registry
        Member account = tokensToMember.get(token);
        tokensToMember.remove(token);

        return account;
    }

    /**
     * @return all {@link DiscordTicketDTO} with their associated {@link Channel} id
     */
    @NotNull
    public Map<Long, DiscordTicketDTO> getTickets() {
        return new HashMap<>(tickets);
    }

    /**
     * Return {@link DiscordTicketDTO} stored in {@link Channel}
     * @param channelId id of {@link Channel} to check
     * @return {@link DiscordTicketDTO} stored in the {@link Channel} or null
     */
    @Nullable
    public DiscordTicketDTO getTicketInChannel(long channelId) {
        return tickets.get(channelId);
    }

    /**
     * This method register a {@link DiscordTicketDTO} in this registry and in database
     * @param dto {@link DiscordTicketDTO} to register
     */
    public void createTicket(@NotNull DiscordTicketDTO dto) {
        tickets.put(dto.getChannelId(), dto);
        ticketService.persistDiscordTicket(dto);
    }

    /**
     * This method update a {@link DiscordTicketDTO} in database
     * @param dto {@link DiscordTicketDTO} to update
     */
    public void updateTicket(@NotNull DiscordTicketDTO dto) {
        ticketService.mergeDiscordTicket(dto);
    }

    /**
     * This method delete a {@link DiscordTicketDTO} in this registry and in database
     * @param dto {@link DiscordTicketDTO} to remove
     */
    public void removeTicket(@NotNull DiscordTicketDTO dto) {
        tickets.remove(dto.getChannelId());
        ticketService.removeDiscordTicket(dto);
    }

    /**
     * This method return all {@link DiscordTicketDTO} of a discord {@link User}
     * @param userId discord {@link User} id
     * @return all {@link DiscordTicketDTO} of user
     */
    @NotNull
    public List<DiscordTicketDTO> getTicketsOf(long userId) {
        List<DiscordTicketDTO> tickets = new ArrayList<>();

        this.tickets.forEach((channel, ticket) -> {
            if (ticket.getUserId() == userId) {
                tickets.add(ticket);
            }
        });

        return tickets;
    }

    /**
     * @return all {@link CustomPlayerDTO} with their associated {@link UUID}
     */
    @NotNull
    public Map<UUID, CustomPlayerDTO> getPlayers() {
        return new HashMap<>(players);
    }

    /**
     * This method return {@link CustomPlayerDTO} of a bukkit {@link Player} {@link UUID}
     * @param player bukkit {@link Player} {@link UUID}
     * @return {@link CustomPlayerDTO} associated / null if he doesn't exist
     */
    @Nullable
    public CustomPlayerDTO getPlayer(@NotNull UUID player) {
        return players.get(player);
    }

    /**
     * This method create and register a {@link CustomPlayerDTO} from the bukkit {@link Player} {@link UUID} <br/>
     * It will me register in this registry and in database
     * @param player bukkit {@link Player} {@link UUID}
     * @return {@link CustomPlayerDTO} instance created
     */
    @NotNull
    public CustomPlayerDTO registerPlayer(@NotNull UUID player) {
        CustomPlayerDTO dto = new CustomPlayerDTO(player);

        customPlayerService.persistCustomPlayer(dto);
        players.put(player, dto);
        return dto;
    }

    /**
     * This method return {@link CustomPlayerDTO} of a bukkit {@link Player} {@link UUID} and if it doesn't exist, it was register and get
     * @param player bukkit {@link Player} {@link UUID}
     * @return {@link CustomPlayerDTO} instance associated
     */
    @NotNull
    public CustomPlayerDTO getOrRegisterPlayer(@NotNull UUID player) {
        CustomPlayerDTO pl = getPlayer(player);
        return pl == null ? registerPlayer(player) : pl;
    }

    /**
     * This method apply change of a {@link CustomPlayerDTO} in database
     * @param dto edited {@link CustomPlayerDTO}
     */
    public void applyModification(@NotNull CustomPlayerDTO dto) {
        customPlayerService.mergeCustomPlayer(dto);
    }

    /**
     * @return all {@link CustomItem}
     */
    @NotNull
    public List<CustomItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * This method check if an item id is used or no
     * @param id id to check
     * @return if it was used
     */
    public boolean itemIdUsed(@NotNull String id) {
        return itemsById.containsKey(id);
    }

    /**
     * This method register a {@link CustomItem} in this registry and in database
     * @param item {@link CustomItem} to register
     */
    public void registerItem(@NotNull CustomItem item) {
        itemsById.put(item.getId(), item);
        items.add(item);
        Categories category = item.getCategory();
        if (category != null) {
            category.items.add(item);
        }
    }

    /**
     * This method read bukkit {@link ItemStack} data to found custom item associated
     * @param itemStack bukkit {@link ItemStack} to check
     * @return associated {@link CustomItem} / null if it doesn't have
     */
    @Nullable
    public CustomItem getCustomItemByItemStack(@Nullable ItemStack itemStack) {
        // Check if item isn't null and isn't AIR
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;

        // Check if item has meta
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        // Check if item has custom item id data
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(CustomItem.idKey, PersistentDataType.STRING)) return null;

        // Then read custom item id and return associated custom item
        String id = data.get(CustomItem.idKey, PersistentDataType.STRING);
        return itemsById.get(id);
    }

    /**
     * @return all {@link CustomItem} and their associated id
     */
    @NotNull
    public Map<String, CustomItem> getItemsById() {
        return new HashMap<>(itemsById);
    }

    /**
     * This method actualize name and lore of {@link CustomItem} stored in a {@link PlayerInventory} <br/>
     * Used to prevent bad bukkit {@link ItemStack} in case of custom item change
     * @param inventory {@link PlayerInventory} to scan
     */
    public void refreshItems(@NotNull PlayerInventory inventory) {
        // For each item stack in player inventory
        for (ItemStack itemStack : inventory.getContents()) {
            // Check if item isn't null and isn't AIR
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;

            // Get as custom item stack to retrieve data like enchantments
            CustomItemStack customItemStack = CustomItemStack.fromItemStack(itemStack);
            if (customItemStack == null) continue;

            // Update item stack
            customItemStack.updateItemStack(itemStack);
        }
    }

    /**
     * @return all {@link ArmorSet}
     */
    @NotNull
    public List<ArmorSet> getArmorSets() {
        return new ArrayList<>(armorSets);
    }

    /**
     * This method check if an {@link ArmorSet} id was already used
     * @param id id to check
     * @return if id was already used
     */
    public boolean armorSetIdUsed(@NotNull String id) {
        return armorSetsById.containsKey(id);
    }

    /**
     * This method register an {@link ArmorSet} in this registry and database
     * @param armorSet {@link ArmorSet} to register
     */
    public void registerArmorSet(@NotNull ArmorSet armorSet) {
        armorSetsById.put(armorSet.getId(), armorSet);
        armorSets.add(armorSet);
    }

    /**
     * @return all {@link CustomEntityType}
     */
    @NotNull
    public List<CustomEntityType> getEntityTypes() {
        return new ArrayList<>(entityTypes);
    }

    /**
     * This method check if a {@link CustomEntityType} id was already used
     * @param id id to check
     * @return if id was already used
     */
    public boolean entityTypeIdUsed(@NotNull String id) {
        return entityTypesById.containsKey(id);
    }

    /**
     * This method register a {@link CustomEntityType} in this registry and in database
     * @param entityType {@link CustomEntityType} to register
     */
    public void registerEntityType(@NotNull CustomEntityType entityType) {
        entityTypes.add(entityType);
        entityTypesById.put(entityType.getId(), entityType);
    }

    /**
     * This method return {@link CustomEntity} associated to the bukkit {@link Entity}
     * @param entity bukkit {@link Entity}
     * @return associated {@link CustomEntity} / null if he doesn't have
     */
    @Nullable
    public CustomEntity getCustomEntityByEntity(@NotNull Entity entity) {
        return entitiesByEntity.get(entity);
    }

    /**
     * @return all {@link CustomEntity}
     */
    @NotNull
    public List<CustomEntity> getEntities() {
        return new ArrayList<>(entities);
    }

    /**
     * This method add a {@link CustomEntity} in this registry
     * @param entity {@link CustomEntity} to add
     */
    public void addEntity(@NotNull CustomEntity entity) {
        entities.add(entity);
        entitiesByEntity.put(entity.getEntity(), entity);
    }

    /**
     * This method remove a {@link CustomEntity} in this registry
     * @param entity {@link CustomEntity} to remove
     */
    public void removeEntity(@NotNull CustomEntity entity) {
        entities.remove(entity);
        entitiesByEntity.remove(entity.getEntity());
    }

    /**
     * @return all {@link ArmorSet} with their associated id
     */
    @NotNull
    public Map<String, ArmorSet> getArmorSetsById() {
        return new HashMap<>(armorSetsById);
    }

    /**
     * This method check if a {@link Region} id was already used
     * @param id id to check
     * @return if id was already used
     */
    public boolean regionIdUsed(@NotNull String id) {
        return regionsById.containsKey(id);
    }

    /**
     * @return all {@link Region} with their associated id
     */
    @NotNull
    public Map<String, Region> getRegionsById() {
        return regionsById;
    }

    /**
     * @return all {@link Region}
     */
    @NotNull
    public List<Region> getRegions() {
        return regions;
    }

    /**
     * This method register a {@link Region} in this registry and in database
     * @param region {@link Region} to register
     */
    public void registerRegion(@NotNull Region region) {
        regionsById.put(region.getId(), region);
        regions.add(region);
    }

    /**
     * This method get all {@link Region} at a position
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param world world of the position
     * @return all {@link Region} of the position
     */
    public Region[] getRegionsAt(int x, int y, int z, @NotNull World world) {
        Set<Region> regions = new HashSet<>();
        regions.add(defaultRegion);

        for (Region region : this.regions) {
            if (region.isIn(x, y, z, world)) {
                regions.add(region);
            }
        }

        return regions.toArray(new Region[0]);
    }

    /**
     * This method get all {@link Region} at a position
     * @param location position
     * @return all {@link Region} of the position / null if world is null
     */
    @Nullable
    public Region[] getRegionsAt(@NotNull Location location) {
        if (location.getWorld() == null) return null;
        return getRegionsAt(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }

    /**
     * This method get {@link Region} at a position
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param world world of the position
     * @return last sub {@link Region} of the position
     */
    @NotNull
    public Region getRegionAt(int x, int y, int z, @NotNull World world) {
        // Get default region at start because default region cover all server
        Region region = defaultRegion;
        // Each time try to check if region has subregions
        outer: while (region.hasSubRegion()) {
            // For each subregion we check if they contain position
            for (Region subRegion : region.getSubRegion(false)) {
                if (subRegion.isIn(x, y, z, world)) {
                    region = subRegion;
                    continue outer;
                }
            }

            // If no subregions contain position then break
            break;
        }

        return region;
    }

    /**
     * This method get {@link Region} at a position
     * @param location position
     * @return last sub {@link Region} of the position / null if world is null
     */
    @Nullable
    public Region getRegionAt(@NotNull Location location) {
        if (location.getWorld() == null) return null;
        return getRegionAt(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }

    /**
     * This method check if a position is in a {@link Region}
     * @param region region to check
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param world world to check
     * @param strict true if it can't be a subregion of the region to check
     * @return if position is in region
     */
    public boolean isInRegion(@NotNull Region region, int x, int y, int z, @NotNull World world, boolean strict) {
        Region regionIn = getRegionAt(x, y, z, world);
        if (regionIn == region) return true;
        if (!strict) {
            List<Region> allRegion = region.getSubRegion(true);

            return allRegion.contains(regionIn);
        }

        return false;
    }
    /**
     * This method check if a position is in a {@link Region}
     * @param region region to check
     * @param location position
     * @param strict true if it can't be a subregion of the region to check
     * @return if position is in region
     */
    public boolean isInRegion(@NotNull Region region, @NotNull Location location, boolean strict) {
        if (location.getWorld() == null) return false;
        return this.isInRegion(region, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld(), strict);
    }

    /**
     * This method set the default {@link Region} of server
     * @param defaultRegion default {@link Region}
     */
    public void setDefaultRegion(@NotNull Region defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

    /**
     * @return all {@link BlockType}
     */
    @NotNull
    public List<BlockType> getBlockTypes() {
        return new ArrayList<>(blockTypes);
    }

    /**
     * This method check if an item id is used or no
     * @param id id to check
     * @return if it was used
     */
    public boolean blockTypeIdUsed(@NotNull String id) {
        return blockTypesById.containsKey(id);
    }

    /**
     * @param id id of block type
     * @return block type associated to the id
     */
    @Nullable
    public BlockType getBlockTypeById(@NotNull String id) {
        return blockTypesById.get(id);
    }

    /**
     * This method register a {@link BlockType} in this registry and in database
     * @param blockType {@link BlockType} to register
     */
    public void registerBlockType(@NotNull BlockType blockType) {
        blockTypesById.put(blockType.getId(), blockType);
        blockTypes.add(blockType);
    }

    /**
     * Register a {@link CustomBlock}
     * @param block {@link CustomBlock} to register
     */
    public void registerBlock(@NotNull CustomBlock block) {
        for (BlockType blockType : block.getStates()) {
            if (!blockType.isRegistered()) throw new RuntimeException("Block type " + blockType.getId() + " isn't registered!");
        }

        blocks.add(block);
        if (blocksFile != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String str = mapper.writeValueAsString(blocks);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blocksFile)));
                writer.write(str);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove a block and save blocks data
     * @param block block to delete
     */
    public void removeBlock(@NotNull CustomBlock block) {
        blocks.remove(block);
        if (blocksFile != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String str = mapper.writeValueAsString(blocks);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blocksFile)));
                writer.write(str);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get a block at a position
     * @param location location
     * @return custom block at the location or null
     */
    @Nullable
    public CustomBlock getBlockAt(@NotNull Location location) {
        if (location.getWorld() == null) return null;

        for (CustomBlock block : blocks) {
            if (block.getX() == location.getBlockX() && block.getY() == location.getBlockY() && block.getZ() == location.getBlockZ() && location.getWorld().getUID().equals(block.getWorld())) {
                return block;
            }
        }

        return null;
    }

    /**
     * @return all blocks
     */
    public List<CustomBlock> getBlocks() {
        return new ArrayList<>(blocks);
    }

    /**
     * Change blocks which store blocks
     * @param file new file
     */
    public void setBlocksFile(File file) {
        blocksFile = file;
    }

    public void reloadBlocks() throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(blocksFile)));
        String line;
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }

        ObjectMapper mapper = new ObjectMapper();
        CustomBlock[] blocks = mapper.readValue(resultStringBuilder.toString(), CustomBlock[].class);
        this.blocks.clear();
        for (CustomBlock block : blocks) {
            if (block != null) this.blocks.add(block);
        }
    }

    /**
     * @return all {@link SpellType}
     */
    @NotNull
    public List<SpellType> getSpellTypes() {
        return new ArrayList<>(spellTypes);
    }

    /**
     * This method check if an item id is used or no
     * @param id id to check
     * @return if it was used
     */
    public boolean spellTypeIdUsed(@NotNull String id) {
        return spellTypesById.containsKey(id);
    }

    /**
     * @param id id of block type
     * @return block type associated to the id
     */
    @Nullable
    public SpellType getSpellTypeById(@NotNull String id) {
        return spellTypesById.get(id);
    }

    /**
     * This method register a {@link SpellType} in this registry and in database
     * @param spellType {@link SpellType} to register
     */
    public void registerSpellType(@NotNull SpellType spellType) {
        spellTypesById.put(spellType.getId(), spellType);
        spellTypes.add(spellType);
    }
}
