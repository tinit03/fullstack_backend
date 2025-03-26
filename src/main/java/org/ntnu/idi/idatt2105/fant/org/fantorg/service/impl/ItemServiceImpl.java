package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ItemService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.ItemSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;
  public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository) {
    this.itemRepository = itemRepository;
    this.categoryRepository = categoryRepository;
  }

  @Override
  public Item createItem(ItemCreateDto dto, User seller) {
    Item item = ItemMapper.toItem(dto);
    item.setSeller(seller);
    item.setPublishedAt(LocalDateTime.now());

    Category subCategory = categoryRepository.findById(dto.getSubcategoryId())
        .orElseThrow(() -> new EntityNotFoundException("subcategory not found")); //create custom exceptions later
    item.setSubCategory(subCategory);
    return itemRepository.save(item);
  }

  @Override
  public Item updateItem(Long id, ItemCreateDto updatedItem, User seller) {
    Item existing = itemRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Item not found"));

    if (!existing.getSeller().getId().equals(seller.getId())) {
      throw new SecurityException("You don't own this item");
    }
    Category subCategory = categoryRepository.findById(updatedItem.getSubcategoryId())
            .orElseThrow(() -> new EntityNotFoundException("subcategory not found"));


    existing.setTitle(updatedItem.getItemName());
    existing.setDescription(updatedItem.getDescription());
    existing.setPrice(updatedItem.getPrice());
    existing.setSubCategory(subCategory);
    existing.setLocation(new Location(updatedItem.getPostalCode(),updatedItem.getCity()));
    existing.setListingType(updatedItem.getListingType());
    return itemRepository.save(existing);
  }

  @Override
  public void deleteItem(Long id, User seller) {
    Item existing = itemRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Item not found"));

    if (!existing.getSeller().getId().equals(seller.getId())) {
      throw new SecurityException("You don't own this item");
    }

    itemRepository.delete(existing);
  }

  @Override
  public Optional<Item> getItemById(Long id) {
    return itemRepository.findById(id);
  }

  @Override
  public Page<Item> getAllItems(Pageable pageable) {
    return itemRepository.findAll(pageable);
  }

  @Override
  public Page<Item> searchItems(String keyword, Pageable pageable) {
    Specification<Item> spec = Specification.where(null);
    if (keyword != null && !keyword.isBlank()) {
      String[] tokens = keyword.toLowerCase().split(" ");
      for (String token : tokens) {
        spec = spec.and(ItemSpecification.hasKeyWordInAnyField(token));
      }
    }
    return itemRepository.findAll(spec, pageable);
  }

  @Override
  public Page<Item> getItemsBySeller(User seller, Pageable pageable) {
    return itemRepository.findItemBySeller(seller, pageable);
  }
}
