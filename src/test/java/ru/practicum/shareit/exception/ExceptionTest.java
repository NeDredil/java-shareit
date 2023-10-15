package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExceptionTest {

    @Test
    public void testException() {
        String message = "Booking time is unavailable";
        BookingTimeUnavailableException exception = new BookingTimeUnavailableException(message);
        assertEquals(message, exception.getMessage());
        String messageUBE = "Booking is not completed";
        UncompletedBookingException exceptionUBE = new UncompletedBookingException(messageUBE);
        assertEquals(messageUBE, exceptionUBE.getMessage());
    }


}