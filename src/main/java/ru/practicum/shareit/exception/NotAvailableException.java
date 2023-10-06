package ru.practicum.shareit.exception;

public class NotAvailableException extends RuntimeException {

    public NotAvailableException() {
    }

    public NotAvailableException(String message) {
        super(message);
    }

}
