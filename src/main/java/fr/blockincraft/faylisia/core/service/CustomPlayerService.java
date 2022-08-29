package fr.blockincraft.faylisia.core.service;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.core.repository.CustomPlayerRepositoryImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomPlayerService {
    private final SessionFactory sessionFactory = Faylisia.getInstance().getSessionFactory();
    private final CustomPlayerRepositoryImpl repository = new CustomPlayerRepositoryImpl();

    public CustomPlayerDTO getCustomPlayerFromId(UUID playerId) {
        Session session = null;
        Transaction transaction = null;
        CustomPlayerDTO dto = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            CustomPlayer customPlayer = repository.getById(playerId);
            if (customPlayer != null) dto = new CustomPlayerDTO(customPlayer);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return dto;
    }

    public List<CustomPlayerDTO> getAllCustomPlayer() {
        Session session = null;
        Transaction transaction = null;
        List<CustomPlayerDTO> dtoList = new ArrayList<>();

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            List<CustomPlayer> customPlayers = repository.getAll();
            if (customPlayers != null) {
                for (CustomPlayer customPlayer : customPlayers) {
                    dtoList.add(new CustomPlayerDTO(customPlayer));
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

    public void mergeCustomPlayer(CustomPlayerDTO dto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            repository.merge(new CustomPlayer(dto));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    public void persistCustomPlayer(CustomPlayerDTO dto) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            repository.persist(new CustomPlayer(dto));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
