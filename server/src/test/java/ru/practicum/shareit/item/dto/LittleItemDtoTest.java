package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LittleItemDtoTest {

    @Test
    public void testLittleItemDto() {
        // Arrange
        long id = 1L;
        String name = "Test Item";

        // Act
        LittleItemDto littleItemDto = new LittleItemDto();
        littleItemDto.setId(id);
        littleItemDto.setName(name);

        // Assert
        Assertions.assertEquals(id, littleItemDto.getId());
        Assertions.assertEquals(name, littleItemDto.getName());
    }
}