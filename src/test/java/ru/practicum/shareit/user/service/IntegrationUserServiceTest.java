package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    public void testSaveUser() {
        UserDto userDto = generateUserDto();
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        service.deleteUserById(user.getId());
    }

    @Test
    public void testFindUserById() {
        UserDto savedUser = service.createUser(generateUserDto());
        UserDto userDto = service.findUserById(savedUser.getId());
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo("Test"));
        assertThat(userDto.getEmail(), equalTo("test@yandex.ru"));
        service.deleteUserById(userDto.getId());
    }

    private UserDto generateUserDto() {
        UserDto dto = new UserDto();
        dto.setName("Test");
        dto.setEmail("test@yandex.ru");
        return dto;
    }
}