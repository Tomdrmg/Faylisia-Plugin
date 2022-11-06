package fr.blockincraft.faylisia.displays;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.displays.animation.LinearAnimation;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.permission.Ranks;
import fr.blockincraft.faylisia.player.Stats;
import fr.blockincraft.faylisia.player.Classes;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.TextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Tab {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final ProtocolManager protocolManager = Faylisia.getInstance().getProtocolManager();

    private static final Map<Integer, UUID> uuidsPerSlot;
    private static final Map<Integer, String> namePerSlot;

    private static final char[] letters = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    // Create a list of player uuid and name to place them in tab with the right order
    // We name them "!aa", "!ab"... because Minecraft sort them by name, we can always change
    // their display name to a good render
    static {
        uuidsPerSlot = new HashMap<>();
        namePerSlot = new HashMap<>();

        for (int i = 0; i < 80; i++) {
            uuidsPerSlot.put(i, UUID.randomUUID());
            char first = letters[i / 26];
            char second = letters[i % 26];

            namePerSlot.put(i, "!" + first + second);
        }
    }

    /**
     * Get a fake player info data to display amount of online players
     * @param onlinePlayers online players amount
     * @param slot slot to display it (Tab contains 4 bars with 20 slot each, from 0 to 79)
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getPlayerHeader(int onlinePlayers, int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.LIME.value, Skins.LIME.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll("           &aJoueurs " + onlinePlayers)
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display an empty slot
     * @param slot slot to display it (Tab contains 4 bars with 20 slot each, from 0 to 79)
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getPlayerEmpty(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                "                                 "
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display a player
     * @param player player to display
     * @param slot slot to display it (Tab contains 4 bars with 20 slot each, from 0 to 79)
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getPlayerRender(@NotNull Player player, int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        Classes classes = registry.getOrRegisterPlayer(player.getUniqueId()).getClasses();
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", classes.skin.value, classes.skin.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, player.getPing(), EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText(
                ColorsUtils.translateAll(customPlayer.getRank().playerName.replace("%player_name%", customPlayer.getNameToUse().replace(" ", "\\_")))
        ));

        return playerInfoData;
    }

    /**
     * Initialize player tab (add {@link Tab#getPlayerHeader(int, int)}, players and empty slots)
     * @param player player to initialize her tab
     */
    public static void initPlayersTabPartFor(@NotNull Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers()).stream().sorted((Comparator<Player>) (o1, o2) -> {
            CustomPlayerDTO p1 = registry.getOrRegisterPlayer(o1.getUniqueId());
            CustomPlayerDTO p2 = registry.getOrRegisterPlayer(o2.getUniqueId());
            return p1.getRank().index - p2.getRank().index;
        }).collect(Collectors.toList());

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(getPlayerHeader(onlinePlayers.size(), 0));
        players.add(getPlayerHeader(onlinePlayers.size(), 20));

        for (int i = 0; i < 38; i++) {
            if (i < onlinePlayers.size()) {
                Player pl = onlinePlayers.get(i);
                players.add(getPlayerRender(pl, i + 1 + i / 19));
            } else {
                players.add(getPlayerEmpty(i + 1 + i / 19));
            }
        }

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update player name and vanish
     * @param player player to update her tab
     */
    public static void refreshRealsPlayersInTabFor(@NotNull Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        for (Player pl : onlinePlayers) {
            CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(pl.getUniqueId());

            WrappedGameProfile gameProfile = new WrappedGameProfile(pl.getUniqueId(), pl.getName());
            PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, pl.getPing(), EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText(
                    ColorsUtils.translateAll(customPlayer.getRank().playerName.replace("%player_name%", customPlayer.getNameToUse()))
            ));

            players.add(playerInfoData);
        }

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);
            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update all players in the tab of a player (update {@link Tab#getPlayerHeader(int, int)} and players)
     * @param player player to update her tab
     */
    public static void refreshPlayersInTabFor(@NotNull Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers()).stream().sorted((Comparator<Player>) (o1, o2) -> {
            CustomPlayerDTO p1 = registry.getOrRegisterPlayer(o1.getUniqueId());
            CustomPlayerDTO p2 = registry.getOrRegisterPlayer(o2.getUniqueId());
            return p1.getRank().index - p2.getRank().index;
        }).collect(Collectors.toList());

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(getPlayerHeader(onlinePlayers.size(), 0));
        players.add(getPlayerHeader(onlinePlayers.size(), 20));

        for (int i = 0; i < 38; i++) {
            if (i < onlinePlayers.size()) {
                Player pl = onlinePlayers.get(i);
                players.add(getPlayerRender(pl, i + 1 + i / 19));
            } else {
                players.add(getPlayerEmpty(i + 1 + i / 19));
            }
        }

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);

            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get fake player info data to display the stats header
     * @param slot slot to display it
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getStatsHeader(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GOLD.value, Skins.GOLD.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll("             &6Stats")
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display player rank
     * @param slot slot to display it
     * @param rank rank to display
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getRankElement(int slot, @NotNull Ranks rank) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText(
                ColorsUtils.translateAll(" &eGrade: " + rank.displayName)
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display player class
     * @param slot slot to display it
     * @param classes class to display
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getClassesElement(int slot, @NotNull Classes classes) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll(" &eClass: &" + classes.color + classes.name)
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display player damage
     * @param slot slot to display it
     * @param value damage to display
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getDamageElement(int slot, long value) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll("&f\uE000&c Puissance: " + value)
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display a stat of a player
     * @param slot slot to display it
     * @param stat stat to show
     * @param value value of the stat
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getStatElement(int slot, @NotNull Stats stat, long value) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll("&f" + stat.icon + " " + stat.color + stat.name + ": " + value)
        ));

        return playerInfoData;
    }

    /**
     * Initialize stats tab part for a player
     * @param player player to init her tab
     */
    public static void initStatsPartFor(@NotNull Player player) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(getStatsHeader(40));

        for (int i = 0; i < 19; i++) {
            players.add(getPlayerEmpty(i + 1 + 40));
        }

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update stats tab part for a player
     * @param player player to update her tab
     */
    public static void refreshStatsPartFor(@NotNull Player player) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

        players.add(getRankElement(42, customPlayer.getRank()));
        players.add(getClassesElement(43, customPlayer.getClasses()));

        List<Stats> stats = Arrays.stream(Stats.values()).sorted((o1, o2) -> o1.index - o2.index).toList();

        for (int i = 0; i < stats.size(); i++) {
            int slot = 45 + i;
            Stats stat = stats.get(i);
            long value = Math.round(customPlayer.getStat(stat));

            players.add(getStatElement(slot, stat, value));
        }

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a fake player info data to display guild header
     * @param slot slot to display it
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getGuildHeader(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.AQUA.value, Skins.AQUA.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll("             &bGuild")
        ));

        return playerInfoData;
    }

    /**
     * Get a fake player info data to display a "coming soon" player
     * @param slot slot to display it
     * @return fake player info data
     */
    @NotNull
    private static PlayerInfoData getComingSoon(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ColorsUtils.translateAll(" &8Prochainement")
        ));

        return playerInfoData;
    }

    /**
     * Initialize guild tab part for a player
     * @param player player to init her tab
     */
    public static void initGuildPartFor(@NotNull Player player) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(getGuildHeader(60));

        for (int i = 0; i < 19; i++) {
            players.add(getPlayerEmpty(i + 1 + 60));
        }

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update guild information in tab of a player
     * @param player player to update her tab
     */
    public static void refreshGuildPartFor(@NotNull Player player) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(getComingSoon(62));

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    //Tab header and footer part
    private static final Map<UUID, AnimatedText[]> header = new HashMap<>();
    private static final Map<UUID, AnimatedText[]> footer = new HashMap<>();

    /**
     * Update tab header and footer for a player
     * @param player player to update her tab
     */
    public static void updateFooterAndHeader(@NotNull Player player) {
        AnimatedText[] header = Tab.header.get(player.getUniqueId());
        AnimatedText[] footer = Tab.footer.get(player.getUniqueId());

        if (header == null) {
            header = new AnimatedText[]{
                    new AnimatedText(""),
                    new LinearAnimation('f', 5, LinearAnimation.StartPosition.CENTER)
                            .addElement('d', "Bienvenue sur ")
                            .addElement('d', "Faylisia", true)
                            .build(),
                    new AnimatedText("")
            };

            Tab.header.put(player.getUniqueId(), header);
        }
        if (footer == null) {
            footer = new AnimatedText[]{
                    new AnimatedText(""),
                    // new AnimatedText("&8Vos ping: &7%ping%"),
                    new LinearAnimation('f', 5, LinearAnimation.StartPosition.SIDE)
                            .addElement('d', "Site: faylisia.fr")
                            .build(),
                    new AnimatedText("")
            };

            Tab.footer.put(player.getUniqueId(), footer);
        }

        StringBuilder headerSb = new StringBuilder();
        for (int i = 0; i < header.length; i++) {
            if (i != 0) headerSb.append("\n");
            headerSb.append(ColorsUtils.translateAll(header[i].get()));
        }

        StringBuilder footerSb = new StringBuilder();
        for (int i = 0; i < footer.length; i++) {
            if (i != 0) footerSb.append("\n");
            footerSb.append(ColorsUtils.translateAll(footer[i].get()
                    .replace("%ping%", String.valueOf(player.getPing()))));
        }

        player.setPlayerListHeaderFooter(headerSb.toString(), footerSb.toString());
    }
}
