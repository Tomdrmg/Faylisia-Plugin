package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.DiscordData;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LinkExecutor implements CommandExecutor {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private static final JDA discordBot = Faylisia.getInstance().getDiscordBot();
    private static final String command = "link";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!sender.hasPermission("faylisia.link")) {
                sendNoPermissionMessage(sender);
                return true;
            }

            if (args.length == 1) {
                String token = args[0];

                if (token.length() > 0) {
                    Guild guild = discordBot.getGuildById(DiscordData.guildId);
                    if (guild == null) {
                        sender.sendMessage(Messages.GUILD_NOT_FOUND.get());
                        return true;
                    }

                    CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

                    if (customPlayer.getDiscordUserId() != null) {
                        Member member = guild.getMemberById(customPlayer.getDiscordUserId());

                        if (member == null) {
                            customPlayer.setDiscordUserId(null);
                        } else {
                            Map<String, String> parameters = new HashMap<>();

                            parameters.put("%account_name%", member.getUser().getName());
                            parameters.put("%account_tag%", member.getUser().getDiscriminator());

                            sender.sendMessage(Messages.ALREADY_LINK.get(parameters));
                            return true;
                        }
                    }

                    Member member = registry.validateToken(token);

                    if (member == null) {
                        sender.sendMessage(Messages.INVALID_TOKEN.get());
                        return true;
                    }

                    customPlayer.setDiscordUserId(member.getIdLong());

                    Role role = guild.getRoleById(DiscordData.linkedRoleId);
                    if (role != null) {
                        guild.addRoleToMember(member, role).queue();
                    }

                    guild.modifyNickname(member, customPlayer.getName()).queue();

                    Map<String, String> parameters = new HashMap<>();

                    parameters.put("%account_name%", member.getUser().getName());
                    parameters.put("%account_tag%", member.getUser().getDiscriminator());

                    sender.sendMessage(Messages.SUCCESS_LINK.get(parameters));
                    return true;
                }
            }
        }

        sendHelpMessage(sender);
        return false;
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
