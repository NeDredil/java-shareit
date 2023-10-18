package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findRequestsByUserId(Long userId);

    ItemRequestDto findRequestById(Long userId, Long itemId);

    List<ItemRequestDto> findAllRequest(Long userId, int from, int size);

}
