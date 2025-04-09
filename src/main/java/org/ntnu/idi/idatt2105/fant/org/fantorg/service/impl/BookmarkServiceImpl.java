package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item.ItemNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bookmark;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BookmarkRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  private final BookmarkRepository bookmarkRepository;

  @Override
  public void bookmarkItem(User user, Long itemId) {

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ItemNotFoundException(itemId));
    if(Objects.equals(item.getSeller().getId(), user.getId())){
      throw new IllegalArgumentException("The user shouldn't be able to bookmark their own item!");
    }
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(()-> new UserNotFoundException(itemId));
    if(bookmarkRepository.existsByUserAndItem(managedUser,item)){
      throw new IllegalArgumentException("The item is already bookmarked!");
    }
    Bookmark bookmark = Bookmark.builder()
        .user(managedUser)
        .item(item)
        .bookmarkedAt(LocalDateTime.now())
        .build();

    bookmarkRepository.save(bookmark);
  }

  @Override
  @Transactional
  public void removeBookmark(User user, Long itemId) {
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFoundException(user.getId()));

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ItemNotFoundException(itemId));
   bookmarkRepository.deleteByUserAndItem(managedUser,item);
  }

  @Override
  @Transactional
  public boolean isBookmarked(User user, Long itemId) {
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFoundException(user.getId()));
    Item item = itemRepository.findById(itemId)
            .orElseThrow(()-> new ItemNotFoundException(itemId));
    return bookmarkRepository.existsByUserAndItem(managedUser,item);
  }
  @Override
  public Page<Item> getBookmarkedItems(User user, Pageable pageable) {
    Page<Bookmark> activeBookmarks = bookmarkRepository.findByUserAndItem_Status(user, Status.ACTIVE, pageable);
    return activeBookmarks.map(Bookmark::getItem);
  }
}
