package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemStatusUpdate;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
public class ItemController {
  private final ItemService itemService;

  @Autowired
  public ItemController(ItemService itemService){
    this.itemService = itemService;
  }
  /**
   * GET /items/search
   * Endpoint to search for items using a keyword.
   * Accepts optional keyword, page, and size parameters.
   */
  @GetMapping("/search")
  public Page<ItemDto> searchItems(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
    Page<Item> itemsPage = itemService.searchItems(keyword, pageable);

    return itemsPage.map(ItemMapper::toItemDto);
  }

  /**
   * GET /items
   * Returns all items with pagination.
   */
  @GetMapping
  public Page<ItemDto> getAllItems(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
    Page<Item> itemsPage = itemService.getAllItems(pageable);
    return itemsPage.map(ItemMapper::toItemDto);
  }

  /**
   * GET /items/{id}
   * Returns detailed information for a single item.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ItemDto> getItemDetail(@PathVariable Long id) {
    Item item = itemService.getItemById(id);
    ItemDto detailDto = ItemMapper.toItemDto(item);
    return ResponseEntity.ok(detailDto);
  }

  @PostMapping
  public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemCreateDto dto, @AuthenticationPrincipal User user) {
    Item saved = itemService.createItem(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(ItemMapper.toItemDto(saved));
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> deleteItem(
      @PathVariable Long itemId,
      @AuthenticationPrincipal User user) {

    itemService.deleteItem(itemId, user);
    return ResponseEntity.noContent().build();
  }


  @PutMapping("/{itemId}")
  public ResponseEntity<ItemDto> updateItem(
      @PathVariable Long itemId,
      @Valid @RequestBody ItemEditDto dto,
      @AuthenticationPrincipal User user) {
    Item updated = itemService.updateItem(itemId, dto, user);
    ItemDto updatedDto = ItemMapper.toItemDto(updated);
    return ResponseEntity.ok(updatedDto);
  }

  @PutMapping("/{itemId}/status")
  public ResponseEntity<ItemDto> updateStatus(
      @PathVariable Long itemId,
      @Valid @RequestBody ItemStatusUpdate status,
      @AuthenticationPrincipal User user) {
    Item updated = itemService.changeStatus(itemId,status.getStatus(),user);
    ItemDto updatedDto = ItemMapper.toItemDto(updated);
    return ResponseEntity.ok(updatedDto);
  }
}
