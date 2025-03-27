package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ImageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BringService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ItemService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.ItemSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;

  private final ImageRepository imageRepository;

  private final BringService bringService;

  private final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

  @Override
  public Item createItem(ItemCreateDto dto, User seller) {
    //Map the item details from the dto to entity
    Item item = ItemMapper.toItem(dto);
    item.setSeller(seller);
    item.setPublishedAt(LocalDateTime.now());
    //Retrieve and set the subcategory from the repository
    Category subCategory = categoryRepository.findById(dto.getSubcategoryId())
        .orElseThrow(() -> new EntityNotFoundException(
            "subcategory not found")); //create custom exceptions later
    item.setSubCategory(subCategory);

    Location location = bringService.getLocationDetails(dto.getPostalCode());
    item.setLocation(location);
    //Save item into repository, so we can get an itemId for images.
    Item savedItem = itemRepository.save(item);
    return savedItem;
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

    Location location = bringService.getLocationDetails(updatedItem.getPostalCode());
    existing.setTitle(updatedItem.getItemName());
    existing.setDescription(updatedItem.getDescription());
    existing.setPrice(updatedItem.getPrice());
    existing.setSubCategory(subCategory);
    existing.setLocation(location);
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
