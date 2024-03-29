package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.LittleItemDto;
import ru.practicum.shareit.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    private BookingStatus status;
    private ShortUserDto booker;
    private LittleItemDto item;
}
