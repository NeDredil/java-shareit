package ru.practicum.shareit.exeption;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UncompletedBookingException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    @Test
    public void handleNotOwnerExceptionShouldReturnErrorResponseWithNotFoundStatus() {
        String errorMessage = "Not owner";
        NotOwnerException exception = new NotOwnerException(errorMessage);
        ErrorHandler errorHandler = new ErrorHandler();

        ErrorResponse response = errorHandler.handleNotOwnerException(exception);

        assertEquals(errorMessage, response.getError());
    }

    @Test
    public void handleNotFoundExceptionShouldReturnErrorResponseWithNotFoundStatus() {
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);
        ErrorHandler errorHandler = new ErrorHandler();


        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertEquals(errorMessage, response.getError());
    }



    @Test
    public void handleUncompletedBookingExceptionShouldReturnErrorResponseWithBadRequestStatus() {
        UncompletedBookingException exception = new UncompletedBookingException("Uncompleted booking");
        ErrorHandler errorHandler = new ErrorHandler();

        ErrorResponse response = errorHandler.handleUncompletedBookingException(exception);

        assertEquals("Нельзя создать отзыв для незавершенного бронирования.", response.getError());
    }

    @Test
    public void handleDataIntegrityViolationExceptionShouldReturnErrorResponseWithConflictStatus() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");
        ErrorHandler errorHandler = new ErrorHandler();

        ErrorResponse response = errorHandler.handleDataIntegrityViolationException(exception);

        assertEquals("DataIntegrityViolationException Duplicate", response.getError());
    }

    @Test
    public void handleNotAvailableExceptionShouldReturnErrorResponseWithBadRequestStatus() {
        String errorMessage = "Not available";
        NotAvailableException exception = new NotAvailableException(errorMessage);
        ErrorHandler errorHandler = new ErrorHandler();

        ErrorResponse response = errorHandler.handleNotAvailableException(exception);

        assertEquals(errorMessage, response.getError());
    }
}