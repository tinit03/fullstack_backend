package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.io.IOException;
import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;

/** Service interface for managing images associated with items. */
public interface ImageService {

  /**
   * Saves an uploaded image for a specific item.
   *
   * @param dto The DTO containing the image data (usually base64 or URL).
   * @param itemId The ID of the item to associate the image with.
   * @return The saved image as a DTO.
   */
  ImageItemDto saveImage(ImageItemUploadDto dto, Long itemId);

  /**
   * Retrieves all images associated with a specific item.
   *
   * @param itemId The ID of the item.
   * @return A list of image DTOs.
   */
  List<ImageItemDto> getImagesByItemId(Long itemId);

  /**
   * Updates an existing image with a new URL.
   *
   * @param url The new image URL.
   * @param currentImage The current image entity to update.
   * @return The updated image entity.
   */
  Image updateImage(String url, Image currentImage);

  /**
   * Deletes an image both from storage and the database.
   *
   * @param image The image entity to delete.
   * @throws IOException If an error occurs during deletion from cloud storage.
   */
  void deleteImage(Image image) throws IOException;
}
