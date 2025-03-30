package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ImageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
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

  /**
   * Saves a new image and links it to an existing item.
   *
   * @param dto The image data to save.
   * @param itemId The ID of the item to associate the image with.
   * @return The saved image as a DTO.
   * @throws EntityNotFoundException if the item does not exist.
   */
  @Override
  public ImageDto saveImage(ImageCreateDto dto, Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    Image image = ImageMapper.fromCreateDto(dto);
    image.setItem(item);
    Image savedImage = imageRepository.save(image);

    return ImageMapper.toDto(savedImage);
  }
  /**
   * Retrieves all images associated with a given item ID.
   *
   * @param itemId The ID of the item.
   * @return List of image DTOs.
   */
  @Override
  public List<ImageDto> getImagesByItemId(Long itemId) {
    if (!itemRepository.existsById(itemId)) {
      throw new EntityNotFoundException("Item with ID " + itemId + " not found");
    }

    List<Image> images = imageRepository.findByItem_ItemId(itemId);
    return ImageMapper.toDtoList(images);


  }
  /**
   * Deletes an image by its ID.
   *
   * @param imageId The ID of the image to delete.
   * @throws EntityNotFoundException if the image does not exist.
   */
  @Override
  public void deleteImage(Long imageId) {
    if (!imageRepository.existsById(imageId)) {
      throw new EntityNotFoundException("Image with ID " + imageId + " not found");
    }
    imageRepository.deleteById(imageId);
  }

}
