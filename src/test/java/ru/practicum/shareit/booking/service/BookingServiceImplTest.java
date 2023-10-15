package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDateTimeException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingToSave;
    private LocalDateTime time;
    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(2L)
                .name("ownerTest")
                .email("owerTest@yandex.ru")
                .build();

        user = User.builder()
                .id(3L)
                .name("userTest")
                .email("userTest@yandex.ru")
                .build();

        item = Item.builder()
                .id(4L)
                .name("itemNameTest")
                .description("itemDescTest")
                .available(true)
                .owner(owner)
                .build();

        time = LocalDateTime.now();

        booking = Booking.builder()
                .start(time.minusHours(1))
                .end(time.minusMinutes(10))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        bookingToSave = BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(item.getId())
                .booker(UserMapper.toLittleUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void testCreateBookingWhenAllConditionsAreMetThenBookingIsCreated() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actualBooking = bookingService.createBooking(user.getId(), bookingToSave);

        assertEquals(bookingToSave.getStart(), actualBooking.getStart());
        assertEquals(bookingToSave.getEnd(), actualBooking.getEnd());
        assertEquals(bookingToSave.getItemId(), actualBooking.getItemId());
    }

    @Test
    void testCreateBookingWhenStartDateIsAfterEndDateThenThrowIncorrectDateTimeException() {
        bookingToSave.setStart(time.plusHours(2));
        bookingToSave.setEnd(time.plusHours(1));

        assertThrows(IncorrectDateTimeException.class, () -> bookingService.createBooking(user.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBookingWhenItemIsNotAvailableThenThrowNotAvailableException() {
        item.setAvailable(false);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(user.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBookingWhenUserIsNotOwnerOfItemThenThrowNotFoundException() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(owner.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCalculateTotalWithNullInput() {
        assertThrows(NotFoundException.class, () -> bookingService.findBookingForAllOwnerItems(user.getId(), null, from, size));
    }

    @Test
    void testFindAllBookingsForOwner() {
        assertThrows(NotFoundException.class, () -> bookingService.findAllBookingsForOwner(-1, BookingState.ALL, from, size));
    }

    @Test
    void testCalculateTotalWhenInputIsEmptyThenReturnZero() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerId(user.getId(), PageRequest.of(from / size, size, Sort.by("start").descending()))).thenReturn(Collections.emptyList());

        Collection<BookingDto> actualBookings = bookingService.findAllBookingsForOwner(user.getId(), BookingState.ALL, from, size);

        assertEquals(0, actualBookings.size());
    }

    @Test
    void testFindAllWhenStatePastThenReturnBookings() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndEndBefore(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findAllBookingsForOwner(user.getId(), BookingState.PAST, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenItemStatePastThenReturnBookings() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findAllForOwnerPast(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findBookingForAllOwnerItems(user.getId(), BookingState.PAST, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenItemStateCurrentThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllForOwnerCurrent(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findBookingForAllOwnerItems(owner.getId(), BookingState.CURRENT, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenStateCurrentThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                any(), any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findAllBookingsForOwner(owner.getId(), BookingState.CURRENT, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenItemStateFutureThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllForOwnerFuture(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findBookingForAllOwnerItems(owner.getId(), BookingState.FUTURE, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenStateFutureThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartAfter(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findAllBookingsForOwner(owner.getId(), BookingState.FUTURE, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenStateWaitingThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findAllBookingsForOwner(owner.getId(), BookingState.WAITING, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenItemStateWaitingThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllForOwnerState(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findBookingForAllOwnerItems(owner.getId(), BookingState.WAITING, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void testFindAllWhenStateRejectedThenReturnBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> actualBookings = bookingService.findAllBookingsForOwner(owner.getId(), BookingState.REJECTED, from, size);

        assertEquals(1, actualBookings.size());
    }

    @Test
    void createWhenNotValidTimeThenIncorrectDateTimeExceptionThrow() {
        bookingToSave.setStart(time.plusHours(2));
        bookingToSave.setEnd(time.plusHours(1));

        assertThrows(IncorrectDateTimeException.class, () -> bookingService.createBooking(user.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createWhenItemNotAvailableThenNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(user.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createWhenBookerIsTheItemOwnerThenNotFoundExceptionThrow() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(owner.getId(), bookingToSave));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void readWhenUserNotOwnerOrNotBookerThenNotOwnerExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class, () -> bookingService.findBooking(999L, booking.getId()));
    }

    @Test
    void updateStatusWhenInvokeThenReturnUpdatedBooking() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.updateStatusBooking(owner.getId(), booking.getId(), true);

        verify(bookingRepository).save(booking);
    }

    @Test
    void updateStatusWhenStatusIsAlreadyApproveThenNotAvailableExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class,
                () -> bookingService.updateStatusBooking(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateStatusWhenUserNotExistThenNotFoundExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.updateStatusBooking(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateStatusWhenUserWasNotOwnerThenNotOwnerExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class,
                () -> bookingService.updateStatusBooking(user.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void deleteWhenUserWasNotOwnerThenNotOwnerExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class,
                () -> bookingService.deleteBookingById(owner.getId(), booking.getId()));

        verify(bookingRepository, never()).deleteById(booking.getId());
    }

    @Test
    void deleteWhenInvokeThenDeleteBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.deleteBookingById(user.getId(), booking.getId());

        verify(bookingRepository).deleteById(booking.getId());
    }
}