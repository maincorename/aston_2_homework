package application.mappers;

import application.dto.UserDto;
import application.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        return new User(dto.name(), dto.email(), dto.age());
    }

    public static UserDto toDto (User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }

    public static List<UserDto> toDtoList (List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
