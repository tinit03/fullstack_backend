package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.Set;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkService {
  void bookmarkItem(User user, Long itemId);
  void removeBookmark(User user, Long itemId);
  boolean isBookmarked(User user, Long itemId);
  Page<Item> getBookmarkedItems(User user, Pageable pageable);
}
