package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.DiscordData;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LinkCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "link";
    }

    @CommandAction(permission = "faylisia.link", onlyPlayers = true)
    public void command(Player player, @CommandParam(type = ParamType.TOKEN) String token) {
        if (token.length() > 0) {
            Guild guild = Faylisia.getInstance().getDiscordBot().getGuildById(DiscordData.guildId);
            if (guild == null) {
                player.sendMessage(Messages.GUILD_NOT_FOUND.get());
                return;
            }

            CustomPlayerDTO customPlayer = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(player.getUniqueId());

            if (customPlayer.getDiscordUserId() != null) {
                Member member = guild.getMemberById(customPlayer.getDiscordUserId());

                if (member == null) {
                    customPlayer.setDiscordUserId(null);
                } else {
                    Map<String, String> parameters = new HashMap<>();

                    parameters.put("%account_name%", member.getUser().getName());
                    parameters.put("%account_tag%", member.getUser().getDiscriminator());

                    player.sendMessage(Messages.ALREADY_LINK.get(parameters));
                    return;
                }
            }

            Member member = Faylisia.getInstance().getRegistry().validateToken(token);

            if (member == null) {
                player.sendMessage(Messages.INVALID_TOKEN.get());
                return;
            }

            customPlayer.setDiscordUserId(member.getIdLong());

            Role role = guild.getRoleById(DiscordData.linkedRoleId);
            if (role != null) {
                guild.addRoleToMember(member, role).queue();
            }

            guild.modifyNickname(member, customPlayer.getNameToUse()).queue();

            Map<String, String> parameters = new HashMap<>();

            parameters.put("%account_name%", member.getUser().getName());
            parameters.put("%account_tag%", member.getUser().getDiscriminator());

            player.sendMessage(Messages.SUCCESS_LINK.get(parameters));
        }
    }
}
