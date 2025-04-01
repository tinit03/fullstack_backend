package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;

public class ImageMapper {

  public static ImageDto toDto(Image image) {
    if (image == null) return null;

    ImageDto dto = new ImageDto();
    dto.setUrl(image.getUrl());
    dto.setCaption(image.getCaption());
    dto.setPublicId(image.getPublicId());
    return dto;
  }

  public static List<ImageDto> toDtoList(List<Image> images) {
    return images == null ? null : images.stream().map(ImageMapper::toDto).collect(Collectors.toList());
  }

  public static Image fromCreateDto(ImageCreateDto dto) {
    if (dto == null) return null;

    Image image = new Image();
    image.setUrl(dto.getUrl());
    image.setCaption(dto.getCaption());
    return image;
  }

  public static List<Image> fromCreateDtoList(List<ImageCreateDto> dtos) {
    return dtos == null ? null : dtos.stream().map(ImageMapper::fromCreateDto).collect(Collectors.toList());
  }
}