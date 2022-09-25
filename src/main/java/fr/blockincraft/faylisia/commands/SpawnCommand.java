package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.map.Spawn;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpawnCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "spawn";
    }

    @CommandAction(permission = "faylisia.spawn.teleport", onlyPlayers = true)
    public void teleport(Player player) {
        Spawn.teleportToSpawn(player);
        player.sendMessage(Messages.TELEPORTED_TO_SPAWN.get());
    }

    @CommandAction(permission = "faylisia.spawn.teleport_others", onlyPlayers = false)
    public void teleportOthers(CommandSender sender, @CommandParam(type = ParamType.ONLINE_PLAYER_SUPPORT_ALL) Player[] players) {
        CustomPlayerDTO player = sender instanceof Player pl ? Faylisia.getInstance().getRegistry().getOrRegisterPlayer(pl.getUniqueId()) : null;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("%player_name%", player == null ? "La Console" : player.getNameToUse());

        if (players.length == 1) {
            CustomPlayerDTO target = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(players[0].getUniqueId());

            if (player != null && player.getPlayer().equals(players[0].getUniqueId())) {
                sender.sendMessage(Messages.TELEPORTED_TO_SPAWN.get());
            } else {
                players[0].sendMessage(Messages.TELEPORTED_TO_SPAWN_BY.get(parameters));

                parameters.put("%player_name%", target.getNameToUse());
                sender.sendMessage(Messages.TELEPORTED_OTHER_TO_SPAWN.get());
            }

            Spawn.teleportToSpawn(players[0]);
        } else {
            sender.sendMessage(Messages.TELEPORTED_ALL_TO_SPAWN.get());

            for (Player pl : players) {
                pl.sendMessage(Messages.TELEPORTED_TO_SPAWN_BY.get(parameters));
                Spawn.teleportToSpawn(pl);
            }
        }
    }
}
