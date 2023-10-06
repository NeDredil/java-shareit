package ru.practicum.shareit.exception;

public class IncorrectDateTimeException extends RuntimeException {

    public IncorrectDateTimeException() {
    }

    public IncorrectDateTimeException(String message) {
        super(message);
    }

}
