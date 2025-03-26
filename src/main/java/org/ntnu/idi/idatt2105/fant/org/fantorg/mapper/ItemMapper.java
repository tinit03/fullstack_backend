package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDetailDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;

public class ItemMapper {
  public static ItemDto toItemDto(Item item) {
    ItemDto dto = new ItemDto();
    dto.setId(item.getItemId());
    dto.setName(item.getTitle());
    dto.setDescription(item.getDescription());
    dto.setPrice(item.getPrice());
    dto.setTags(item.getTags());
    dto.setPublishedAt(item.getPublishedAt());

    Optional.ofNullable(item.getLocation())
        .ifPresent(location -> {
          dto.setCity(location.getCity());
          dto.setPostalCode(location.getPostalCode());
        });

    Optional.ofNullable(item.getSeller())
        .map(UserMapper::toDto)
        .ifPresent(userDto -> {
          dto.setSellerFullName(userDto.getFullName());
          dto.setSellerId(userDto.getId());
        });

    return dto;
  }

  public static ItemDetailDto toItemDetailDto(Item item) {
    ItemDetailDto dto = new ItemDetailDto();
    dto.setId(item.getItemId());
    dto.setName(item.getTitle());
    dto.setDescription(item.getDescription());
    dto.setPrice(item.getPrice());
    dto.setTags(item.getTags());
    dto.setPublishedAt(item.getPublishedAt());

    Optional.ofNullable(item.getLocation()).ifPresent(location -> {
      dto.setCity(location.getCity());
      dto.setPostalCode(location.getPostalCode());
    });

    Optional.ofNullable(item.getSeller())
        .map(UserMapper::toDto)
        .ifPresent(userDto -> {
          dto.setSellerFullName(userDto.getFullName());
          dto.setSellerId(userDto.getId());
        });
    // legge til logikk senere
    //dto.setImageUrls(List.of());
    //dto.setAverageRating(null);
    //dto.setReviewCount(0);
    //dto.setIsBookmarked(false);

    return dto;
  }

  public static Item toItem(ItemCreateDto dto) {
    Item item = new Item();
    item.setTitle(dto.getItemName());
    item.setDescription(dto.getDescription());
    item.setPrice(dto.getPrice());

    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      item.setTags(dto.getTags());
    } else {
      item.setTags(List.of()); // Default to empty list if no tags are added
    }
    Location location = new Location();
    location.setCity(dto.getCity());
    location.setPostalCode(dto.getPostalCode());

    item.setLocation(location);
    item.setListingType(dto.getListingType());
    return item;
  }
}
