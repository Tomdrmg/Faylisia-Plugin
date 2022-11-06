package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.entity.CustomEntityType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SummonCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "summon";
    }

    @CommandAction(onlyPlayers = false, prefixes = {}, permission = "faylisia.summon")
    public void summon(CommandSender sender, @CommandParam(type = ParamType.ENTITY_TYPE) CustomEntityType entityType, @CommandParam(type = ParamType.X) Integer x, @CommandParam(type = ParamType.Y) Integer y, @CommandParam(type = ParamType.Z) Integer z) {
        World world = sender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().get(0);

        entityType.spawn(x, y, z);
    }
}
