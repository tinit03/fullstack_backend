package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * REST controller for managing bookmark operations for items.
 * Provides endpoints for bookmarking an item, removing a bookmark, and
 * retrieving paginated bookmarked items for the authenticated user.
 */
@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  /**
   * Bookmarks the specified item for the authenticated user.
   *
   * @param itemId the ID of the item to bookmark
   * @param user the authenticated user performing the action
   * @return a ResponseEntity with an OK status if the item was bookmarked successfully
   */
  @Operation(summary = "Bookmark Item",
      description = "Adds the specified item to the authenticated user's bookmarks.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Item bookmarked successfully")
  })
  @PostMapping("/{itemId}")
  public ResponseEntity<Void> bookmarkItem(@PathVariable Long itemId,
      @AuthenticationPrincipal User user) {
    bookmarkService.bookmarkItem(user, itemId);
    return ResponseEntity.ok().build();
  }

  /**
   * Removes the bookmark for the specified item for the authenticated user.
   *
   * @param itemId the ID of the item whose bookmark is to be removed
   * @param user the authenticated user performing the action
   * @return a ResponseEntity with a no-content status if the bookmark was removed successfully
   */
  @Operation(summary = "Remove Bookmark",
      description = "Removes the bookmark of the specified item for the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Bookmark removed successfully")
  })
  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> removeBookmark(@PathVariable Long itemId,
      @AuthenticationPrincipal User user) {
    bookmarkService.removeBookmark(user, itemId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves a paginated list of items bookmarked by the authenticated user.
   *
   * @param user the authenticated user
   * @param page the page number to retrieve (default is 0)
   * @param size the number of items per page (default is 10)
   * @param sortField the field to sort by (default is "bookmarkedAt")
   * @param sortDir the direction of sorting; "asc" for ascending or "desc" for descending (default is "desc")
   * @return a ResponseEntity containing a page of ItemDto objects with the bookmark flag set to true
   */
  @Operation(summary = "Get Bookmarked Items",
      description = "Retrieves a paginated list of items bookmarked by the authenticated user, "
          + "sorted by the specified field and direction.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bookmarked items retrieved successfully")
  })
  @GetMapping("/me")
  public ResponseEntity<Page<ItemDto>> getBookmarkedItems(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "bookmarkedAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir
  ) {
    Sort sort = SortUtil.buildSort(sortField, sortDir);
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
