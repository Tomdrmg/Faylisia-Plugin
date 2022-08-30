package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.configurable.Provider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DiscordExecutor implements CommandExecutor {
    private static final String command = "discord";
    private static final JDA discordBot = Faylisia.getInstance().getDiscordBot();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (!sender.hasPermission("faylisia.discord")) {
            sendNoPermissionMessage(sender);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            if (args[1].equalsIgnoreCase("rules")) {
                Guild guild = discordBot.getGuildById(Provider.guildId);
                if (guild == null) {
                    sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
                    return true;
                }

                TextChannel channel = discordBot.getTextChannelById(Provider.rulesId);
                if (channel == null) {
                    sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
                    return true;
                }

                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setAuthor("Faylisia")
                                        .setTitle("**Règles**")
                                        .setDescription("")
                                        .setColor(0x9525b8)
                                        .build()
                        )
                        .build()
                ).submit();
            } else if (args[1].equalsIgnoreCase("tickets")) {
                Guild guild = discordBot.getGuildById(Provider.guildId);
                if (guild == null) {
                    sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
                    return true;
                }

                TextChannel channel = discordBot.getTextChannelById(Provider.ticketsId);
                if (channel == null) {
                    sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
                    return true;
                }

                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setAuthor("Faylisia")
                                        .setTitle("**Tickets**")
                                        .setDescription("")
                                        .setColor(0x9525b8)
                                        .build()
                        )
                        .setActionRows(ActionRow.of(
                                new ButtonImpl(
                                        "create_ticket",
                                        "Nouveau",
                                        ButtonStyle.PRIMARY,
                                        false,
                                        Emoji.fromEmote("Frog_fk", 753797501982867456L, false)
                                )
                        ))
                        .build()
                ).submit();
            }
        }

        sendHelpMessage(sender);
        return true;
    }

    public void sendHelpMessage(CommandSender sender) {
        Map<String, String> parameters = new HashMap<>();

        BaseComponent message = new TextComponent(Messages.HELP_MESSAGE.get(parameters));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://faylis.xyz/wiki/commands"));

        sender.spigot().sendMessage(message);
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        Map<String, String> parameters = new HashMap<>();

        sender.sendMessage(Messages.NO_PERMISSION_MESSAGE.get(parameters));
    }
}
