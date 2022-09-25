package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
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
                         @CommandParam(type = ParamType.TEXT) String text) {
        List<String> lines = new ArrayList<>();
        World world = player.getWorld();

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        lines.add("registry.registerBlock(new CustomBlock(" + x + ", " + y + ", " + z + ", UUID.fromString(\"" + world.getUID() + "\"), " + restartAtRegen + ", Material." + finalBlock.name() + ", " + text + "));");
                    }
                }
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("%amount%", String.valueOf(lines.size()));

        player.sendMessage(Messages.CBLOCKS_RESULT_MESSAGE.get(params));
        File file = new File(Faylisia.getInstance().getDataFolder(), player.getUniqueId() + "_" + Date.from(Instant.now()).getTime());
        int i = 2;
        while (file.exists()) {
            file = new File(Faylisia.getInstance().getDataFolder(), player.getUniqueId() + "_" + Date.from(Instant.now()).getTime() + "_" + i);
            i++;
        }

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            for (String line : lines) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
