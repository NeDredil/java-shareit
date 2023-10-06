package ru.practicum.shareit.exception;

public class UncompletedBookingException extends RuntimeException {

    public UncompletedBookingException() {
    }

    public UncompletedBookingException(String message) {
        super(message);
    }

}
