package tests;

<<<<<<<< HEAD:user-service/src/test/java/UserHibernateTest.java
import dao.UserHibernate;
import entity.User;
========
package tests;

>>>>>>>> 18f6771 (Переход на Spring):src/test/java/tests/UserHibernateTest.java
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

    @Test
    void create_ShouldThrowException_WhenEmailAlreadyExists() {
        User user1 = new User("Василий", "test@example.com", 30);
        userHibernate.create(user1);

        User user2 = new User("Никита", "test@example.com", 25);
        Assertions.assertThrows(RuntimeException.class, () -> userHibernate.create(user2));
    }

    @Test
    void update_ShouldThrowException_WhenEmailAlreadyExists() {
        User user1 = new User("Василий", "test@example.com", 30);
        User user2 = new User("Никита", "example@example.com", 25);
        userHibernate.create(user1);
        userHibernate.create(user2);

        user1.setEmail("example@example.com");
        Assertions.assertThrows(RuntimeException.class, () -> userHibernate.update(user1));
    }
}
