package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.configurable.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "fly";
    }

    @CommandAction(permission = "faylisia.fly", onlyPlayers = true, prefixes = {"cc"})
    public void command(Player player) {
        boolean newValue = !player.getAllowFlight();
        player.setAllowFlight(newValue);

        if (newValue) {
            player.sendMessage(Messages.FLY_ENABLED.get());
        } else {
            player.sendMessage(Messages.FLY_DISABLED.get());
        }
    }
}
