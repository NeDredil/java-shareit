package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotAvailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingStateTest {
    @Autowired
    private BookingState bookingState;

    @Test
    void toBookingStateWhenInvokeThenReturnBookingState() {
        BookingState result = BookingState.toBookingState("ALL");

        assertEquals(BookingState.ALL, result);
    }

    @Test
    void toBookingStateWhenNoValidStateThenThrowException() {
        assertThrows(NotAvailableException.class, () -> BookingState.toBookingState("INCORRECT"));
    }

}

