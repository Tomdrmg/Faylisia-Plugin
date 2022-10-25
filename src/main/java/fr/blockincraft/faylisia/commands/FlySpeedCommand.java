package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FlySpeedCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "flyspeed";
    }

    @CommandAction(permission = "faylisia.flyspeed", onlyPlayers = true)
    public void flyspeed(Player player, @CommandParam(type = ParamType.FLY_SPEED) Integer speed) {
        player.setFlySpeed((float) (speed / 10.0));

        Map<String, String> params = new HashMap<>();
        params.put("%speed%", String.valueOf(speed * 100));

        player.sendMessage(Messages.NEW_FLY_PEED_SET.get(params));
    }
}
