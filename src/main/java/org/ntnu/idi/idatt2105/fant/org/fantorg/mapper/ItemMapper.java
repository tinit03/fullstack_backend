package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

public class ItemMapper {
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
    Optional.ofNullable(item.getLocation())
        .ifPresent(location -> {
          dto.setLocation(item.getLocation());
        });

    Optional.ofNullable(item.getSeller())
        .map(UserMapper::toDto)
        .ifPresent(userDto -> {
          dto.setSellerFullName(userDto.getFullName());
          dto.setSellerId(userDto.getId());
        });
    dto.setImages(ImageMapper.toDtoList(item.getImages()));
    return dto;
  }

  public static Item toItem(ItemCreateDto dto) {
    Item item = new Item();
    item.setTitle(dto.getItemName());
    item.setDescription(dto.getDescription());
    item.setPrice(dto.getPrice());
    item.setListingType(dto.getListingType());
    item.setCondition(dto.getCondition());
    item.setStatus(Status.ACTIVE);
    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      item.setTags(dto.getTags());
    } else {
      item.setTags(List.of()); // Default to empty list if no tags are added
    }
    return item;
  }
}
