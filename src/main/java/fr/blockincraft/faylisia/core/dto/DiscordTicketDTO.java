package fr.blockincraft.faylisia.core.dto;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.configurable.DiscordData;
import fr.blockincraft.faylisia.core.entity.DiscordTicket;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;
import java.util.Date;

public class DiscordTicketDTO {
    private static final JDA discordBot = Faylisia.getInstance().getDiscordBot();

    private long channelId;
    private long userId;
    private long creationTime;
    private boolean closed;

    public DiscordTicketDTO(long channelId, long userId) {
        this.channelId = channelId;
        this.userId = userId;
        this.creationTime = Date.from(Instant.now()).getTime();
        this.closed = false;
    }

    public DiscordTicketDTO(DiscordTicket model) {
        this.channelId = model.getChannelId();
        this.userId = model.getUserId();
        this.creationTime = model.getCreationTime();
        this.closed = model.isClosed();
    }

    public long getChannelId() {
        return channelId;
    }

    public long getUserId() {
        return userId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        closed = true;
    }

    public long getTimeSinceCreation() {
        return Date.from(Instant.now()).getTime() - creationTime;
    }

    public TextChannel getChannel() {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) return null;

        return guild.getTextChannelById(channelId);
    }

    public Member getUser() {
        Guild guild = discordBot.getGuildById(DiscordData.guildId);
        if (guild == null) return null;

        return guild.getMemberById(userId);
    }
}
