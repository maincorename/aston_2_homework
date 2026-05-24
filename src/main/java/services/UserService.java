package services;

import dao.UserHibernate;
import dao.UserRepository;
import entity.User;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository = new UserHibernate();

    public Long createUser(String name, String email, Integer age) {
        User user = new User(name, email, age);
        userRepository.userCreate(user);
        return user.getId();
    }

    public Optional<User> getUser(Long id) {
        return userRepository.userSelect(id);
    }

    public void updateUser (User user, String name, String email, String ageStr) {
        if (!name.isBlank()) user.setName(name);
        if (!email.isBlank()) user.setEmail(email);
        if (!ageStr.isBlank()) {
            int age = Integer.parseInt(ageStr);
            if (age > 0) user.setAge(age);
        }

        userRepository.userUpdate(user);
    }

    public void deleteUser (Long id) {
        userRepository.userDelete(id);
    }
}
