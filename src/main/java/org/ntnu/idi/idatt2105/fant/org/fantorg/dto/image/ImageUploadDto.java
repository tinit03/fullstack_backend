package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for uploading an image.
 * <p>
 * This DTO is used to transfer the image's URL during the image upload process in the application.
 * </p>
 */
@Getter
@Setter
public class ImageUploadDto {

  /**
   * The URL of the image.
   * <p>
   * This field contains the URL where the image is hosted. It is used when uploading an image
   * or referencing an image in the application.
   * </p>
   */
  private String url;
}
