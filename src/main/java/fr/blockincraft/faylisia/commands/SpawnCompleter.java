package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpawnCompleter implements TabCompleter {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = new ArrayList<>();

        if (args.length == 1) {
            if ("help".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("help");

            if (sender.hasPermission("faylisia.spawn.teleport_others")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getUniqueId());
                    if (custom.getName().toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                        completion.add(player.getName());
                }
                if ("@a".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("@a");
            }
        }

        return completion;
    }
}
