package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface ItemService {
  Item createItem(ItemCreateDto dto, User seller);
  Item updateItem(Long id, Item updatedItem, User seller);
  void deleteItem(Long id, User seller);
  Optional<Item> getItemById(Long id);
  Page<Item> getAllItems(Pageable pageable);
  Page<Item> searchItems(String keyword, Pageable pageable);
  Page<Item> getItemsBySeller(User seller, Pageable pageable);

}
