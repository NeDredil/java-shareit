package ru.practicum.shareit.exception;

public class BookingTimeUnavailableException extends RuntimeException {

    public BookingTimeUnavailableException(String massage) {
        super(massage);
    }
}
