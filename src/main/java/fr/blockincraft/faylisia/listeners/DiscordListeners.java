package fr.blockincraft.faylisia.listeners;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.DiscordData;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.dto.DiscordTicketDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DiscordListeners extends ListenerAdapter {
    private static final JDA discordBot = Faylisia.getInstance().getDiscordBot();
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) {
            return;
        }

        Role playerRole = guild.getRoleById(DiscordData.playerRoleId);
        if (playerRole != null) {
            for (Member member : guild.getMembers()) {
                boolean hasRole = false;

                for (Role role : member.getRoles()) {
                    if (role == playerRole) {
                        hasRole = true;
                        break;
                    }
                }

                if (!hasRole) {
                    guild.addRoleToMember(member, playerRole).queue();
                }
            }
        }

        Role linkedRole = guild.getRoleById(DiscordData.linkedRoleId);
        registry.getPlayers().forEach((id, custom) -> {
            if (custom.getDiscordUserId() != null) {
                Member member = guild.getMemberById(custom.getDiscordUserId());
                if (member == null) {
                    custom.setDiscordUserId(null);
                } else {
                    if (linkedRole != null) guild.addRoleToMember(member, linkedRole).queue();
                    guild.modifyNickname(member, custom.getName()).queue();
                }
            }
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);

        if (guild != null && event.getGuild() == guild) {
            Role playerRole = guild.getRoleById(DiscordData.playerRoleId);
            if (playerRole != null) {
                guild.addRoleToMember(event.getMember(), playerRole).queue();
            }

            TextChannel channel = guild.getTextChannelById(DiscordData.welcomeId);
            if (channel != null) {
                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setColor(0x4ef542)
                                        .setImage(event.getMember().getAvatarUrl())
                                        .setDescription("Bienvenue <@" + event.getMember().getIdLong() + ">!")
                                        .setTitle("Bienvenue")
                                        .setFooter(DiscordData.footer, DiscordData.footerUrl)
                                        .build()
                        )
                        .build()
                ).queue();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);

        if (guild != null && event.getGuild() == guild) {
            TextChannel channel = guild.getTextChannelById(DiscordData.welcomeId);
            if (channel != null) {
                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setColor(0xf54242)
                                        .setImage(event.getUser().getAvatarUrl())
                                        .setDescription("Aurevoir <@" + event.getUser().getIdLong() + ">!")
                                        .setTitle("Aurevoir")
                                        .setFooter(DiscordData.footer, DiscordData.footerUrl)
                                        .build()
                        )
                        .build()
                ).queue();
            }

            registry.getPlayers().forEach((id, custom) -> {
                if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == event.getUser().getIdLong()) {
                    custom.setDiscordUserId(null);
                }
            });
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() == null) return;

        Button button = event.getButton();
        ButtonInteraction interaction = event.getInteraction();
        User user = interaction.getUser();
        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();
        TextChannel channel = interaction.getTextChannel();

        if (interaction.isFromGuild() && member != null && guild != null && guild.getIdLong() == DiscordData.guildId) {
            if (button.getId().equals(DiscordData.newTicketIdButton)) {
                for (DiscordTicketDTO ticket : registry.getTicketsOf(member.getIdLong())) {
                    if (!ticket.isClosed()) {
                        event.reply("Tu a déja ouvert un ticket <#" + ticket.getChannelId() + ">, ferme le pour pouvoir en créer un nouveau.")
                                .setEphemeral(true)
                                .queue();
                        return;
                    }
                }

                Category category = guild.getCategoryById(DiscordData.ticketsCategoryId);
                if (category == null) {
                    event.reply("Une erreur c'est produite durant la création du ticket.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                guild.createTextChannel("Ticket de " + member.getEffectiveName(), category)
                        .syncPermissionOverrides()
                        .addPermissionOverride(member, 137439464512L, 0L)
                        .addRolePermissionOverride(DiscordData.ticketsRoleId, 532576463936L, 0L)
                        .queue(channelIn -> {
                            DiscordTicketDTO dto = new DiscordTicketDTO(channelIn.getIdLong(), member.getIdLong());
                            registry.createTicket(dto);

                            channelIn.sendMessage(new MessageBuilder("<@&" + DiscordData.ticketsRoleId + ">")
                                    .setEmbeds(
                                            new EmbedBuilder()
                                                    .setTitle("**Support**")
                                                    .setColor(0x9525b8)
                                                    .setDescription("Bonjour <@" + member.getIdLong() + ">!\n" +
                                                            "Un membre du staff va venir répondre à ta requête.\n" +
                                                            "Merci de nous expliquer la raison de ta venue.")
                                                    .setFooter(DiscordData.footer, DiscordData.footerUrl)
                                                    .build()
                                    )
                                    .setActionRows(ActionRow.of(
                                            new ButtonImpl(
                                                    DiscordData.closeTicketIdButton,
                                                    "Fermer le Ticket",
                                                    ButtonStyle.SECONDARY,
                                                    false,
                                                    Emoji.fromEmote("minecraft-1", 850781198577041438L, true)
                                            )
                                    ))
                                    .build()
                            ).queue();

                            event.reply("Ton ticket à bien était créer <#" + channelIn.getIdLong() + ">.")
                                    .setEphemeral(true)
                                    .queue();
                        }, throwable -> event.reply("Une erreur c'est produite durant la création du ticket.")
                                .setEphemeral(true)
                                .queue());
            } else if (button.getId().equals(DiscordData.closeTicketIdButton)) {
                Category category = guild.getCategoryById(DiscordData.closedTicketsCategoryId);
                if (category == null) {
                    event.reply("Une erreur c'est produite durant la fermeture du ticket.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                DiscordTicketDTO ticket = registry.getTicketInChannel(channel.getIdLong());
                if (ticket == null) {
                    event.reply("Le ticket est introuvable.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                ticket.close();
                registry.updateTicket(ticket);

                channel.getManager().setParent(category).queue();
                for (PermissionOverride perm : channel.getMemberPermissionOverrides()) {
                    perm.delete().queue();
                }
                interaction.editButton(new ButtonImpl(
                                DiscordData.deleteTicketIdButton,
                                "Supprimer le Ticket",
                                ButtonStyle.DANGER,
                                false,
                                Emoji.fromEmote("minecraft-1", 850781198577041438L, true)
                        )
                ).queue();
            } else if (button.getId().equals(DiscordData.deleteTicketIdButton)) {
                DiscordTicketDTO ticket = registry.getTicketInChannel(channel.getIdLong());
                if (ticket == null) {
                    event.reply("Le ticket est introuvable.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                registry.removeTicket(ticket);
                channel.delete().reason("Suppression du ticket").queue();
            } else if (button.getId().equals(DiscordData.linkIdButton)) {
                AtomicReference<String> linkedName = new AtomicReference<>(null);

                registry.getPlayers().forEach((id, custom) -> {
                    if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == member.getIdLong()) {
                        linkedName.set(custom.getName());
                    }
                });

                if (linkedName.get() != null) {
                    event.reply("Tu a déja lié ton compte au joueur '" + linkedName.get() + "', si tu veux le lié a un autre joueur il te faut dabord le délié.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                if (registry.hasToken(member)) {
                    event.reply("Tu a déja lié un token en cours, il va expirer deux minutes après sa création, tu pourra alors en créer un nouveau.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                String token = registry.createToken(member);

                event.reply("Voici ton token: " + token + ", pour lié ton compte execute la commande '/link " + token + "' en jeu. Attention le token expire dans deux minutes, il faudra en créer un nouveau.")
                        .setEphemeral(true)
                        .queue();
            } else if (button.getId().equals(DiscordData.unlinkIdButton)) {
                AtomicReference<CustomPlayerDTO> customPlayer = new AtomicReference<>(null);

                registry.getPlayers().forEach((id, custom) -> {
                    if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == member.getIdLong()) {
                        customPlayer.set(custom);
                    }
                });

                if (customPlayer.get() == null) {
                    event.reply("Ton compte discord n'est lié à aucun compte Minecraft.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                CustomPlayerDTO customPl = customPlayer.get();
                customPl.setDiscordUserId(null);

                Player player = Bukkit.getPlayer(customPl.getPlayer());
                if (player != null) {
                    Map<String, String> parameters = new HashMap<>();

                    parameters.put("%account_name%", member.getUser().getName());
                    parameters.put("%account_tag%", member.getUser().getDiscriminator());

                    player.sendMessage(Messages.ACCOUNT_UNLINKED.get(parameters));
                }

                Role role = guild.getRoleById(DiscordData.linkedRoleId);
                if (role != null) {
                    guild.removeRoleFromMember(member, role).queue();
                }

                guild.modifyNickname(member, member.getUser().getName()).queue();
                event.reply("Ton compte discord n'est maintenant plus lié à aucun compte Minecraft.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
