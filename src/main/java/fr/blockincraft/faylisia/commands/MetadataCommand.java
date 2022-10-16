package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.management.Categories;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class MetadataCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "metadate";
    }

    @CommandAction(permission = "faylisia.metadata", prefixes = {"generate"}, onlyPlayers = false)
    public void generateFile(CommandSender sender) {
        try {
            Registry registry = Faylisia.getInstance().getRegistry();

            File metadataFile = new File(Faylisia.getInstance().getDataFolder(), "METADATA.md");
            metadataFile.createNewFile();

            StringBuilder content = new StringBuilder("""
                    # Plugin Metadata
                    
                    ## Items Metadata
                    
                    | Id | Custom Model Data |
                    |---|---:|
                    """);

            for (CustomItem item : registry.getItems()) {
                content.append("| ").append(item.getId()).append(" | ").append(item.getCustomModelData()).append(" |\n");
            }

            content.append("\n");
            content.append("## Categories Metadata\n");
            content.append("\n");
            content.append("| Id | Custom Model Data  |\n");
            content.append("|---|---:|\n");

            for (Categories category : Categories.values()) {
                content.append("| ").append(category.name().toLowerCase(Locale.ROOT)).append(" | ").append(category.customModelData).append(" |\n");
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadataFile)));
            writer.write(content.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
