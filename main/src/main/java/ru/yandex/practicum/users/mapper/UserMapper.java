package ru.yandex.practicum.users.mapper;

import ru.yandex.practicum.users.dto.NewUserRequest;
import ru.yandex.practicum.users.model.User;
import ru.yandex.practicum.users.dto.UserDto;

public class UserMapper {

    public static User toUserNew(NewUserRequest userDto) {
        return User
                .builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}
