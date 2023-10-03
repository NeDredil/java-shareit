package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.debug("поступил запрос на создание пользователя");
        return UserMapper.toUserDto(userService.createUser(userDto));
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) {
        log.debug("поступил запрос на получение данных пользователя c id: ", userId);
        return UserMapper.toUserDto(userService.findUserById(userId));
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.debug("поступил запрос на получение данных всех пользователей");
        return userService.findAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.debug("поступил запрос на изменение данных пользователя c id: ", userId);
        return UserMapper.toUserDto(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.debug("поступил запрос на удаление данных пользователя c id: ", userId);
        userService.deleteUserById(userId);
    }

}