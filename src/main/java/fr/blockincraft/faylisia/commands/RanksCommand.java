package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.permission.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class  RanksCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "ranks";
    }

    @CommandAction(permission = "faylisia.ranks.get", onlyPlayers = false, prefixes = {"get"})
    public void getPlayerRank(CommandSender sender, @CommandParam(type = ParamType.PLAYER) CustomPlayerDTO player) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%player_name%", player.getNameToUse());
        parameters.put("%rank%", player.getRank().name);

        sender.sendMessage(Messages.RANK_OF.get(parameters));
    }

    @CommandAction(permission = "faylisia.ranks.edit", onlyPlayers = false, prefixes = {"set"})
    public void setPlayerRank(CommandSender sender, @CommandParam(type = ParamType.PLAYER) CustomPlayerDTO target, @CommandParam(type = ParamType.RANK) Ranks rank) {
        if (sender instanceof ConsoleCommandSender console) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("%player_name%", target.getNameToUse());
            parameters.put("%rank%", rank.name);

            console.sendMessage(Messages.BEEN_SET_PLAYER_RANK_TO.get(parameters));
            parameters.put("%player_name%", "La Console");

            Player pl = Bukkit.getPlayer(target.getPlayer());
            if (pl != null) {
                pl.sendMessage(Messages.PLAYER_SET_YOUR_RANK_TO.get(parameters));
            }

            target.setRank(rank);
        } else if (sender instanceof Player player) {
            if (target.getPlayer().equals(player.getUniqueId())) {
                player.sendMessage(Messages.CANNOT_SET_YOUR_RANK.get());
                return;
            }

            CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
            Ranks senderRank = customPlayer.getRank();
            Ranks targetRank = target.getRank();

            if (senderRank.index >= targetRank.index) {
                player.sendMessage(Messages.CANNOT_SET_RANK_OF_EQUAL_OR_SUPERIOR_PLAYER.get());
                return;
            }
            if (senderRank.index >= rank.index) {
                player.sendMessage(Messages.CANNOT_SET_A_EQUAL_OR_SUPERIOR_RANK.get());
                return;
            }

            Map<String, String> parameters = new HashMap<>();
            parameters.put("%player_name%", target.getNameToUse());
            parameters.put("%rank%", rank.name);

            player.sendMessage(Messages.BEEN_SET_PLAYER_RANK_TO.get(parameters));
            parameters.put("%player_name%", customPlayer.getNameToUse());

            Player pl = Bukkit.getPlayer(target.getPlayer());
            if (pl != null) {
                pl.sendMessage(Messages.PLAYER_SET_YOUR_RANK_TO.get(parameters));
            }

            target.setRank(rank);
        }
    }
}
