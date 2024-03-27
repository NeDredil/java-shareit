package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortBookingDto {

    private long id;
    private long bookerId;

}
