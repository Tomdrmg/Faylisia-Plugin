package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.blocks.BlockType;
import fr.blockincraft.faylisia.blocks.CustomBlock;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class CblocksCommand extends Command {
    @Override
    @NotNull
    public String getCommand() {
        return "cblocks";
    }

    @CommandAction(permission = "faylisia.cblocks", onlyPlayers = true, prefixes = {"generate"})
    public void generate(Player player,
                         @CommandParam(type = ParamType.X) Integer x1,
                         @CommandParam(type = ParamType.Y) Integer y1,
                         @CommandParam(type = ParamType.Z) Integer z1,
                         @CommandParam(type = ParamType.X) Integer x2,
                         @CommandParam(type = ParamType.Y) Integer y2,
                         @CommandParam(type = ParamType.Z) Integer z2,
                         @CommandParam(type = ParamType.BOOLEAN) Boolean restartAtRegen,
                         @CommandParam(type = ParamType.BLOCK_MATERIAL) Material material,
                         @CommandParam(type = ParamType.BLOCK_MATERIAL) Material finalBlock,
                         @CommandParam(type = ParamType.BLOCK_TYPE_LIST) BlockType[] blockTypes) {
        List<CustomBlock> blocks = new ArrayList<>();
        World world = player.getWorld();

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        blocks.add(new CustomBlock(x, y, z, world.getUID(), restartAtRegen, finalBlock, blockTypes));
                    }
                }
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("%amount%", String.valueOf(blocks.size()));

        player.sendMessage(Messages.CBLOCKS_GENERATE_MESSAGE.get(params));

        Registry registry = Faylisia.getInstance().getRegistry();
        for (CustomBlock b : blocks) {
            registry.registerBlock(b);
        }
    }

    @CommandAction(permission = "faylisia.cblocks", onlyPlayers = true, prefixes = {"remove"})
    public void remove(Player player,
                         @CommandParam(type = ParamType.X) Integer x1,
                         @CommandParam(type = ParamType.Y) Integer y1,
                         @CommandParam(type = ParamType.Z) Integer z1,
                         @CommandParam(type = ParamType.X) Integer x2,
                         @CommandParam(type = ParamType.Y) Integer y2,
                         @CommandParam(type = ParamType.Z) Integer z2) {
        Registry registry = Faylisia.getInstance().getRegistry();
        World world = player.getWorld();
        int amount = 0;

        for (CustomBlock block : registry.getBlocks()) {
            if (block.getWorld().equals(world.getUID()) &&
                    block.getX() >= Math.min(x1, x2) && block.getX() <= Math.max(x1, x2) &&
                    block.getY() >= Math.min(y1, y2) && block.getY() <= Math.max(y1, y2) &&
                    block.getZ() >= Math.min(z1, z2) && block.getZ() <= Math.max(z1, z2)) {
                registry.removeBlock(block);
                amount ++;
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("%amount%", String.valueOf(amount));

        player.sendMessage(Messages.CBLOCKS_REMOVE_MESSAGE.get(params));
    }

    @CommandAction(permission = "faylisia.cblocks", onlyPlayers = false, prefixes = {"reload"})
    public void reload(CommandSender sender) {
        try {
            Faylisia.getInstance().getRegistry().reloadBlocks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
