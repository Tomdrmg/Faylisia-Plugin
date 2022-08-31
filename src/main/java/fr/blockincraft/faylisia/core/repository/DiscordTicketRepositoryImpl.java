package fr.blockincraft.faylisia.core.repository;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.entity.DiscordTicket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class DiscordTicketRepositoryImpl {
    private final SessionFactory sessionFactory = Faylisia.getInstance().getSessionFactory();

    public DiscordTicket getById(long channelId) {
        Session session = sessionFactory.getCurrentSession();

        return session.get(DiscordTicket.class, channelId);
    }

    public List<DiscordTicket> getAll() {
        Session session = sessionFactory.getCurrentSession();

        return session.createNamedQuery("getAllTickets", DiscordTicket.class).getResultList();
    }

    public List<DiscordTicket> getAllOf(long userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createNamedQuery("getAllTicketsOf", DiscordTicket.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void merge(DiscordTicket ticket) {
        Session session = sessionFactory.getCurrentSession();

        session.merge(ticket);
    }

    public void persist(DiscordTicket ticket) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(ticket);
    }

    public void remove(DiscordTicket ticket) {
        Session session = sessionFactory.getCurrentSession();

        session.remove(ticket);
    }
}
