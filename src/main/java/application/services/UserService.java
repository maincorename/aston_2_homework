package application.services;

import application.dao.UserSpringRepository;
import application.dto.UserDto;
import application.entity.User;
import application.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserSpringRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USER_NOT_FOUND_MSG = "Пользователь с ID %d не найден";

    @Autowired
    public UserService (UserSpringRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto create (String name, String email, Integer age) {
        User user = new User(name, email, age);
        user = userRepository.save(user);
        logger.info("Создан пользователь с ID: {}", user.getId());
        return UserMapper.toDto(user);
    }

    public UserDto getById (Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(USER_NOT_FOUND_MSG, id)));
        return UserMapper.toDto(user);
    }

    public UserDto update (Long id, String name, String email, Integer age) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(USER_NOT_FOUND_MSG, id)));

        if (name != null && !name.isBlank()) user.setName(name);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (age != null && age > 0) user.setAge(age);

        user = userRepository.save(user);
        logger.info("Обновлён пользователь с ID: {}", user.getId());

        return UserMapper.toDto(user);
    }

    public void delete (Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException(String.format(USER_NOT_FOUND_MSG, id));
        }

        userRepository.deleteById(id);
        logger.info("Пользователь с ID: {} удалён", id);
    }

    public List<UserDto> getAll () {
        return UserMapper.toDtoList(userRepository.findAll());
    }
}
