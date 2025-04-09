package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BookmarkService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.SortUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  @PostMapping("/{itemId}")
  public ResponseEntity<Void> bookmarkItem(@PathVariable Long itemId,
      @AuthenticationPrincipal User user) {
    bookmarkService.bookmarkItem(user, itemId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> removeBookmark(@PathVariable Long itemId,
      @AuthenticationPrincipal User user) {
    bookmarkService.removeBookmark(user, itemId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<Page<ItemDto>> getBookmarkedItems(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "bookmarkedAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir
  ) {
    Sort sort = SortUtil.buildSort(sortField,sortDir);

    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Item> pagedItems = bookmarkService.getBookmarkedItems(user, pageable);

    Page<ItemDto> dtoPage = pagedItems.map(item -> {
      ItemDto dto = ItemMapper.toItemDto(item);
      dto.setIsBookmarked(true);
      return dto;
    });

    return ResponseEntity.ok(dtoPage);
  }
}
