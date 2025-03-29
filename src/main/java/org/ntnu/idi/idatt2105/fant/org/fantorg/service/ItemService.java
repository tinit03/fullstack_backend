package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
  Item createItem(ItemCreateDto dto, User seller);
  Item updateItem(Long id, ItemEditDto dto, User seller);
  void deleteItem(Long id, User seller);

  //TODO: Endre alle Item til ItemDto, burde ikke sende entitet til klient.
  Optional<Item> getItemById(Long id);
  Page<Item> getAllItems(Pageable pageable);
  Page<Item> searchItems(String keyword, Pageable pageable);
  Page<Item> getItemsBySeller(User seller, Pageable pageable);

}
