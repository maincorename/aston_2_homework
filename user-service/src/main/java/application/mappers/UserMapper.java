package application.mappers;

import application.dto.UserDto;
import application.entity.User;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        return new User(dto.getName(), dto.getEmail(), dto.getAge());
    }
}
