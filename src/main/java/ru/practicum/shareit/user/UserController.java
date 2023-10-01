package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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
    public UserDto create(@RequestBody @Valid User user) {
        log.debug("{} create", this.getClass().getName());
        return UserMapper.toUserDto(userService.create(user));
    }

    @GetMapping("/{userId}")
    public UserDto read(@PathVariable long userId) {
        log.debug("{} read({})", this.getClass().getName(), userId);
        return UserMapper.toUserDto(userService.read(userId));
    }

    @GetMapping
    public Collection<UserDto> readAll() {
        log.debug("{} readAll", this.getClass().getName());
        return userService.readAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody User user) {
        log.debug("{} update({})", this.getClass().getName(), userId);
        return UserMapper.toUserDto(userService.update(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.debug("{} delete({})", this.getClass().getName(), userId);
        userService.delete(userId);
    }

}