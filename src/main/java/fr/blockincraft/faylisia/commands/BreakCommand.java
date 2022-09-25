package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BreakCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "break";
    }

    @CommandAction(permission = "faylisia.break", onlyPlayers = true)
    public void command(Player player) {
        CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
        boolean canBreak = !customPlayer.getCanBreak();
        customPlayer.setCanBreak(canBreak);

        player.sendMessage(canBreak ? Messages.BREAK_ENABLED.get() : Messages.BREAK_DISABLED.get());
    }
}
