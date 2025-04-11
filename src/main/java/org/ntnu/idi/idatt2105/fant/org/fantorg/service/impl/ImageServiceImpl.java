package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ImageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing images associated with items.
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
private final ImageRepository imageRepository;
private final ItemRepository itemRepository;
private final CloudinaryService cloudinaryService;
  /**
   * Saves a new image and links it to an existing item.
   *
   * @param dto The image data to save.
   * @param itemId The ID of the item to associate the image with.
   * @return The saved image as a DTO.
   * @throws EntityNotFoundException if the item does not exist.
   */
  @Override
  public ImageItemDto saveImage(ImageItemUploadDto dto, Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    Image image = ImageMapper.fromCreateDto(dto);
    image.setItem(item);
    Image savedImage = imageRepository.save(image);

    return ImageMapper.toItemImageDto(savedImage);
  }
  /**
   * Retrieves all images associated with a given item ID.
   *
   * @param itemId The ID of the item.
   * @return List of image DTOs.
   */
  @Override
  public List<ImageItemDto> getImagesByItemId(Long itemId) {
    if (!itemRepository.existsById(itemId)) {
      throw new EntityNotFoundException("Item with ID " + itemId + " not found");
    }

    List<Image> images = imageRepository.findByItem_ItemId(itemId);
    return ImageMapper.toDtoList(images);


  }
  /**
   * Deletes an image by its ID.
   *
   * @param image the image that is going to be deleted.
   * @throws IllegalArgumentException if the upload of the image fails.
   */
  @Override
  public void deleteImage(Image image) {
    if (image == null) return;
    try{
      String publicId = image.getPublicId();
      if (publicId != null && !publicId.isBlank()) {
        cloudinaryService.deleteImage(publicId);
      }

      imageRepository.delete(image);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Something happened");
    }

  }

  /**
   * Updates the image given an url and image.
   * @param url The image url.
   * @param currentImage Current image.
   * @return The updated image.
   */
  @Override
  public Image updateImage(String url, Image currentImage) {
    if (url == null || url.isBlank()) {
      deleteImage(currentImage);
      return null;
    }

    if (url.startsWith("http://") || url.startsWith("https://")) {
      return currentImage; // No update needed
    }

    deleteImage(currentImage);
    try {
      Map<String, String> result = cloudinaryService.uploadBase64Image(url);
      Image image = new Image();
      image.setUrl(result.get("url"));
      image.setPublicId(result.get("public_id"));
      return imageRepository.save(image);
    } catch (IOException exception) {
      throw new IllegalArgumentException("Something happened", exception);
    }
  }
}
