package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;

public class ImageMapper {

  public static ImageItemDto toItemImageDto(Image image) {
    if (image == null) return null;

    ImageItemDto dto = new ImageItemDto();
    dto.setUrl(image.getUrl());
    dto.setCaption(image.getCaption());
    dto.setPublicId(image.getPublicId());
    return dto;
  }

  public static List<ImageItemDto> toDtoList(List<Image> images) {
    return images == null ? null : images.stream().map(ImageMapper::toItemImageDto).collect(Collectors.toList());
  }
  public static ImageDto toDto(Image image){
    if(image == null) return null;
    ImageDto dto = new ImageDto();
    dto.setPublicId(image.getPublicId());
    dto.setUrl(image.getUrl());
    return dto;
  }
  public static Image fromCreateDto(ImageItemUploadDto dto) {
    if (dto == null) return null;

    Image image = new Image();
    image.setUrl(dto.getUrl());
    image.setCaption(dto.getCaption());
    return image;
  }

  public static Image fromProfileDto(ImageDto dto) {
    if (dto == null) return null;
    Image image = new Image();
    image.setItem(null);
    image.setCaption("");
    image.setPublicId(dto.getPublicId());
    image.setUrl(dto.getUrl());
    return image;
  }

  public static List<Image> fromCreateDtoList(List<ImageItemUploadDto> dtos) {
    return dtos == null ? null : dtos.stream().map(ImageMapper::fromCreateDto).collect(Collectors.toList());
  }
}