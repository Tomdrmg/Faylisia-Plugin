package fr.blockincraft.faylisia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiscordCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completion = new ArrayList<>();

        if (sender.hasPermission("faylisia.discord")) {
            if (args.length == 1 && "send".toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) completion.add("send");

            if (args.length == 2) {
                if ("rules".toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add("rules");
                if ("tickets".toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add("tickets");
                if ("link".toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) completion.add("link");
            }
        }

        return completion;
    }
}
