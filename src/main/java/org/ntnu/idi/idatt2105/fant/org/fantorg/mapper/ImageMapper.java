package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;

/**
 * Utility class for converting between Image entities and their corresponding DTOs.
 *
 * <p>The ImageMapper class provides methods to map Image entities to ImageDto objects, as well as
 * methods to map Image DTOs for image creation and profile updates.
 */
public class ImageMapper {

  /**
   * Converts an Image entity to an ImageItemDto.
   *
   * <p>This method maps the relevant fields from the Image entity to an ImageItemDto, including the
   * URL, caption, and public ID.
   *
   * @param image The Image entity to be converted.
   * @return The ImageItemDto containing image details, or null if the image is null.
   */
  public static ImageItemDto toItemImageDto(Image image) {
    if (image == null) return null;

    ImageItemDto dto = new ImageItemDto();
    dto.setUrl(image.getUrl());
    dto.setCaption(image.getCaption());
    dto.setPublicId(image.getPublicId());
    return dto;
  }

  /**
   * Converts a list of Image entities to a list of ImageItemDto objects.
   *
   * <p>This method iterates through the given list of Image entities and converts each one to an
   * ImageItemDto.
   *
   * @param images The list of Image entities to be converted.
   * @return A list of ImageItemDto objects, or null if the input list is null.
   */
  public static List<ImageItemDto> toDtoList(List<Image> images) {
    return images == null
        ? null
        : images.stream().map(ImageMapper::toItemImageDto).collect(Collectors.toList());
  }

  /**
   * Converts an Image entity to an ImageDto.
   *
   * <p>This method maps the relevant fields from the Image entity to an ImageDto, including the
   * public ID and URL.
   *
   * @param image The Image entity to be converted.
   * @return The ImageDto containing the image details, or null if the image is null.
   */
  public static ImageDto toDto(Image image) {
    if (image == null) return null;

    ImageDto dto = new ImageDto();
    dto.setPublicId(image.getPublicId());
    dto.setUrl(image.getUrl());
    return dto;
  }

  /**
   * Converts an ImageItemUploadDto to an Image entity.
   *
   * <p>This method creates a new Image entity from the provided ImageItemUploadDto, which contains
   * the image URL and caption.
   *
   * @param dto The ImageItemUploadDto containing image URL and caption.
   * @return The Image entity created from the DTO, or null if the DTO is null.
   */
  public static Image fromCreateDto(ImageItemUploadDto dto) {
    if (dto == null) return null;

    Image image = new Image();
    image.setUrl(dto.getUrl());
    image.setCaption(dto.getCaption());
    return image;
  }

  /**
   * Converts an ImageDto to an Image entity for profile update purposes.
   *
   * <p>This method creates a new Image entity from the provided ImageDto, which contains the public
   * ID and URL. The other properties of the Image are set to default values.
   *
   * @param dto The ImageDto containing the public ID and URL.
   * @return The Image entity created from the DTO, or null if the DTO is null.
   */
  public static Image fromProfileDto(ImageDto dto) {
    if (dto == null) return null;

    Image image = new Image();
    image.setItem(null); // no associated item
    image.setCaption(""); // no caption
    image.setPublicId(dto.getPublicId());
    image.setUrl(dto.getUrl());
    return image;
  }

  /**
   * Converts a list of ImageItemUploadDto objects to a list of Image entities.
   *
   * <p>This method iterates through the given list of ImageItemUploadDto objects and converts each
   * one to an Image entity.
   *
   * @param dtos The list of ImageItemUploadDto objects to be converted.
   * @return A list of Image entities, or null if the input list is null.
   */
  public static List<Image> fromCreateDtoList(List<ImageItemUploadDto> dtos) {
    return dtos == null
        ? null
        : dtos.stream().map(ImageMapper::fromCreateDto).collect(Collectors.toList());
  }
}
