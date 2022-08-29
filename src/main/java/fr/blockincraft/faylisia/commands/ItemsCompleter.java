package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemsCompleter implements TabCompleter {
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("faylisia.items.menu") && "menu".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("menu");
            if (sender.hasPermission("faylisia.items.give") && "give".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("give");
            if (sender.hasPermission("faylisia.items.recipe") && "recipe".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("recipe");
            if ("help".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("help");
        }

        if (args.length == 2) {
            if (sender.hasPermission("faylisia.items.give") && "give".equalsIgnoreCase(args[0])) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getUniqueId());
                    if (custom.getName().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add(player.getName());
                }
                if ("@s".toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add("@s");
                if ("@a".toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add("@a");
            }

            if (sender.hasPermission("faylisia.items.recipe") && "recipe".equalsIgnoreCase(args[0])) {
                List<CustomItem> items = registry.getItems();

                for (CustomItem item : items) {
                    if (item.getId().startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add(item.getId());
                }
            }
        }

        if (args.length == 3) {
            if (sender.hasPermission("faylisia.items.give") && "give".equalsIgnoreCase(args[0])) {
                List<CustomItem> items = registry.getItems();

                for (CustomItem item : items) {
                    if (item.getId().startsWith(args[2].toLowerCase(Locale.ROOT))) completion.add(item.getId());
                }
            }
        }

        return completion;
    }
}
