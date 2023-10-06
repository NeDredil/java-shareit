package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.debug("поступил запрос на добавление вещи:" + itemDto + " пользователем c id: {} ", userId);
        return ItemMapper.toItemDto(itemService.createItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable long itemId) {
        log.debug("поступил запрос на просмотр вещи по идентификатору id: {}  ", itemId);
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> findAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("поступил запрос на просмотр владельцем всех своих вещей, id: {} ", userId);
        return itemService.findAllItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.debug("поступил запрос на редактирование вещи: {} владельцем c id: {} ", itemDto, userId);
            itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable long itemId) {
        log.debug("поступил запрос на удаление вещи c id: {} ", itemId);
        itemService.deleteItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.debug("поступил запрос на просмотр доступной для аренды вещи", text);
        return itemService.getItemsBySearchQuery(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        log.debug("поступил запрос на создание комментария", itemId);
        return CommentMapper.toCommentDto(itemService.createComment(userId, itemId, commentDto));
    }

}