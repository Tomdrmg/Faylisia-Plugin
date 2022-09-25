package fr.blockincraft.faylisia.listeners;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.DiscordData;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.dto.DiscordTicketDTO;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DiscordListeners extends ListenerAdapter {
    private static final JDA discordBot = Faylisia.getInstance().getDiscordBot();
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final List<Runnable> readyActions = new ArrayList<>();
    private static boolean ready = false;

    public static void doWhenReady(@NotNull Runnable action) {
        if (ready) {
            action.run();
        } else {
            readyActions.add(action);
        }
    }

    /**
     * When bot is ready, actualize stored data, roles and channels
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ready = true;

        // Retrieve guild
        // Return if guild doesn't exist
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) {
            return;
        }

        guild.loadMembers().onSuccess(members -> {
            // Get player role and apply it
            // In case of user join discord when bot is off
            Role playerRole = guild.getRoleById(DiscordData.playerRoleId);
            if (playerRole != null) {
                for (Member member : members) {
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

            // Get linked role and apply it or remove it for members which are linked or unlinked during bot is off
            // Theoretically impossible but in case of data was changed directly in database
            Role linkedRole = guild.getRoleById(DiscordData.linkedRoleId);
            registry.getPlayers().forEach((id, custom) -> {
                if (custom.getDiscordUserId() != null) {
                    Member member = null;

                    for (Member m : members) {
                        if (m.getIdLong() == custom.getDiscordUserId()) {
                            member = m;
                            break;
                        }
                    }

                    if (member == null) {
                        custom.setDiscordUserId(null);
                    } else {
                        if (linkedRole != null) guild.addRoleToMember(member, linkedRole).queue();
                        guild.modifyNickname(member, custom.getLastName()).queue();
                    }
                }
            });

            for (Runnable action : readyActions) {
                action.run();
            }
        });
    }

    /**
     * When a member join, add it the player role and send a welcome message
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);

        if (guild != null && event.getGuild() == guild) {
            // Add player role
            Role playerRole = guild.getRoleById(DiscordData.playerRoleId);
            if (playerRole != null) {
                guild.addRoleToMember(event.getMember(), playerRole).queue();
            }

            // Get channel and send welcome message
            TextChannel channel = guild.getTextChannelById(DiscordData.welcomeId);
            if (channel != null) {
                channel.sendMessage(new MessageBuilder("")
                        .setEmbeds(
                                new EmbedBuilder()
                                        .setColor(0x4ef542)
                                        .setThumbnail(event.getMember().getAvatarUrl())
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

    /**
     * When a member quit, send a quit message and unlink his in game account
     */
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);

        if (guild != null && event.getGuild() == guild) {
            // Get channel and send message
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

            // Unlink in game account
            registry.getPlayers().forEach((id, custom) -> {
                if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == event.getUser().getIdLong()) {
                    custom.setDiscordUserId(null);
                }
            });
        }
    }

    /**
     * When a button is clicked do the associated action
     */
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
                // Check if player already have a ticket not closed
                for (DiscordTicketDTO ticket : registry.getTicketsOf(member.getIdLong())) {
                    if (!ticket.isClosed()) {
                        event.reply("Tu a déja ouvert un ticket <#" + ticket.getChannelId() + ">, ferme le pour pouvoir en créer un nouveau.")
                                .setEphemeral(true)
                                .queue();
                        return;
                    }
                }

                Category category = guild.getCategoryById(DiscordData.ticketsCategoryId);
                // Check if we found ticket category
                if (category == null) {
                    event.reply("Une erreur c'est produite durant la création du ticket.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                // Create the channel
                guild.createTextChannel("Ticket de " + member.getEffectiveName(), category)
                        .syncPermissionOverrides()
                        .addPermissionOverride(member, 137439464512L, 0L)
                        .addRolePermissionOverride(DiscordData.ticketsRoleId, 532576463936L, 0L)
                        .queue(channelIn -> {
                            // After creation, register ticket in registry and database
                            DiscordTicketDTO dto = new DiscordTicketDTO(channelIn.getIdLong(), member.getIdLong());
                            registry.createTicket(dto);

                            // Send support message
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

                            // Reply interaction
                            event.reply("Ton ticket à bien était créer <#" + channelIn.getIdLong() + ">.")
                                    .setEphemeral(true)
                                    .queue();
                        }, throwable -> event.reply("Une erreur c'est produite durant la création du ticket.")
                                .setEphemeral(true)
                                .queue());
            } else if (button.getId().equals(DiscordData.closeTicketIdButton)) {
                Category category = guild.getCategoryById(DiscordData.closedTicketsCategoryId);
                // Check if we found closed ticket category
                if (category == null) {
                    event.reply("Une erreur c'est produite durant la fermeture du ticket.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                DiscordTicketDTO ticket = registry.getTicketInChannel(channel.getIdLong());
                // Check if we found ticket
                if (ticket == null) {
                    event.reply("Le ticket est introuvable.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                // Close ticket
                ticket.close();
                registry.updateTicket(ticket);

                // Move ticket to closed ticket category
                channel.getManager().setParent(category).queue();
                for (PermissionOverride perm : channel.getMemberPermissionOverrides()) {
                    perm.delete().queue();
                }
                // Update button from close to delete
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
                // Check if we found ticket
                if (ticket == null) {
                    event.reply("Le ticket est introuvable.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                // Delete it
                registry.removeTicket(ticket);
                channel.delete().reason("Suppression du ticket").queue();
            } else if (button.getId().equals(DiscordData.linkIdButton)) {
                AtomicReference<String> linkedName = new AtomicReference<>(null);

                // Get linked account name
                registry.getPlayers().forEach((id, custom) -> {
                    if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == member.getIdLong()) {
                        linkedName.set(custom.getNameToUse());
                    }
                });

                // Check if account already link
                if (linkedName.get() != null) {
                    event.reply("Tu a déja lié ton compte au joueur '" + linkedName.get() + "', si tu veux le lié a un autre joueur il te faut dabord le délié.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                // Check if user already has a token
                if (registry.hasToken(member)) {
                    event.reply("Tu a déja lié un token en cours, il va expirer deux minutes après sa création, tu pourra alors en créer un nouveau.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                // Create a token
                String token = registry.createToken(member);

                // Send token
                event.reply("Voici ton token: " + token + ", pour lié ton compte execute la commande '/link " + token + "' en jeu. Attention le token expire dans deux minutes, il faudra en créer un nouveau.")
                        .setEphemeral(true)
                        .queue();
            } else if (button.getId().equals(DiscordData.unlinkIdButton)) {
                AtomicReference<CustomPlayerDTO> customPlayer = new AtomicReference<>(null);

                // Get linked account or null if not linked
                registry.getPlayers().forEach((id, custom) -> {
                    if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == member.getIdLong()) {
                        customPlayer.set(custom);
                    }
                });

                // Check if account is link
                if (customPlayer.get() == null) {
                    event.reply("Ton compte discord n'est lié à aucun compte Minecraft.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                // Unlink account
                CustomPlayerDTO customPl = customPlayer.get();
                customPl.setDiscordUserId(null);

                // Send message to in game player if he is online
                Player player = Bukkit.getPlayer(customPl.getPlayer());
                if (player != null) {
                    Map<String, String> parameters = new HashMap<>();

                    parameters.put("%account_name%", member.getUser().getName());
                    parameters.put("%account_tag%", member.getUser().getDiscriminator());

                    player.sendMessage(Messages.ACCOUNT_UNLINKED.get(parameters));
                }

                // Remove linked role
                Role role = guild.getRoleById(DiscordData.linkedRoleId);
                if (role != null) {
                    guild.removeRoleFromMember(member, role).queue();
                }

                // Reset discord pseudo
                guild.modifyNickname(member, member.getUser().getName()).queue();
                event.reply("Ton compte discord n'est maintenant plus lié à aucun compte Minecraft.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            Guild guild = event.getGuild();
            if (guild.getIdLong() == DiscordData.guildId) {
                TextChannel channel = event.getTextChannel();
                if (channel.getIdLong() == DiscordData.chatInGameId) {
                    Member member = event.getMember();
                    if (member == null) {
                        event.getMessage().delete().queue();
                        return;
                    }

                    if (member.getIdLong() == discordBot.getSelfUser().getIdLong()) {
                        return;
                    }

                    if (member.getRoles().stream().map(Role::getIdLong).toList().contains(DiscordData.linkedRoleId)) {
                        AtomicReference<CustomPlayerDTO> customPlayer = new AtomicReference<>(null);

                        registry.getPlayers().forEach((id, custom) -> {
                            if (custom.getDiscordUserId() != null && custom.getDiscordUserId() == member.getIdLong()) {
                                customPlayer.set(custom);
                            }
                        });

                        if (customPlayer.get() == null) {
                            event.getMessage().delete().queue();
                            return;
                        }

                        CustomPlayerDTO finalCustomPlayer = customPlayer.get();

                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(ColorsUtils.translateAll("&9Discord &8- " + finalCustomPlayer.getRank().chatName.replace("%player_name%", finalCustomPlayer.getNameToUse()) + " &8>> &f" + event.getMessage().getContentDisplay()));
                        });
                    } else {
                        event.getMessage().delete().queue();
                    }
                }
            }
        }
    }
}
