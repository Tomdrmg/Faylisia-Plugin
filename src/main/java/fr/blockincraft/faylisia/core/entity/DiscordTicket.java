package fr.blockincraft.faylisia.core.entity;

import fr.blockincraft.faylisia.core.dto.DiscordTicketDTO;
import jakarta.persistence.*;

@NamedQuery(query = "SELECT d FROM DiscordTicket d", name = "getAllTickets")
@NamedQuery(query = "SELECT d FROM DiscordTicket d WHERE d.userId LIKE :userId", name = "getAllTicketsOf")
@Entity
public class DiscordTicket {
    @Id
    @Column(name = "channel_id", unique = true)
    long channelId;
    @Column(name = "user_id")
    long userId;
    @Column(name = "cration_time")
    long creationTime;
    boolean closed;

    public DiscordTicket() {

    }

    public DiscordTicket(DiscordTicketDTO dto) {
        this.channelId = dto.getChannelId();
        this.userId = dto.getUserId();
        this.creationTime = dto.getCreationTime();
        this.closed = dto.isClosed();
    }

    public long getUserId() {
        return userId;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isClosed() {
        return closed;
    }
}
