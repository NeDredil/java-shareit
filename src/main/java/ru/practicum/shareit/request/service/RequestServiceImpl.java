package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.request.mapper.RequestMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.Constant.*;
import static ru.practicum.shareit.exception.Constant.NOT_FOUND_ITEM_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId))));
        itemRequest.setCreated(LocalDateTime.now());
        return RequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findRequestsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorId(userId);

        Map<Long, List<Item>> collectItem = itemRepository.findAllByRequestRequestorId(userId)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests.stream()
                .map(itemRequest -> RequestMapper.toRequestDto(itemRequest,
                        collectItem.getOrDefault(itemRequest.getRequestor().getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM_REQUEST, requestId)));
        Collection<Item> allByRequestId = itemRepository.findAllByRequestId(itemRequest.getId());
        return RequestMapper.toRequestDto(itemRequest, allByRequestId);
    }

    @Override
    public List<ItemRequestDto> findAllRequest(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        PageRequest page = PageRequest.of(from, size, Sort.by("created").ascending());
        return requestRepository.findAllByRequestorIdIsNot(userId, page).stream()
                .map(RequestMapper::toRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(
                        itemRepository.findAllByRequestId(itemRequestDto.getId())
                                .stream().map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getItemRequestById(Long requestId) {
        return RequestMapper.toRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM_REQUEST, requestId))));
    }

}
