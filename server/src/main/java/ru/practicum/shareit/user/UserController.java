package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.debug("поступил запрос на создание пользователя");
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) {
        log.debug("поступил запрос на получение данных пользователя c id: {} ", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.debug("поступил запрос на получение данных всех пользователей");
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.debug("поступил запрос на изменение данных пользователя c id: {} ", userId);
        userDto.setId(userId);
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable long userId) {
        log.debug("поступил запрос на удаление данных пользователя c id: {} ", userId);
        userService.deleteUserById(userId);
    }

}