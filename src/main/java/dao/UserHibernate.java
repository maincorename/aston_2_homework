package dao;

import entity.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.Optional;

public class UserHibernate implements UserRepository {
    private final SessionFactory sessionFactory;

    public UserHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public UserHibernate() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void create(User user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if(transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public Optional<User> select(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
          throw new RuntimeException("Ошибка при поиске пользователя, ID: " + id, e);
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка при обновлении пользователя.", e);
        }
    }

    @Override
    public User delete(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);

            if (user != null) {
                session.remove(user);
            } else {
                throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
            }

            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }
}
