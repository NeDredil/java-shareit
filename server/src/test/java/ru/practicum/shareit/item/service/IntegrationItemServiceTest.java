package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemServiceTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRepository itemRepository;

    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    public void beforeEach() {
        userDto = generateUserDto("userTest@yandex.ru", "userTest");
        itemDto = generateItemDto();
        userDto = userService.createUser(userDto);
        itemDto = itemService.createItem(userDto.getId(), itemDto);
    }

    @AfterEach
    public void afterEach() {
        userService.deleteUserById(userDto.getId());
        itemRepository.deleteById(itemDto.getId());
    }

    @Test
    public void saveTest() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), (itemDto.getDescription()));
    }

    @Test
    public void findItemByIdTest() {
        UserDto savedOwnerDto = userService.createUser(generateUserDto("userTest", "userTest@yandex.ru"));
        ItemDto savedItemDto = itemService.createItem(savedOwnerDto.getId(), itemDto);
        ItemDto newItemDto = itemService.findItemById(savedOwnerDto.getId(), savedItemDto.getId());

        assertNotNull(newItemDto);
        assertEquals(savedItemDto.getName(), newItemDto.getName());
        assertEquals(savedItemDto.getDescription(), newItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), newItemDto.getAvailable());
    }


    private ItemDto generateItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test");
        itemDto.setDescription("testDesc");
        itemDto.setAvailable(true);
        return itemDto;
    }

    private UserDto generateUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}