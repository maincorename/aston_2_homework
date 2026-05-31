package tests;

import dao.UserRepository;
import entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.UserService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);

        //когда создаётся пользователь устанавливаем id и время создания
        lenient().doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            setPrivateField(user, "id", 1L);
            setPrivateField(user, "created_at", LocalDateTime.now());
            return null;
        }).when(userRepository).create(any(User.class));
    }

    //имитируем установку значения приватного поля рефлексией
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = User.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Не получилось установить значение для поля " + fieldName, e);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Василий, test@example.com, 30",
    })
    void create_ShouldCallRepositoryCreateAndReturnId(String name, String email, int age) {
        Long id = userService.create(name, email, age);

        Assertions.assertEquals(1L, id);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).create(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        Assertions.assertEquals(name, capturedUser.getName());
        Assertions.assertEquals(email, capturedUser.getEmail());
        Assertions.assertEquals(age, capturedUser.getAge());
        Assertions.assertNotNull(capturedUser.getCreated_at());
    }

    @ParameterizedTest
    @CsvSource({
            "1, Василий, test@example.com, 30",
    })
    void select_ShouldReturnUserFromRepository(Long id, String expectedName, String expectedEmail, int expectedAge) {
        User existingUser = new User(expectedName, expectedEmail, expectedAge);
        setPrivateField(existingUser, "id", id);
        when(userRepository.select(id)).thenReturn(Optional.of(existingUser));

        Optional<User> result = userService.get(id);

        User user = result.get();
        Assertions.assertEquals(expectedName, user.getName());
        Assertions.assertEquals(expectedEmail, user.getEmail());
        Assertions.assertEquals(expectedAge, user.getAge());
        Assertions.assertEquals(id, user.getId());
    }

    @ParameterizedTest
    @CsvSource({
            // oldName, oldEmail, oldAge, newName, newEmail, newAgeStr, expName, expEmail, expAge
            "Василий, test@example.com, 30, Екатерина, newtest@example.com, 25, Екатерина, newtest@example.com, 25",
    })
    void update_ShouldModifyUserFields(
            String oldName, String oldEmail, int oldAge,
            String newName, String newEmail, String newAgeStr,
            String expectedName, String expectedEmail, int expectedAge) {

        User user = new User(oldName, oldEmail, oldAge);
        setPrivateField(user, "id", 10L);

        userService.update(user, newName, newEmail, newAgeStr);

        Assertions.assertEquals(expectedName, user.getName());
        Assertions.assertEquals(expectedEmail, user.getEmail());
        Assertions.assertEquals(expectedAge, user.getAge());
        verify(userRepository).update(user);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, })
    void delete_ShouldCallRepositoryDelete(Long id) {
        userService.delete(id);
        verify(userRepository).delete(id);
    }
}
