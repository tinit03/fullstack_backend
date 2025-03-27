package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;

  /**
   * Uploads a new image for a given item.
   *
   * @param itemId ID of the item the image is associated with.
   * @param imageDto Data for creating the image.
   * @return The created image.
   */
  @PostMapping("/{itemId}")
  public ResponseEntity<ImageDto> uploadImage(@PathVariable Long itemId,
      @Validated @RequestBody ImageCreateDto imageDto) {
    ImageDto savedImage = imageService.saveImage(imageDto, itemId);

    return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
  }

  /**
   * Retrieves all images for a given item.
   *
   * @param itemId ID of the item.
   * @return List of images.
   */
  @GetMapping("/item/{itemId}")
  public ResponseEntity<List<ImageDto>> getImagesByItem(@PathVariable Long itemId) {
    List<ImageDto> images = imageService.getImagesByItemId(itemId);
    return ResponseEntity.ok(images);
  }

  /**
   * Deletes an image by its ID.
   *
   * @param imageId ID of the image to delete.
   * @return No content response.
   */
  @DeleteMapping("/{imageId}")
  public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
    imageService.deleteImage(imageId);
    return ResponseEntity.noContent().build();
  }
}