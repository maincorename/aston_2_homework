package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory buildSessionFactory() {
        try {
            // Загрузка конфигурации из hibernate.cfg
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            logger.error("Произошла ошибка при создании SessionFactory. ", e);

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
