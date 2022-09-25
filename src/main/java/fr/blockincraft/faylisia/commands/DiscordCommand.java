package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.configurable.DiscordData;
import fr.blockincraft.faylisia.configurable.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DiscordCommand extends Command {
    private static final JDA discordBot = Faylisia.getInstance().getDiscordBot();

    @Override
    public @NotNull String getCommand() {
        return "discord";
    }

    @CommandAction(permission = "faylisia.discord", onlyPlayers = false, prefixes = {"send", "link"})
    public void linkMessage(CommandSender sender) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) {
            sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
            return;
        }

        TextChannel channel = guild.getTextChannelById(DiscordData.linkId);
        if (channel == null) {
            sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
            return;
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
    }

    @CommandAction(permission = "faylisia.discord", onlyPlayers = false, prefixes = {"send", "rules"})
    public void rulesMessage(CommandSender sender) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) {
            sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
            return;
        }

        TextChannel channel = guild.getTextChannelById(DiscordData.rulesId);
        if (channel == null) {
            sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
            return;
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
    }

    @CommandAction(permission = "faylisia.discord", onlyPlayers = false, prefixes = {"send", "tickets"})
    public void ticketsMessage(CommandSender sender) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) {
            sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
            return;
        }

        TextChannel channel = guild.getTextChannelById(DiscordData.ticketsId);
        if (channel == null) {
            sender.sendMessage(Messages.CHANNEL_NOT_FOUND.get());
            return;
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
    }
}
