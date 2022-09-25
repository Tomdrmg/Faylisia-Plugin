package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NickCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "nick";
    }

    @CommandAction(permission = "faylisia.nick", onlyPlayers = true)
    public void active(Player player, @CommandParam(type = ParamType.ENABLE_STATE) Boolean enable) {
        CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());

        customPlayer.setCustomNameEnabled(enable);

        if (enable) {
            player.sendMessage(Messages.NICK_ENABLED.get());
        } else {
            player.sendMessage(Messages.NICK_DISABLED.get());
        }
    }

    @CommandAction(permission = "faylisia.nick", onlyPlayers = true, prefixes = {"set"})
    public void changeNickname(Player player, @CommandParam(type = ParamType.NAME) String name) {
        CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());

        customPlayer.setCustomName(name);

        Map<String, String> params = new HashMap<>();
        params.put("%nickname%", name);
        player.sendMessage(Messages.NICKNAME_CHANGED_MESSAGE.get(params));
    }

    @CommandAction(permission = "faylisia.nick.get", onlyPlayers = false, prefixes = {"get"})
    public void get(CommandSender sender, @CommandParam(type = ParamType.PLAYER_NON_NICK) CustomPlayerDTO player) {
        Map<String, String> params = new HashMap<>();
        params.put("%player_name%", player.getLastName());
        params.put("%nickname%", player.getCustomName());

        if (player.isCustomNameEnabled()) {
            sender.sendMessage(Messages.ENABLED_NICK_STATE_MESSAGE.get(params));
        } else {
            sender.sendMessage(Messages.DISABLED_NICK_STATE_MESSAGE.get(params));
        }
    }

    @CommandAction(permission = "faylisia.nick.get", onlyPlayers = false, prefixes = {"realname"})
    public void realName(CommandSender sender, @CommandParam(type = ParamType.PLAYER) CustomPlayerDTO player) {
        Map<String, String> params = new HashMap<>();
        params.put("%player_name%", player.getLastName());
        params.put("%nickname%", player.getCustomName());

        if (player.isCustomNameEnabled()) {
            sender.sendMessage(Messages.ENABLED_NICK_STATE_MESSAGE.get(params));
        } else {
            sender.sendMessage(Messages.DISABLED_NICK_STATE_MESSAGE.get(params));
        }
    }
}
