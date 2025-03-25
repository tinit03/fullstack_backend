package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.ItemDetailDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;

public class ItemMapper {

  public static ItemDto toItemDto(Item item) {
    ItemDto dto = new ItemDto();
    dto.setId(item.getItemId());
    dto.setName(item.getTitle());
    dto.setDescription(item.getBriefDescription());
    dto.setPrice(item.getPrice());
    dto.setTags(item.getTags());
    dto.setPublishedAt(item.getPublishedAt());

    Optional.ofNullable(item.getLocation())
        .ifPresent(location -> {
          dto.setCity(location.getCity());
          dto.setPostalCode(location.getPostalCode());
        });
    Optional.ofNullable(item.getSeller()).ifPresent(seller -> {
      dto.setSellerId(seller.getId());
      dto.setSellerUsername(seller.getUsername()); //Ønskelig å bruke hele navnet enn brukernavnet.
    });
    return dto;
  }

  public static ItemDetailDto toItemDetailDto(Item item) {
    ItemDetailDto dto = new ItemDetailDto();
    dto.setId(item.getItemId());
    dto.setName(item.getTitle());
    dto.setDescription(item.getBriefDescription());
    dto.setPrice(item.getPrice());
    dto.setTags(item.getTags());
    dto.setPublishedAt(item.getPublishedAt());

    Optional.ofNullable(item.getLocation()).ifPresent(location -> {
      dto.setCity(location.getCity());
      dto.setPostalCode(location.getPostalCode());
//      dto.setLatitude(location.getLatitude());
//      dto.setLongitude(location.getLongitude());
    });

    Optional.ofNullable(item.getSeller()).ifPresent(seller -> {
      dto.setSellerId(seller.getId());
      dto.setSellerUsername(seller.getUsername());
    });

    dto.setFullDescription(item.getFullDescription());
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
    item.setBriefDescription(dto.getBriefDescription());
    item.setFullDescription(dto.getFullDescription());
    item.setPrice(dto.getPrice());

    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      item.setTags(dto.getTags());
    } else {
      item.setTags(List.of()); // Default to empty list if no tags are added
    }
    Location location = new Location();
    location.setCity(dto.getCity());
    location.setPostalCode(dto.getPostalCode());
//    location.setLatitude(dto.getLatitude());
//    location.setLongitude(dto.getLongitude());
    item.setLocation(location);
    return item;
  }
}
