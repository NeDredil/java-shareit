package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateUserWhenUserIsCreatedThenReturnUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "Test", "Test@yandex.ru");
        when(userService.createUser(userDto)).thenReturn(UserMapper.toUser(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    public void testFindUserByIdWhenUserIsFoundThenReturnUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "Test", "Test@yandex.ru");
        when(userService.findUserById(1L)).thenReturn(UserMapper.toUser(userDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    public void testFindAllUsersWhenUsersAreFoundThenReturnListOfUserDtos() throws Exception {
        List<User> users = Arrays.asList(
                new User(1L, "Test", "Test@yandex.ru"),
                new User(2L, "Test", "Test@yandex.ru")
        );
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void testUpdateUserWhenUserIsUpdatedThenReturnUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "Test", "Test@yandex.ru");
        when(userService.updateUser(userDto)).thenReturn(UserMapper.toUser(userDto));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    public void testDeleteUserByIdWhenUserIsDeletedThenReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateUserWhenUserDtoIsInvalidThenReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(1L, "", "Test@yandex.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindAllUsersWhenNoUsersAreFoundThenReturnEmptyList() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testDeleteUserByIdWhenUserIsNotFoundThenReturnNotFound() throws Exception {
        doThrow(new NotFoundException("User not found")).when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }
}
