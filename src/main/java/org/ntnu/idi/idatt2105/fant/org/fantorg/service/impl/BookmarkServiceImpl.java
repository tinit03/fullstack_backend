package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item.ItemNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BookmarkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;


  @Override
  @Transactional
  public void bookmarkItem(User user, Long itemId) {
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(()-> new UserNotFoundException(itemId));
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ItemNotFoundException(itemId));
    if(Objects.equals(item.getSeller().getId(), user.getId())){
      throw new IllegalArgumentException("The user shouldn't be able to bookmark their own item!");
    }
    if(managedUser.getBookmarkedItems().contains(itemId)){
      throw new IllegalArgumentException("The item is already bookmarked!");
    }
    managedUser.getBookmarkedItems().add(item);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void removeBookmark(User user, Long itemId) {
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFoundException(user.getId()));

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ItemNotFoundException(itemId));
    if(!user.getBookmarkedItems().contains(item)){
      throw new IllegalArgumentException("You cannot remove bookmark for items that are not bookmarked!");
    }
    managedUser.getBookmarkedItems().remove(item);
    userRepository.save(managedUser);
  }

  @Override
  @Transactional
  public boolean isBookmarked(User user, Long itemId) {
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFoundException(user.getId()));
    return managedUser.getBookmarkedItems()
        .stream()
        .anyMatch(item -> item.getItemId().equals(itemId));
  }
  @Override
  @Transactional
  public Set<Item> getBookmarkedItems(User user) {
    User managedUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFoundException(user.getId()));
    return managedUser.getBookmarkedItems();
  }
}
