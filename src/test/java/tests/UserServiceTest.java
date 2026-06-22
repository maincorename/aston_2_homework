package tests;

import application.dao.UserSpringRepository;
import application.dto.UserDto;
import application.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import application.services.UserService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserSpringRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);

        //когда создаётся пользователь устанавливаем id и время создания
        lenient().doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                setPrivateField(user, "id", 1L);
            }
            if (user.getCreated_at() == null) {
                setPrivateField(user, "created_at", LocalDateTime.now());
            }
            return user;
        }).when(userRepository).save(any(User.class));
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
    void create_ShouldReturnUserDto(String name, String email, int age) {
        UserDto result = userService.create(name, email, age);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(name, result.getName());
        Assertions.assertEquals(email, result.getEmail());
        Assertions.assertEquals(age, result.getAge());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
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
    void select_ShouldReturnUserDtoFromRepository(Long id, String expectedName, String expectedEmail, int expectedAge) {
        User existingUser = new User(expectedName, expectedEmail, expectedAge);
        setPrivateField(existingUser, "id", id);
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        UserDto user = userService.getById(id);

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
            String newName, String newEmail, int newAge,
            String expectedName, String expectedEmail, int expectedAge) {

        Long userId = 1L;
        User existingUser = new User(oldName, oldEmail, oldAge);
        setPrivateField(existingUser, "id", userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        //имитируем возвращение объекта
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(userId, newName, newEmail, newAge);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getId());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedEmail, result.getEmail());
        Assertions.assertEquals(expectedAge, result.getAge());
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, })
    void delete_ShouldCallRepositoryDelete(Long id) {
        when(userRepository.existsById(id)).thenReturn(true);
        userService.delete(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void create_ShouldThrowExceptionFromRepository() {
        doThrow(new RuntimeException("Ошибка при создании пользователя"))
                .when(userRepository).save(any(User.class));

        Assertions.assertThrows(RuntimeException.class, () -> userService.create("Василий", "test@example.com", 30));
    }

    @Test
    void getAll_ShouldReturnListOfUserDto() {
        User user1 = new User("Василий", "test@example.com", 30);
        User user2 = new User("Мария", "maria@example.com", 25);
        setPrivateField(user1, "id", 1L);
        setPrivateField(user2, "id", 2L);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Василий", result.get(0).getName());
        Assertions.assertEquals("Мария", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    void update_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> userService.update(99L, "Name", "email@test.com", 25));
        Assertions.assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> userService.delete(99L));
        Assertions.assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> userService.getById(99L));
        Assertions.assertTrue(exception.getMessage().contains("не найден"));
    }
}
