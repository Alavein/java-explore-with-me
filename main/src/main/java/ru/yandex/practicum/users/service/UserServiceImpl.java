package ru.yandex.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exceptions.DataNotFoundException;
import ru.yandex.practicum.users.mapper.UserMapper;
import ru.yandex.practicum.users.model.User;
import ru.yandex.practicum.users.dto.NewUserRequest;
import ru.yandex.practicum.users.dto.UserDto;
import ru.yandex.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Создание пользователя {}", newUserRequest);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserNew(newUserRequest)));
    }

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (ids == null || ids.isEmpty()) {

            return userRepository.findAll(page)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            log.info("Получение пользоватлей с id = {}", ids);
            return userRepository.findAllByIdIn(ids, page)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
        log.info("Удаление пользователя с id = {}", userId);
        userRepository.deleteById(userId);
    }

    public User getUserById(Integer userId) {
        log.info("Получение пользователя с id = {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
    }
}
