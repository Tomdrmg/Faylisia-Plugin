package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MsgCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "msg";
    }

    @CommandAction(permission = "faylisia.msg", onlyPlayers = true)
    public void msg(Player player, @CommandParam(type = ParamType.ONLINE_PLAYER) Player target, @CommandParam(type = ParamType.MESSAGE) String message) {
        CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
        CustomPlayerDTO customTarget = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(target.getUniqueId());

        Map<String, String> params = new HashMap<>();

        params.put("%message%", message);
        params.put("%sender_name%", customPlayer.getNameToUse());
        params.put("%target_name%", customTarget.getNameToUse());

        target.sendMessage(Messages.MSG_FROM_MESSAGE.get(params));
        player.sendMessage(Messages.MSG_TO_MESSAGE.get(params));
        for (CustomPlayerDTO custom : Faylisia.getInstance().getRegistry().getPlayers().values()) {
            if (custom.isChatSpy()) {
                Player pl = Bukkit.getPlayer(custom.getPlayer());
                if (pl != null) {
                    pl.sendMessage(Messages.MSG_SPY_FROM_TO_MESSAGE.get(params));
                }
            }
        }
    }

    @CommandAction(permission = "faylisia.msg.spy", onlyPlayers = true, prefixes = {"spy"})
    public void spy(Player player, @CommandParam(type = ParamType.ENABLE_STATE) Boolean enable) {
        CustomPlayerDTO custom = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());
        custom.setChatSpy(enable);

        player.sendMessage(enable ? Messages.CHAT_SPY_ENABLE_MESSAGE.get() : Messages.CHAT_SPY_DISABLE_MESSAGE.get());
    }
}
