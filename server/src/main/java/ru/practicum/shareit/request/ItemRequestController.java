package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping()
    public ItemRequestDto createRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("поступил запрос на добавление запроса вещи: {} пользователя с id: {}.", itemRequestDto, userId);
        return requestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public Collection<ItemRequestDto> findRequestsById(@RequestHeader(SHARER_USER_ID) Long userId) {
        log.debug("поступил запрос на получение списка запросов вещи пользователя с id: {} .", userId);
        return requestService.findRequestsByUserId(userId);
    }

    @GetMapping("{itemId}")
    public ItemRequestDto findRequestById(@RequestHeader(SHARER_USER_ID) Long userId,
                                          @PathVariable Long itemId) {
        log.debug("поступил запрос на получение запроса пользователя с id: {}", userId);
        return requestService.findRequestById(userId, itemId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.debug("поступил запрос на получение списка всех запросов вещей, от пользователя {}.", userId);
        return requestService.findAllRequest(userId, from, size);
    }

}
