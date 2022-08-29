package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.permission.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RanksCompleter implements TabCompleter {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("faylisia.ranks.get") && "get".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("get");
            if (sender.hasPermission("faylisia.ranks.edit") && "set".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("set");
            if ("help".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("help");
        } else if (args.length == 2) {
            if (sender.hasPermission("faylisia.ranks.get") && args[0].equalsIgnoreCase("get")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getUniqueId());
                    if (custom.getName().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add(player.getName());
                }
            } else if (sender.hasPermission("faylisia.ranks.edit") && args[0].equalsIgnoreCase("set")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getUniqueId());
                    if (custom.getName().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender.hasPermission("faylisia.ranks.edit")) {
            if (sender instanceof Player player) {
                CustomPlayerDTO custom = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
                Ranks rank = custom.getRank();

                for (Ranks rankIn : Ranks.values()) {
                    if (rank.index < rankIn.index || rank.index == 0) {
                        if (rankIn.name.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT))) completion.add(rankIn.name);
                    }
                }
            }
        }

        return completion;
    }
}
