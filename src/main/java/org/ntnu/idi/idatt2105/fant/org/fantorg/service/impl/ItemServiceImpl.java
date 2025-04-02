package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item.ItemNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BookmarkRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BookmarkService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BringService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ItemService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.ItemSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;

  private final CloudinaryService cloudinaryService;

  private final BookmarkRepository bookmarkRepository;

  private final BookmarkService bookmarkService;

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

    List<Image> imageEntities = new ArrayList<>();
    Item savedItem = itemRepository.save(item);

    if (null == dto.getImages()) {
      return savedItem;
    }

    for (ImageCreateDto imgDto : dto.getImages()) {
      try {
        Map<String, String> result = cloudinaryService.uploadBase64Image(imgDto.getUrl());
        Image image = new Image();
        image.setUrl(result.get("url"));
        image.setPublicId(result.get("public_id"));
        image.setCaption(imgDto.getCaption());
        image.setItem(savedItem);
        imageEntities.add(image);
      } catch (IOException e) {
        logger.warn("Failed to upload image, skipping: {}", e.getMessage());
      }
    }
    imageRepository.saveAll(imageEntities);
    savedItem.setImages(imageEntities);
    return savedItem;
  }
  @Override
  public Item updateItem(Long id, ItemEditDto updatedItem, User seller) {
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
    existing.setCondition(updatedItem.getCondition());
    existing.setTags(updatedItem.getTags());
    existing.setForSale(updatedItem.isForSale());

    // Step 1: Upload new base64 images first and enrich DTO
    // Basically går gjennom alle bildene som har blitt lastet opp fra frontend.
    List<Image> newImages = new ArrayList<>();
    for (ImageEditDto imgDto : updatedItem.getImages()) {
      if (imgDto.getPublicId() == null && imgDto.getUrl() != null) {
        try {
          Map<String, String> result = cloudinaryService.uploadBase64Image(imgDto.getUrl());
          imgDto.setPublicId(result.get("public_id")); // Update the DTO
          imgDto.setUrl(result.get("url"));
          Image image = new Image();
          image.setUrl(result.get("url"));
          image.setPublicId(result.get("public_id"));
          image.setCaption(imgDto.getCaption());
          image.setItem(existing);
          newImages.add(image);
        } catch (IOException e) {
          logger.warn("Failed to upload image, skipping: {}", e.getMessage());
        }
      }
    }

// Step 2: Fetch existing image publicIds
    List<Image> existingImages = imageRepository.findByItem_ItemId(id);
    List<String> updatedPublicIds = updatedItem.getImages().stream()
        .map(ImageEditDto::getPublicId)
        .filter(Objects::nonNull)
        .toList();

// Step 3: Delete removed images
    List<Image> imagesToDelete = existingImages.stream()
        .filter(img -> img.getPublicId() != null && !updatedPublicIds.contains(img.getPublicId()))
        .toList();

    for (Image image : imagesToDelete) {
      try {
        cloudinaryService.deleteImage(image.getPublicId());
      } catch (IOException e) {
        logger.warn("Failed to delete Cloudinary image: {}", e.getMessage());
      }
    }
    imageRepository.deleteAll(imagesToDelete);

// Step 4: Save new images
    imageRepository.saveAll(newImages);

// Step 5: Update captions of retained images only
    List<Image> retainedImages = existingImages.stream()
        .filter(img -> img.getPublicId() != null && updatedPublicIds.contains(img.getPublicId()))
        .toList();

    for (Image retainedImage : retainedImages) {
      updatedItem.getImages().stream()
          .filter(dto -> dto.getPublicId() != null &&
              dto.getPublicId().equals(retainedImage.getPublicId()))
          .findFirst()
          .ifPresent(dto -> retainedImage.setCaption(dto.getCaption()));
    }
    imageRepository.saveAll(retainedImages);

    return itemRepository.save(existing);
  }

  @Override
  public Item changeStatus(Long id, Status status, User seller) {
    Item existing = itemRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Item not found"));

    if (!existing.getSeller().getId().equals(seller.getId())) {
      throw new SecurityException("You don't own this item");
    }
    existing.setStatus(status);
    Item savedItem = itemRepository.save(existing);
    return savedItem;
  }

  @Override
  public void deleteItem(Long id, User seller) {
    Item existing = itemRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Item not found"));

    if (!existing.getSeller().getId().equals(seller.getId())) {
      throw new SecurityException("You don't own this item");
    }
    if (existing.getImages() != null) {
      for (Image image : existing.getImages()) {
        try {
          cloudinaryService.deleteImage(image.getPublicId());
        } catch (IOException e) {
          logger.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
        }
      }
    }

    itemRepository.delete(existing);
  }

  @Override
  public Item getItemById(Long id) {
    return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
  }

  @Override
  public ItemDto getItemByIdBookmarked(Long id, User user) {
    Item item = getItemById(id);
    ItemDto dto = ItemMapper.toItemDto(item);
    if(user==null){
      return dto;
    }
    if(bookmarkService.isBookmarked(user,id)){
      dto.setIsBookmarked(true);
    }
    return dto;
  }

  @Override
  public Page<ItemDto> getAllItems(Pageable pageable, User user) {
    Page<Item> pageItem = itemRepository.findAll(pageable);
    Page<ItemDto> pageDto = pageItem.map(ItemMapper::toItemDto);
    return markDtosWithBookmarkStatus(pageDto,user);
  }

  @Override
  public Page<ItemDto> searchItems(String keyword, Pageable pageable, User user) {
    Specification<Item> spec = Specification.where(null);
    if (keyword != null && !keyword.isBlank()) {
      String[] tokens = keyword.toLowerCase().split(" ");
      for (String token : tokens) {
        spec = spec.and(ItemSpecification.hasKeyWordInAnyField(token));
      }
    }
    Page<Item> items =  itemRepository.findAll(spec, pageable);
    Page<ItemDto> dto = items.map(ItemMapper::toItemDto);
    return markDtosWithBookmarkStatus(dto,user);
  }

  @Override
  public Page<ItemDto> getItemsBySeller(User seller, Pageable pageable) {
    Page<Item> items = itemRepository.findItemBySeller(seller, pageable);
    return items.map(ItemMapper::toItemDto);
  }

  private Page<ItemDto> markDtosWithBookmarkStatus(Page<ItemDto> dtos, User user) {
    if (user == null) return dtos;
    Set<Long> bookmarkedIds = bookmarkRepository.findByUser(user)
        .stream()
        .map(bookmark -> bookmark.getItem().getItemId())
        .collect(Collectors.toSet());
    for (ItemDto dto : dtos) {
      dto.setIsBookmarked(bookmarkedIds.contains(dto.getId()));
    }
    return dtos;
  }
}
