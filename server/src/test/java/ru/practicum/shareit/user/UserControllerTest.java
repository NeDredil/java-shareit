package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void findAllUsersTest() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    public void createUserTest() throws Exception {
        long userId = 1L;
        UserDto userDto = createTestUserDto(userId);

        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)));

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    public void findUserByIdTest() throws Exception {
        long userId = 1L;
        UserDto userDto = createTestUserDto(userId);

        when(userService.findUserById(userId))
                .thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)));

        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    public void updateUserTest() throws Exception {
        long userId = 1L;
        UserDto userDto = createTestUserDto(userId);
        userDto.setName("updatedName");

        when(userService.updateUser(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)));

        verify(userService, times(1)).updateUser(any(UserDto.class));
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(any(Long.class));
    }

    private UserDto createTestUserDto(Long id) {
        String name = "user";
        String email = "user@user.com";

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private String toJson(UserDto dto) {
        return String.format("{\"id\":%d,\"name\": %s,\"email\": %s}", dto.getId(), dto.getName(), dto.getEmail());
    }
}