package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest createRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findRequestsById(Long userId);

    ItemRequestDto findRequestById(Long userId, Long itemId);

    List<ItemRequestDto> findAllRequest(Long userId, int from, int size);

}
