package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.map.Spawn;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SpawnExecutor implements CommandExecutor {
    private static final String command = "spawn";
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("faylisia.spawn.teleport_others")) {
                sendNoPermissionMessage(sender);
                return true;
            }

            Player pl = sender instanceof Player ? (Player) sender : null;
            CustomPlayerDTO custom = pl == null ? null : registry.getOrRegisterPlayer(pl.getUniqueId());

            Map<String, String> parameters = new HashMap<>();
            parameters.put("%player_name%", pl == null ? "la Console" : custom.getName());

            if (args[0].equalsIgnoreCase("@a")) {
                sender.sendMessage(Messages.TELEPORTED_ALL_TO_SPAWN.get());

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (pl != player) {
                        player.sendMessage(Messages.TELEPORTED_TO_SPAWN_BY.get(parameters));
                    }
                    Spawn.teleportToSpawn(player);
                }
            } else {
                Player player = getPlayer(args[0], sender);
                if (player == null) return true;

                CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                if (sender instanceof Player pl2 && pl2 == player) {
                    sender.sendMessage(Messages.TELEPORTED_TO_SPAWN.get());
                } else {
                    player.sendMessage(Messages.TELEPORTED_TO_SPAWN_BY.get(parameters));

                    parameters.put("%player_name%", customPlayer.getName());
                    sender.sendMessage(Messages.TELEPORTED_OTHER_TO_SPAWN.get());
                }
                Spawn.teleportToSpawn(player);
            }
            return true;
        } else if (args.length == 0 && sender instanceof Player player) {
            if (!player.hasPermission("faylisia.spawn.teleport")) {
                sendNoPermissionMessage(sender);
                return true;
            }

            Spawn.teleportToSpawn(player);
            sender.sendMessage(Messages.TELEPORTED_TO_SPAWN.get());
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    public void sendHelpMessage(CommandSender sender) {
        Map<String, String> parameters = new HashMap<>();

        BaseComponent message = new TextComponent(Messages.HELP_MESSAGE.get(parameters));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://faylis.xyz/wiki/commands"));

        sender.spigot().sendMessage(message);
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        Map<String, String> parameters = new HashMap<>();

        sender.sendMessage(Messages.NO_PERMISSION_MESSAGE.get(parameters));
    }

    public Player getPlayer(String arg, CommandSender sender) {
        if (arg.equals("@s") && sender instanceof Player player) {
            return player;
        }

        Player player = null;

        for (Player pl : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO custom = registry.getOrRegisterPlayer(pl.getUniqueId());
            if (custom.getName().equalsIgnoreCase(arg)) {
                player = pl;
                break;
            }
        }

        if (player == null) {
            sendUnknownPlayerMessage(sender, arg);
        }

        return player;
    }

    public void sendUnknownPlayerMessage(CommandSender sender, String targetName) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%target_name%", targetName);

        sender.sendMessage(Messages.UNKNOWN_PLAYER_MESSAGE.get(parameters));
    }
}
