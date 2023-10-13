package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Valid
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID) Long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.debug("поступил запрос на добавление вещи:" + itemDto + " пользователем c id: {} ", userId);
        return ItemMapper.toItemDto(itemService.createItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(SHARER_USER_ID) Long userId,
                                @PathVariable long itemId) {
        log.debug("поступил запрос на просмотр вещи по идентификатору id: {}  ", itemId);
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> findAllItemsByUserId(@RequestHeader(SHARER_USER_ID) Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("поступил запрос на просмотр владельцем всех своих вещей, id: {} ", userId);
        return itemService.findAllItemsByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) Long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.debug("поступил запрос на редактирование вещи: {} владельцем c id: {} ", itemDto, userId);
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@RequestHeader(SHARER_USER_ID) Long userId,
                               @PathVariable long itemId) {
        log.debug("поступил запрос на удаление вещи c id: {} ", itemId);
        itemService.deleteItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("поступил запрос: {}, на просмотр доступной для аренды вещи", text);
        return itemService.getItemsBySearchQuery(text, from, size).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(SHARER_USER_ID) Long userId,
                                    @PathVariable long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        log.debug("поступил запрос {} от пользователя с id: {}, " +
                "на создание комментария с id {}", commentDto, userId, itemId);
        return CommentMapper.toCommentDto(itemService.createComment(userId, itemId, commentDto));
    }

}