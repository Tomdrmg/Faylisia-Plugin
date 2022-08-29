package fr.blockincraft.faylisia.core.repository;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;

public class CustomPlayerRepositoryImpl {
    private final SessionFactory sessionFactory = Faylisia.getInstance().getSessionFactory();

    public CustomPlayer getById(UUID playerId) {
        Session session = sessionFactory.getCurrentSession();

        return session.get(CustomPlayer.class, playerId);
    }

    public List<CustomPlayer> getAll() {
        Session session = sessionFactory.getCurrentSession();

        return session.createNamedQuery("getAllPlayers", CustomPlayer.class).getResultList();
    }

    public void merge(CustomPlayer customPlayer) {
        Session session = sessionFactory.getCurrentSession();

        session.merge(customPlayer);
    }

    public void persist(CustomPlayer customPlayer) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(customPlayer);
    }
}
