package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.permission.Ranks;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RanksExecutor implements CommandExecutor {
    private static final String command = "ranks";
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
            if (!sender.hasPermission("faylisia.ranks.get")) {
                sendNoPermissionMessage(sender);
                return true;
            }

            Player player = getPlayer(args[1], sender);
            if (player == null) return true;

            Map<String, String> parameters = new HashMap<>();
            CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getUniqueId());

            parameters.put("%player_name%", custom.getName());
            parameters.put("%rank%", custom.getRank().name);

            sender.sendMessage(Messages.RANK_OF.get(parameters));
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender instanceof Player player) {
            if (!player.hasPermission("faylisia.ranks.edit")) {
                sendNoPermissionMessage(sender);
                return true;
            }

            Player target = getPlayer(args[1], sender);
            if (target == null) return true;

            if (target == player) {
                player.sendMessage(Messages.CANNOT_SET_YOUR_RANK.get());
                return true;
            }

            Ranks rank = getRank(args[2], sender);
            if (rank == null) return true;

            CustomPlayerDTO customSender = registry.getOrRegisterPlayer(player.getUniqueId());
            Ranks senderRank = customSender.getRank();

            CustomPlayerDTO customTarget = registry.getOrRegisterPlayer(target.getUniqueId());
            Ranks targetRank = customTarget.getRank();

            if (senderRank.index >= targetRank.index) {
                player.sendMessage(Messages.CANNOT_SET_RANK_OF_EQUAL_OR_SUPERIOR_PLAYER.get());
                return true;
            }
            if (senderRank.index >= rank.index) {
                player.sendMessage(Messages.CANNOT_SET_A_EQUAL_OR_SUPERIOR_RANK.get());
                return true;
            }

            Map<String, String> parameters = new HashMap<>();
            parameters.put("%player_name%", customTarget.getName());
            parameters.put("%rank%", rank.name);

            player.sendMessage(Messages.BEEN_SET_PLAYER_RANK_TO.get(parameters));
            parameters.put("%player_name%", customSender.getName());

            target.sendMessage(Messages.PLAYER_SET_YOUR_RANK_TO.get(parameters));
            customTarget.setRank(rank);

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

        parameters.put("%command%", command);

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

    public Ranks getRank(String arg, CommandSender sender) {
        Ranks rank = null;

        for (Ranks rankIn : Ranks.values()) {
            if (rankIn.name.equalsIgnoreCase(arg)) {
                rank = rankIn;
                break;
            }
        }

        if (rank == null) {
            sendUnknownRankMessage(sender, arg);
        }

        return rank;
    }

    public void sendUnknownRankMessage(CommandSender sender, String rank) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%rank%", rank);

        sender.sendMessage(Messages.UNKNOWN_RANK_MESSAGE.get(parameters));
    }
}
