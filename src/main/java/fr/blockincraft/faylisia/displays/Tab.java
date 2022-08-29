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

import java.util.*;
import java.util.stream.Collectors;

public class Tab {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final ProtocolManager protocolManager = Faylisia.getInstance().getProtocolManager();

    private static final Map<Integer, UUID> uuidsPerSlot;
    private static final Map<Integer, String> namePerSlot;

    private static final char[] letters = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

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

    private static PlayerInfoData getPlayerHeader(int onlinePlayers, int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.LIME.value, Skins.LIME.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', "           &aJoueurs " + onlinePlayers)
        ));

        return playerInfoData;
    }

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

    private static PlayerInfoData getPlayerRender(Player player, int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        Classes classes = registry.getOrRegisterPlayer(player.getUniqueId()).getClasses();
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", classes.skin.value, classes.skin.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, player.getPing(), EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText(
                ColorsUtils.translateAll(customPlayer.getRank().playerName.replace("%player_name%", customPlayer.getName().replace(" ", "\\_")))
        ));

        return playerInfoData;
    }

    public static void initPlayersTabPartFor(Player player) {
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

    public static void refreshPlayersInTabFor(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

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

    public static void refreshPlayerSkinOfFor(Player of, Player player) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(of.getUniqueId(), of.getName());
        Classes classes = registry.getOrRegisterPlayer(of.getUniqueId()).getClasses();

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, of.getPing(), EnumWrappers.NativeGameMode.CREATIVE,
                WrappedChatComponent.fromText(of.getName())
        );

        players.add(playerInfoData);

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        wrappedGameProfile.getProperties().removeAll("textures");
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", classes.skin.value, classes.skin.signature));

        playerInfoData = new PlayerInfoData(wrappedGameProfile, of.getPing(), EnumWrappers.NativeGameMode.CREATIVE,
                WrappedChatComponent.fromText(of.getName())
        );

        players.clear();
        players.add(playerInfoData);

        packet.getPlayerInfoDataLists().write(0, players);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        packet.getIntLists().write(0, Arrays.asList(of.getEntityId()));

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);

        packet.getIntegers().write(0, of.getEntityId());
        packet.getUUIDs().write(0, of.getUniqueId());
        packet.getDoubles().write(0, of.getLocation().getX());
        packet.getDoubles().write(1, of.getLocation().getY());
        packet.getDoubles().write(2, of.getLocation().getZ());

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private static PlayerInfoData getStatsHeader(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GOLD.value, Skins.GOLD.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', "             &6Stats")
        ));

        return playerInfoData;
    }

    private static PlayerInfoData getRankElement(int slot, Ranks rank) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText(
                ColorsUtils.translateAll(" &eGrade: " + rank.displayName)
        ));

        return playerInfoData;
    }

    private static PlayerInfoData getClassesElement(int slot, Classes classes) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', " &eClass: &" + classes.color + classes.name)
        ));

        return playerInfoData;
    }

    private static PlayerInfoData getDamageElement(int slot, long value) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', "&f\uE000&c Puissance: " + value)
        ));

        return playerInfoData;
    }

    private static PlayerInfoData getStatElement(int slot, Stats stat, long value) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', "&f" + stat.icon + " &" + stat.color + stat.name + ": " + value)
        ));

        return playerInfoData;
    }

    public static void initStatsPartFor(Player player) {
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

    public static void refreshStatsPartFor(Player player) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> players = new ArrayList<>();

        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

        players.add(getRankElement(42, customPlayer.getRank()));
        players.add(getClassesElement(43, customPlayer.getClasses()));

        players.add(getDamageElement(46, customPlayer.getRawDamage()));

        List<Stats> stats = Arrays.stream(Stats.values()).sorted((o1, o2) -> o1.index - o2.index).toList();

        for (int i = 0; i < stats.size(); i++) {
            int slot = 47 + i;
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

    private static PlayerInfoData getGuildHeader(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.AQUA.value, Skins.AQUA.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', "             &bGuild")
        ));

        return playerInfoData;
    }

    private static PlayerInfoData getComingSoon(int slot) {
        String name = namePerSlot.get(slot);
        UUID uuid = uuidsPerSlot.get(slot);

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", Skins.GRAY.value, Skins.GRAY.signature));

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(
                ChatColor.translateAlternateColorCodes('&', " &8Prochainement")
        ));

        return playerInfoData;
    }

    public static void initGuildPartFor(Player player) {
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

    public static void refreshGuildPartFor(Player player) {
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





    //Header footer part
    private static final Map<UUID, AnimatedText[]> header = new HashMap<>();
    private static final Map<UUID, AnimatedText[]> footer = new HashMap<>();

    public static void updateFooterAndHeader(Player player) {
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
                    new AnimatedText("&8Vos ping: &7%ping%"),
                    new LinearAnimation('f', 5, LinearAnimation.StartPosition.SIDE)
                            .addElement('d', "Site: faylis.xyz")
                            .build(),
                    new AnimatedText("")
            };

            Tab.footer.put(player.getUniqueId(), footer);
        }

        StringBuilder headerSb = new StringBuilder();
        for (int i = 0; i < header.length; i++) {
            if (i != 0) headerSb.append("\n");
            headerSb.append(ChatColor.translateAlternateColorCodes('&', header[i].get()));
        }

        StringBuilder footerSb = new StringBuilder();
        for (int i = 0; i < footer.length; i++) {
            if (i != 0) footerSb.append("\n");
            footerSb.append(ChatColor.translateAlternateColorCodes('&', footer[i].get()
                    .replace("%ping%", String.valueOf(player.getPing()))));
        }

        player.setPlayerListHeaderFooter(headerSb.toString(), footerSb.toString());
    }
}
