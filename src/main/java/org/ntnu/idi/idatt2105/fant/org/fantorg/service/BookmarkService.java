package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for managing bookmarks on items. */
public interface BookmarkService {

  /**
   * Adds a bookmark for a specific item by the given user.
   *
   * @param user The user bookmarking the item
   * @param itemId The ID of the item to bookmark
   */
  void bookmarkItem(User user, Long itemId);

  /**
   * Removes a bookmark for a specific item by the given user.
   *
   * @param user The user removing the bookmark
   * @param itemId The ID of the item to unbookmark
   */
  void removeBookmark(User user, Long itemId);

  /**
   * Checks if the user has bookmarked a specific item.
   *
   * @param user The user
   * @param itemId The ID of the item
   * @return {@code true} if the item is bookmarked by the user, otherwise {@code false}
   */
  boolean isBookmarked(User user, Long itemId);

  /**
   * Retrieves a paginated list of items bookmarked by the user.
   *
   * @param user The user whose bookmarks to retrieve
   * @param pageable The pagination information
   * @return A page of {@link Item} objects
   */
  Page<Item> getBookmarkedItems(User user, Pageable pageable);
}
