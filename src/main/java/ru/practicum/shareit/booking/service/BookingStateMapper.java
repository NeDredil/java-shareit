package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotAvailableException;

public class BookingStateMapper {

    private BookingStateMapper() {
    }

    public static BookingState toBookingState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new NotAvailableException("Unknown state: " + state);
        }
    }

}
