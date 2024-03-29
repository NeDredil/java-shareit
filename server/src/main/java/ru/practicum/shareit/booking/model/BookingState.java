package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.NotAvailableException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState toBookingState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new NotAvailableException("Unknown state: " + state);
        }
    }
}
