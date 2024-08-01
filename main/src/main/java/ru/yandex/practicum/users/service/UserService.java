package ru.yandex.practicum.users.service;

import ru.yandex.practicum.users.dto.NewUserRequest;
import ru.yandex.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    void deleteUserById(Integer userId);
}
