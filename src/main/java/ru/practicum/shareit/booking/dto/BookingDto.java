package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.LittleItemDto;
import ru.practicum.shareit.user.dto.ShortUserDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    private long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    @NotNull
    private Long itemId;

    private BookingStatus status;
    private ShortUserDto booker;
    private LittleItemDto item;
}
