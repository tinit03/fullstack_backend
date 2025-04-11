package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for uploading an image associated with an item.
 * <p>
 * This DTO is used to transfer the image's details (such as its URL and caption)
 * when the image is being uploaded for an item in the application.
 * </p>
 */
@Getter
@Setter
public class ImageItemUploadDto {

  /**
   * The URL of the image.
   * <p>
   * This field contains the URL where the image is hosted. It is used to reference the image
   * when uploading or displaying it in the application.
   * </p>
   */
  private String url;

  /**
   * The caption for the image.
   * <p>
   * This field holds a textual description or caption for the image, providing additional context or
   * description related to the image during the upload process.
   * </p>
   */
  private String caption;
}
