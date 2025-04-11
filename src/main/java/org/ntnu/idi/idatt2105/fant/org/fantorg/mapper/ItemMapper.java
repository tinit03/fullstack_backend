package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;

/**
 * Utility class for converting between Item entities and their corresponding DTOs.
 *
 * <p>The ItemMapper class provides methods to map Item entities to ItemDto objects for reading
 * operations and ItemCreateDto objects for creating or updating Item entities.
 */
public class ItemMapper {

  /**
   * Converts an Item entity to an ItemDto.
   *
   * <p>This method maps all relevant fields from the Item entity to an ItemDto, including the item
   * details, associated user and images, as well as category information.
   *
   * @param item The Item entity to be converted.
   * @return The ItemDto containing item details.
   */
  public static ItemDto toItemDto(Item item) {
    ItemDto dto = new ItemDto();
    dto.setId(item.getItemId());
    dto.setName(item.getTitle());
    dto.setDescription(item.getDescription());
    dto.setPrice(item.getPrice());
    dto.setTags(item.getTags());
    dto.setCategoryId(item.getSubCategory().getParentCategory().getCategoryId());
    dto.setSubCategoryId(item.getSubCategory().getCategoryId());
    dto.setCategoryName(item.getSubCategory().getParentCategory().getCategoryName());
    dto.setSubCategoryName(item.getSubCategory().getCategoryName());
    dto.setPublishedAt(item.getPublishedAt());
    dto.setListingType(item.getListingType());
    dto.setCondition(item.getCondition());
    dto.setStatus(item.getStatus());
    dto.setForSale(item.isForSale());

    // Set location if available
    Optional.ofNullable(item.getLocation())
        .ifPresent(
            location -> {
              dto.setLocation(item.getLocation());
            });

    // Set seller details if available
    Optional.ofNullable(item.getSeller())
        .map(UserMapper::toDto)
        .ifPresent(
            userDto -> {
              dto.setSellerFullName(userDto.getFullName());
              dto.setSellerId(userDto.getId());
              if (userDto.getProfilePicture() != null) {
                dto.setSellerPicture(userDto.getProfilePicture().getUrl());
              }
            });

    // Set images related to the item
    dto.setImages(ImageMapper.toDtoList(item.getImages()));

    return dto;
  }

  /**
   * Converts an ItemCreateDto to an Item entity.
   *
   * <p>This method maps the fields from the ItemCreateDto to create a new Item entity.
   *
   * @param dto The ItemCreateDto containing the data for creating a new Item.
   * @return The Item entity created from the DTO.
   */
  public static Item toItem(ItemCreateDto dto) {
    Item item = new Item();
    item.setTitle(dto.getItemName());
    item.setDescription(dto.getDescription());
    item.setPrice(dto.getPrice());
    item.setListingType(dto.getListingType());
    item.setCondition(dto.getCondition());
    item.setStatus(dto.getStatus());

    // Set tags from DTO or default to an empty list if none are provided
    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      item.setTags(dto.getTags());
    } else {
      item.setTags(List.of()); // Default to empty list if no tags are added
    }

    return item;
  }
}
