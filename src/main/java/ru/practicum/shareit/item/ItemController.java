package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Valid ItemDto itemDto) {
        log.debug("{} create", this.getClass().getName());
        return ItemMapper.toItemDtoForOwner(itemService.create(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable long itemId) {
        log.debug("{} read", this.getClass().getName());
        return ItemMapper.toItemDto(itemService.read(userId, itemId));
    }

    @GetMapping
    public Collection<ItemDto> readAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("{} readAll", this.getClass().getName());
        return itemService.readAll(userId).stream().map(ItemMapper::toItemDtoForOwner).collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long itemId,
                          @RequestBody Item item) {
        log.debug("{} update", this.getClass().getName());
        return ItemMapper.toItemDto(itemService.update(userId, itemId, item));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long itemId) {
        log.debug("{} delete({})", this.getClass().getName(), itemId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.debug("{} search({})", this.getClass().getName(), text);
        return itemService.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

}