package fr.blockincraft.faylisia.core.service;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.dto.DiscordTicketDTO;
import fr.blockincraft.faylisia.core.entity.DiscordTicket;
import fr.blockincraft.faylisia.core.repository.DiscordTicketRepositoryImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DiscordTicketService {
    private final SessionFactory sessionFactory = Faylisia.getInstance().getSessionFactory();
    private final DiscordTicketRepositoryImpl repository = new DiscordTicketRepositoryImpl();

    public DiscordTicketDTO getDiscordTicketFromId(long channelId) {
        Session session = null;
        Transaction transaction = null;
        DiscordTicketDTO dto = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            DiscordTicket discordTicket = repository.getById(channelId);
            if (discordTicket != null) dto = new DiscordTicketDTO(discordTicket);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return dto;
    }

    public List<DiscordTicketDTO> getAllDiscordTickets() {
        Session session = null;
        Transaction transaction = null;
        List<DiscordTicketDTO> dtoList = new ArrayList<>();

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            List<DiscordTicket> discordTickets = repository.getAll();
            if (discordTickets != null) {
                for (DiscordTicket discordTicket : discordTickets) {
                    dtoList.add(new DiscordTicketDTO(discordTicket));
                }
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return dtoList;
    }

    public List<DiscordTicketDTO> getAllDiscordTicketsOf(long userId) {
        Session session = null;
        Transaction transaction = null;
        List<DiscordTicketDTO> dtoList = new ArrayList<>();

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            List<DiscordTicket> discordTickets = repository.getAllOf(userId);
            if (discordTickets != null) {
                for (DiscordTicket discordTicket : discordTickets) {
                    dtoList.add(new DiscordTicketDTO(discordTicket));
                }
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return dtoList;
    }

    public void mergeDiscordTicket(DiscordTicketDTO dto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            repository.merge(new DiscordTicket(dto));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    public void persistDiscordTicket(DiscordTicketDTO dto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            repository.persist(new DiscordTicket(dto));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    public void removeDiscordTicket(DiscordTicketDTO dto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            repository.remove(new DiscordTicket(dto));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
