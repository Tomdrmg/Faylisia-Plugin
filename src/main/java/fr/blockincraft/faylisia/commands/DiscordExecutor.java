package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.configurable.DiscordData;
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
                Guild guild = discordBot.getGuildById(DiscordData.guildId);
                if (guild == null) {
                    sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
                    return true;
                }

                TextChannel channel = guild.getTextChannelById(DiscordData.rulesId);
                if (channel == null) {
                    sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
                    return true;
                }

                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setFooter(DiscordData.footer, DiscordData.footerUrl)
                                        .setTitle("**Règles**")
                                        .setDescription("""
                                                """)
                                        .setColor(0x9525b8)
                                        .build()
                        )
                        .build()
                ).queue();
                return true;
            } else if (args[1].equalsIgnoreCase("tickets")) {
                Guild guild = discordBot.getGuildById(DiscordData.guildId);
                if (guild == null) {
                    sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
                    return true;
                }

                TextChannel channel = guild.getTextChannelById(DiscordData.ticketsId);
                if (channel == null) {
                    sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
                    return true;
                }

                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setFooter(DiscordData.footer, DiscordData.footerUrl)
                                        .setTitle("**Tickets**")
                                        .setDescription("""
                                                Si tu a besoins d'aide ou une question, tu peux créer
                                                un ticket en cliquant sur le boutton en dessous.""")
                                        .setColor(0x9525b8)
                                        .build()
                        )
                        .setActionRows(ActionRow.of(
                                new ButtonImpl(
                                        DiscordData.newTicketIdButton,
                                        "Nouveau Ticket",
                                        ButtonStyle.SUCCESS,
                                        false,
                                        Emoji.fromEmote("minecraft-1", 850781198577041438L, true)
                                )
                        ))
                        .build()
                ).queue(message -> {
                    Map<String, String> parameters = new HashMap<>();

                    parameters.put("%channel%", channel.getName());
                    parameters.put("%channel_id%", channel.getId());

                    sender.sendMessage(Messages.MESSAGE_WAS_BEEN_SEND.get(parameters));
                }, throwable -> {
                    sender.sendMessage(Messages.ERROR_WHEN_SENDING_MESSAGE.get());
                });


                return true;
            } else if (args[1].equalsIgnoreCase("link")) {
                Guild guild = discordBot.getGuildById(DiscordData.guildId);
                if (guild == null) {
                    sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
                    return true;
                }

                TextChannel channel = guild.getTextChannelById(DiscordData.linkId);
                if (channel == null) {
                    sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
                    return true;
                }

                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setFooter(DiscordData.footer, DiscordData.footerUrl)
                                        .setTitle("**Link**")
                                        .setDescription("""
                                                Pour accéder à certaines fonctionnalité du discord,
                                                tu doit lié ton compte minecraft à ton compte MC.
                                                Pour le lié ton compte il faut que tu clique sur le
                                                bouton 'Lier mon compte' puis que tu execute la
                                                command '/link <ton_token>'.""")
                                        .setColor(0x9525b8)
                                        .build()
                        )
                        .setActionRows(ActionRow.of(
                                new ButtonImpl(
                                        DiscordData.linkIdButton,
                                        "Lier mon compte",
                                        ButtonStyle.PRIMARY,
                                        false,
                                        Emoji.fromEmote("minecraft-1", 850781198577041438L, true)
                                ),
                                new ButtonImpl(
                                        DiscordData.unlinkIdButton,
                                        "Délier mon compte",
                                        ButtonStyle.SECONDARY,
                                        false,
                                        Emoji.fromEmote("minecraft-1", 850781198577041438L, true)
                                )
                        ))
                        .build()
                ).queue(message -> {
                    Map<String, String> parameters = new HashMap<>();

                    parameters.put("%channel%", channel.getName());
                    parameters.put("%channel_id%", channel.getId());

                    sender.sendMessage(Messages.MESSAGE_WAS_BEEN_SEND.get(parameters));
                }, throwable -> {
                    sender.sendMessage(Messages.ERROR_WHEN_SENDING_MESSAGE.get());
                });


                return true;
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
