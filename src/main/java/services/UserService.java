package services;

import dao.UserRepository;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long create (String name, String email, Integer age) {
        User user = new User(name, email, age);
        userRepository.create(user);
        logger.info("Создан пользователь с ID: {}", user.getId());
        return user.getId();
    }

    public Optional<User> get (Long id) {
        return userRepository.select(id);
    }

    public void update (User user, String name, String email, String ageStr) {
        if (!name.isBlank()) user.setName(name);
        if (!email.isBlank()) user.setEmail(email);
        if (!ageStr.isBlank()) {
            int age = Integer.parseInt(ageStr);
            if (age > 0) user.setAge(age);
        }

        userRepository.update (user);
        logger.info("Обновлён пользователь с ID: {}", user.getId());
    }

    public void delete (Long id) {
        userRepository.delete(id);
        logger.info("Пользователь с ID: {} удалён", id);
    }
}
