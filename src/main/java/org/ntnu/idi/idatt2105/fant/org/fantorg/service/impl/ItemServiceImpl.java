package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchFilter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item.ItemNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BookmarkRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BookmarkService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BringService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ItemService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.ItemFacetCountUtil;
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
  private final ItemFacetCountUtil facetUtil;


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

    for (ImageItemUploadDto imgDto : dto.getImages()) {
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
    if(updatedItem.getImages()==null) return itemRepository.save(existing);
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
    if(user.getId().equals(item.getSeller().getId())){
      dto.setIsOwner(true);
    }
    return dto;
  }

  @Override
  public Page<ItemDto> getAllItems(Pageable pageable, Status status, User user) {
    Specification<Item> spec = Specification.where(null);
    if(user!=null){
      spec = spec.and(ItemSpecification.hasNotSeller(user));
    }
    if (status != null && status != Status.INACTIVE) {
      spec = spec.and(ItemSpecification.hasStatus(status));
    } else if (status==null) {
      // Om det er ikke satt noe filter, så sender den alle aktive og solgte
      spec = spec.and(ItemSpecification.hasStatusIn(Status.ACTIVE,Status.SOLD));
    } else throw new IllegalArgumentException("Inactive items are private");

    Page<Item> pageItem = itemRepository.findAll(spec,pageable);
    Page<ItemDto> pageDto = pageItem.map(ItemMapper::toItemDto);
    return markDtosWithBookmarkStatus(pageDto,user);
  }

  @Override
  public ItemSearchResponse searchItems(
      ItemSearchFilter filter,
      Pageable pageable,
      User user
  ) {
    Specification<Item> baseSpec = buildItemSpec(filter, null);
    Specification<Item> fullSpec = (user != null) ? baseSpec.and(ItemSpecification.hasNotSeller(user)) : baseSpec;

    Page<Item> items = itemRepository.findAll(fullSpec, pageable);
    Page<ItemDto> dto = items.map(ItemMapper::toItemDto);
    dto = markDtosWithBookmarkStatus(dto, user);

    ItemSearchResponse response = new ItemSearchResponse();
    response.setItems(dto);

    // Only exclude each respective field for that facet
    Specification<Item> specForCondition = buildItemSpec(filter, "condition");
    Specification<Item> specForForSale = buildItemSpec(filter, "forSale");
    Specification<Item> specForCounty = buildItemSpec(filter, "county");
    Specification<Item> specForCategory = buildItemSpec(filter, "category");
    Specification<Item> specForSubCategory = buildItemSpec(filter, "subCategory");
    Specification<Item> specForToday = buildItemSpec(filter, "onlyToday");

    if (user != null) {
      specForCondition = specForCondition.and(ItemSpecification.hasNotSeller(user));
      specForForSale = specForForSale.and(ItemSpecification.hasNotSeller(user));
      specForCounty = specForCounty.and(ItemSpecification.hasNotSeller(user));
      specForCategory = specForCategory.and(ItemSpecification.hasNotSeller(user));
      specForSubCategory = specForSubCategory.and(ItemSpecification.hasNotSeller(user));
      specForToday = specForToday.and(ItemSpecification.hasNotSeller(user));
    }

    response.setConditionFacet(facetUtil.getEnumFacetCounts(specForCondition, "condition", Condition.class));
    response.setForSaleFacet(facetUtil.getBooleanFacetCounts(specForForSale, "forSale"));
    response.setCountyFacet(facetUtil.getStringFacetCounts(specForCounty, "location.county"));
    response.setCategoryFacet(facetUtil.getLongFacetCounts(specForCategory, "subCategory.parentCategory.id"));
    response.setSubCategoryFacet(facetUtil.getLongFacetCounts(specForSubCategory, "subCategory.id"));
    response.setPublishedTodayFacet(facetUtil.getPublishedTodayFacetCounts(specForToday));

    return response;
  }

  @Override
  public Page<ItemDto> getItemsBySeller(User seller, Status status, Pageable pageable) {
    Specification<Item> spec = ItemSpecification.hasSeller(seller);
    if (status != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    Page<Item> items = itemRepository.findAll(spec,pageable);
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

  private Specification<Item> buildItemSpec(ItemSearchFilter filter, String excludeField) {
    Specification<Item> spec = ItemSpecification.hasStatus(Status.ACTIVE);
    if (!"keyword".equals(excludeField) && filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
      String[] tokens = filter.getKeyword().toLowerCase().split(" ");
      for (String token : tokens) {
        spec = spec.and(ItemSpecification.hasKeyWordInAnyField(token));
      }
    }

    if (!"category".equals(excludeField) && filter.getCategoryId() != null && !filter.getCategoryId().isBlank()) {
      List<Long> categoryIds = Arrays.stream(filter.getCategoryId().split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .map(Long::parseLong)
          .toList();
      spec = spec.and(ItemSpecification.hasCategoryIn(categoryIds));
    }

    if (!"subCategory".equals(excludeField) && filter.getSubCategoryId() != null && !filter.getSubCategoryId().isBlank()) {
      List<Long> subCategoryIds = Arrays.stream(filter.getSubCategoryId().split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .map(Long::parseLong)
          .toList();
      spec = spec.and(ItemSpecification.hasSubCategoryIn(subCategoryIds));
    }

    if (!"condition".equals(excludeField) && filter.getCondition() != null && !filter.getCondition().isBlank()) {
      List<Condition> conditions = Arrays.stream(filter.getCondition().split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .map(String::toUpperCase)
          .map(Condition::valueOf)
          .toList();
      spec = spec.and(ItemSpecification.hasConditionIn(conditions));
    }

    if (!"county".equals(excludeField) && filter.getCounty() != null && !filter.getCounty().isBlank()) {
      List<String> counties = Arrays.stream(filter.getCounty().split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .toList();
      spec = spec.and(ItemSpecification.isInCounties(counties));
    }

    if (!"price".equals(excludeField)) {
      BigDecimal min = filter.getMinPrice() != null ? BigDecimal.valueOf(filter.getMinPrice()) : null;
      BigDecimal max = filter.getMaxPrice() != null ? BigDecimal.valueOf(filter.getMaxPrice()) : null;
      spec = spec.and(ItemSpecification.hasPriceBetween(min, max));
    }

    if (!"forSale".equals(excludeField) && filter.getForSale() != null) {
      spec = spec.and(ItemSpecification.hasForSale(filter.getForSale()));
    }

    if (!"onlyToday".equals(excludeField) && filter.getOnlyToday() != null) {
      spec = spec.and(ItemSpecification.isPublishedToday());
    }

    return spec;
  }
}
