package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.menu.InvseeMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "invsee";
    }

    @CommandAction(permission = "faylisia.invsee", onlyPlayers = true)
    public void command(Player player, @CommandParam(type = ParamType.ONLINE_PLAYER) Player target) {
        if (player.getUniqueId().equals(target.getUniqueId())) {
            return;
        }

        new InvseeMenu(target).open(player);
    }
}
