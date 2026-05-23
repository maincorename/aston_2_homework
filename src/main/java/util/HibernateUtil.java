package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Загрузка конфигурации из hibernate.cfg
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.out.println("Произошла ошибка при создании SessionFactory" + e);
            // Бросаем исключение для трассировки ошибки
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
