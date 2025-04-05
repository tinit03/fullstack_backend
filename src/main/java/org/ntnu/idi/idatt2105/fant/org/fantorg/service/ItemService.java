package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.math.BigDecimal;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchFilter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
  Item createItem(ItemCreateDto dto, User seller);
  Item updateItem(Long id, ItemEditDto dto, User seller);
  Item changeStatus(Long id, Status status, User seller);
  void deleteItem(Long id, User seller);

  //TODO: Endre alle Item til ItemDto, burde ikke sende entitet til klient.
  Item getItemById(Long id);

  ItemDto getItemByIdBookmarked(Long id, User user);

  Page<ItemDto> getAllItems(Pageable pageable,User user, Status status);


  Page<ItemDto> searchItems(ItemSearchFilter itemSearchFilter,Pageable pageable, User user
  );

  Page<ItemDto> getItemsBySeller(User seller, Status status, Pageable pageable);
}
