package ru.practicum.shareit.exeption;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BookingTimeUnavailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTimeUnavailableExceptionTest {

    @Test
    public void testConstructor() {
        String message = "Booking time is unavailable";
        BookingTimeUnavailableException exception = new BookingTimeUnavailableException(message);
        assertEquals(message, exception.getMessage());
    }
}
