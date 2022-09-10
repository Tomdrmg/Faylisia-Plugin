package fr.blockincraft.faylisia.listeners;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.permission.Ranks;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.exception.InvalidColorException;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class ChatListeners implements Listener {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    /**
     * Apply colors and {@link Ranks} prefix to chat messages
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        CustomPlayerDTO custom = registry.getOrRegisterPlayer(e.getPlayer().getUniqueId());
        Ranks rank = custom.getRank();

        String message = e.getMessage();

        e.setFormat(ChatColor.translateAlternateColorCodes('&', ColorsUtils.translateAll(rank.chatName.replace("%player_name%", custom.getName().replace(" ", "\\_"))) +" &8>> &f%2$s"));
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (!e.getPlayer().hasPermission("faylisia.chat_color")) {
            message = ChatColor.stripColor(message);
        }

        Matcher gradMatch = ColorsUtils.gradientPattern.matcher(message);
        while (gradMatch.find()) {
            String color = message.substring(gradMatch.start(), gradMatch.end());

            String colorArgs = color.replace("&grad(", "");
            colorArgs = colorArgs.substring(0, colorArgs.length() - 1);
            String[] args = colorArgs.split(" ");

            List<String> colors = Arrays.asList(args).subList(1, args.length);
            String[] colorsArray = colors.toArray(new String[0]);

            String word = args[0].replace("\\_", " ");

            String replacement = word;

            if (e.getPlayer().hasPermission("faylisia.chat_gradient_color")) {
                if (colorsArray.length < 2) {
                    e.getPlayer().sendMessage(Messages.AT_LEAST_TWO_COLOR_FOR_GRADIENT.get());
                } else {
                    try {
                        replacement = ColorsUtils.generateGradient(word, colorsArray);
                    } catch (InvalidColorException ex) {
                        Map<String, String> parameters = new HashMap<>();

                        parameters.put("%color%", ex.color);

                        e.getPlayer().sendMessage(Messages.INVALID_COLOR.get(parameters));
                    }
                }
            }

            message = message.replace(color, replacement);

            gradMatch = ColorsUtils.gradientPattern.matcher(message);
        }

        if (e.getPlayer().hasPermission("faylisia.chat_hex_color")) {
            message = ColorsUtils.translateHexColors(message);
        } else {
            message = ColorsUtils.stripHexColors(message);
        }

        e.setMessage(message);
    }
}
