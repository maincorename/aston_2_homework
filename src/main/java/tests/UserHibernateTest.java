package tests;

import dao.UserHibernate;
import entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class UserHibernateTest extends BaseDaoTest {

    @BeforeEach
    void initDao() {
        userHibernate = new UserHibernate(sessionFactory);

        //очистка таблицы перед каждым тестом
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createNativeMutationQuery("DELETE FROM users").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void create_ShouldCreateUserAndGenerateId() {
        User user = new User("Василий", "test@example.com", 30);
        userHibernate.create(user);

        Assertions.assertNotNull(user.getId());
        Assertions.assertTrue(user.getId() > 0);
    }

    @Test
    void select_ShouldReturnUserWhenExists() {
        User user = new User("Василий", "test@example.com", 30);
        userHibernate.create(user);
        Optional<User> foundedUser = userHibernate.select(user.getId());

        Assertions.assertTrue(foundedUser.isPresent());
        Assertions.assertEquals("Василий", foundedUser.get().getName());
    }

    @Test
    void save_ShouldChangeUserData() {
        User user = new User("Василий", "test@example.com", 30);
        userHibernate.create(user);
        user.setAge(35);
        userHibernate.update(user);
        User updatedUser = userHibernate.select(user.getId()).orElseThrow();

        Assertions.assertEquals(35, updatedUser.getAge());
    }

    @Test
    void delete_ShouldRemoveUser() {
        User user = new User("Василий", "test@example.com", 30);
        userHibernate.create(user);
        userHibernate.delete(user.getId());

        Assertions.assertFalse(userHibernate.select(user.getId()).isPresent());
    }
}
