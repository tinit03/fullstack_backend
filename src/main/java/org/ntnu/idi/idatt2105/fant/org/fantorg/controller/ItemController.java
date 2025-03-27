package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
   * GET /items/{id}
   * Returns detailed information for a single item.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ItemDto> getItemDetail(@PathVariable Long id) {
    Optional<Item> optionalItem = itemService.getItemById(id);
    if (optionalItem.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ItemDto detailDto = ItemMapper.toItemDto(optionalItem.get());
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
      @Valid @RequestBody ItemCreateDto dto,
      @AuthenticationPrincipal User user) {

    Item updated = itemService.updateItem(itemId, dto, user);
    return ResponseEntity.ok(ItemMapper.toItemDto(updated));
  }
}
