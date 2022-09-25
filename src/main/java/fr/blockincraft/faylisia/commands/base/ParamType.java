package fr.blockincraft.faylisia.commands.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.enchantment.CustomEnchantments;
import fr.blockincraft.faylisia.items.json.EnchantmentDeserializer;
import fr.blockincraft.faylisia.items.specificitems.EnchantmentLacrymaItem;
import fr.blockincraft.faylisia.player.permission.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public enum ParamType {
    PLAYER(CustomPlayerDTO.class, (value, sender, sendError) -> {
        for (CustomPlayerDTO player : Faylisia.getInstance().getRegistry().getPlayers().values()) {
            if (player.getNameToUse().equalsIgnoreCase(value)) {
                return player;
            }
        }

        if (sendError) {
            Map<String, String> params = new HashMap<>();
            params.put("%target_name%", value);
            sender.sendMessage(Messages.UNKNOWN_PLAYER_MESSAGE.get(params));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        for (CustomPlayerDTO player : Faylisia.getInstance().getRegistry().getPlayers().values()) {
            if (player.getNameToUse().toLowerCase(Locale.ROOT).startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(player.getNameToUse());
            }
        }

        return completion;
    }),
    PLAYER_NON_NICK(CustomPlayerDTO.class, (value, sender, sendError) -> {
        for (CustomPlayerDTO player : Faylisia.getInstance().getRegistry().getPlayers().values()) {
            if (player.getLastName().equalsIgnoreCase(value)) {
                return player;
            }
        }

        if (sendError) {
            Map<String, String> params = new HashMap<>();
            params.put("%target_name%", value);
            sender.sendMessage(Messages.UNKNOWN_PLAYER_MESSAGE.get(params));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        for (CustomPlayerDTO player : Faylisia.getInstance().getRegistry().getPlayers().values()) {
            if (player.getLastName().toLowerCase(Locale.ROOT).startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(player.getLastName());
            }
        }

        return completion;
    }),
    ONLINE_PLAYER(Player.class, (value, sender, sendError) -> {
        if (sender instanceof Player player && value.equalsIgnoreCase("@s")) {
            return player;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
            if (customPlayer.getNameToUse().equalsIgnoreCase(value)) {
                return player;
            }
        }

        if (sendError) {
            Map<String, String> params = new HashMap<>();
            params.put("%target_name%", value);
            sender.sendMessage(Messages.UNKNOWN_ONLINE_PLAYER_MESSAGE.get(params));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        if (sender instanceof Player) {
            completion.add("@s");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
            if (customPlayer.getNameToUse().toLowerCase(Locale.ROOT).startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(customPlayer.getNameToUse());
            }
        }

        return completion;
    }),
    ONLINE_PLAYER_SUPPORT_ALL(Player[].class, (value, sender, sendError) -> {
        if (value.equalsIgnoreCase("@a")) {
            return Bukkit.getOnlinePlayers().toArray(new Player[0]);
        } else if (sender instanceof Player player && value.equalsIgnoreCase("@s")) {
            return new Player[]{player};
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
            if (customPlayer.getNameToUse().equalsIgnoreCase(value)) {
                return new Player[]{player};
            }
        }

        if (sendError) {
            Map<String, String> params = new HashMap<>();
            params.put("%target_name%", value);
            sender.sendMessage(Messages.UNKNOWN_ONLINE_PLAYER_MESSAGE.get(params));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>(List.of("@a"));

        if (sender instanceof Player) {
            completion.add("@s");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
            if (customPlayer.getNameToUse().toLowerCase(Locale.ROOT).startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(customPlayer.getNameToUse());
            }
        }

        return completion;
    }),
    CUSTOM_ITEM(CustomItem.class, (value, sender, sendError) -> {
        CustomItem item = Faylisia.getInstance().getRegistry().getItemsById().get(value.toLowerCase(Locale.ROOT));

        if (item == null || !item.isRegistered()) {
            if (sendError) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("%item_id%", value);
                sender.sendMessage(Messages.INVALID_ITEM_MESSAGE.get(parameters));
            }
            return null;
        }

        return item;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        for (CustomItem item : Faylisia.getInstance().getRegistry().getItems()) {
            if (item.isRegistered() && item.getId().startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(item.getId());
            }
        }

        return completion;
    }),
    CUSTOM_ITEM_STACK(CustomItemStack.class, (value, sender, sendError) -> {
        String[] elements = value.split("\\|\\|");

        if (elements.length != 1 && elements.length != 2) {
            if (sendError) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("%item_id%", value);
                sender.sendMessage(Messages.INVALID_ITEM_MESSAGE.get(parameters));
            }
            return null;
        }

        CustomItem item = Faylisia.getInstance().getRegistry().getItemsById().get(elements[0].toLowerCase(Locale.ROOT));

        if (item == null || !item.isRegistered()) {
            if (sendError) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("%item_id%", elements[0]);
                sender.sendMessage(Messages.INVALID_ITEM_MESSAGE.get(parameters));
            }
            return null;
        }

        CustomItemStack customItemStack = new CustomItemStack(item, 1);

        if (elements.length == 2 && (item.isEnchantable() || item instanceof EnchantmentLacrymaItem)) {
            String json = elements[1];

            ObjectMapper mapper = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addDeserializer(Map.class, new EnchantmentDeserializer());
            mapper.registerModule(simpleModule);

            try {
                Map<CustomEnchantments, Integer> enchants = mapper.readValue(json, Map.class);

                if (enchants != null) {
                    enchants.forEach(item instanceof EnchantmentLacrymaItem ? customItemStack::addStoredEnchantment : customItemStack::addEnchantment);
                }
            } catch (Exception ignored) {

            }
        }

        return customItemStack;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        for (CustomItem item : Faylisia.getInstance().getRegistry().getItems()) {
            if (item.isRegistered() && item.getId().startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(item.getId());
            }
        }

        return completion;
    }),
    AMOUNT(Integer.class, (value, sender, sendError) -> {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) { }

        if (sendError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%number%", value);
            sender.sendMessage(Messages.INVALID_NUMBER_MESSAGE.get(parameters));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<amount>");

        return completion;
    }),
    TOKEN(String.class, (value, sender, sendError) -> value, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<token>");

        return completion;
    }),
    RANK(Ranks.class, (value, sender, sendError) -> {
        for (Ranks rank : Ranks.values()) {
            if (rank.name.equalsIgnoreCase(value)) {
                return rank;
            }
        }

        if (sendError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%rank%", value);
            sender.sendMessage(Messages.UNKNOWN_RANK_MESSAGE.get(parameters));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        for (Ranks value : Ranks.values()) {
            completion.add(value.name);
        }

        return completion;
    }),
    X(Integer.class, (value, sender, sendError) -> {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) { }

        if (sendError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%number%", value);
            sender.sendMessage(Messages.INVALID_NUMBER_MESSAGE.get(parameters));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<x>");
        if (sender instanceof Player player) {
            completion.add(String.valueOf(player.getLocation().getBlockX()));
        }

        return completion;
    }),
    Y(Integer.class, (value, sender, sendError) -> {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) { }

        if (sendError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%number%", value);
            sender.sendMessage(Messages.INVALID_NUMBER_MESSAGE.get(parameters));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<y>");
        if (sender instanceof Player player) {
            completion.add(String.valueOf(player.getLocation().getBlockY()));
        }

        return completion;
    }),
    Z(Integer.class, (value, sender, sendError) -> {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) { }

        if (sendError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%number%", value);
            sender.sendMessage(Messages.INVALID_NUMBER_MESSAGE.get(parameters));
        }

        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<z>");
        if (sender instanceof Player player) {
            completion.add(String.valueOf(player.getLocation().getBlockZ()));
        }

        return completion;
    }),
    BLOCK_MATERIAL(Material.class, (value, sender, sendError) -> {
        for (Material material : Material.values()) {
            if (material.name().equalsIgnoreCase(value)) {
                if (material.isBlock()) {
                    return material;
                } else {
                    if (sendError) {
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("%material_name%", material.name().toLowerCase(Locale.ROOT));
                        sender.sendMessage(Messages.NON_BLOCK_MATERIAL_MESSAGE.get(parameters));
                    }
                    return null;
                }
            }
        }

        if (sendError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%material_name%", value);
            sender.sendMessage(Messages.UNKNOWN_MATERIAL_MESSAGE.get(parameters));
        }
        return null;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        for (Material value : Material.values()) {
            if (value.isBlock() && !value.isAir() && value.name().toLowerCase(Locale.ROOT).startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                completion.add(value.name().toLowerCase(Locale.ROOT));
            }
        }

        return completion;
    }),
    TEXT(String.class, (value, sender, sendError) -> value, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<text>");

        return completion;
    }),
    BOOLEAN(Boolean.class, (value, sender, sendError) -> {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            if (sendError) {
                Map<String, String> params = new HashMap<>();
                params.put("%boolean%", value);
                sender.sendMessage(Messages.INVALID_BOOLEAN_MESSAGE.get(params));
            }
            return null;
        }
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        if ("true".startsWith(currentValue.toLowerCase(Locale.ROOT))) completion.add("true");
        if ("false".startsWith(currentValue.toLowerCase(Locale.ROOT))) completion.add("false");

        return completion;
    }),
    ENABLE_STATE(Boolean.class, (value, sender, sendError) -> {
        if (value.equalsIgnoreCase("enable")) {
            return true;
        } else if (value.equalsIgnoreCase("disable")) {
            return false;
        } else {
            if (sendError) {
                Map<String, String> params = new HashMap<>();
                params.put("%state%", value);
                sender.sendMessage(Messages.INVALID_STATE_MESSAGE.get(params));
            }
            return null;
        }
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        if ("enable".startsWith(currentValue.toLowerCase(Locale.ROOT))) completion.add("enable");
        if ("disable".startsWith(currentValue.toLowerCase(Locale.ROOT))) completion.add("disable");

        return completion;
    }),
    NAME(String.class, (value, sender, sendError) -> {
        if (!Pattern.compile("[a-zA-Z1-9_-]+").matcher(value).matches()) {
            if (sendError) sender.sendMessage(Messages.INVALID_NAME_CONTENT.get());
            return null;
        }

        if (value.length() < 3 || value.length() > 16) {
            if (sendError) sender.sendMessage(Messages.INVALID_NAME_LENGTH.get());
            return null;
        }

        return value;
    }, (currentValue, sender) -> {
        List<String> completion = new ArrayList<>();

        completion.add("<name>");

        return completion;
    });

    public final Class<?> type;
    public final ParamParser<?> parser;
    public final ParamCompleter completer;

    ParamType(@NotNull Class<?> type, @NotNull ParamParser<?> parser, @NotNull ParamCompleter completer) {
        this.type = type;
        this.parser = parser;
        this.completer = completer;
    }

    @FunctionalInterface
    public interface ParamParser<T> {
        @Nullable
        T parse(String value, CommandSender sender, boolean sendError);
    }

    @FunctionalInterface
    public interface ParamCompleter {
        @NotNull
        List<String> complete(String currentValue, CommandSender sender);
    }
}
