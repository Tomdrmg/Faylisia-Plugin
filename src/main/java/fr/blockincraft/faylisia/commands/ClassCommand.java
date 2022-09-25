package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.menu.ClassMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClassCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "class";
    }

    @CommandAction(permission = "faylisia.class", onlyPlayers = true)
    public void command(Player player) {
        new ClassMenu(Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId()), null).open(player);
    }
}
