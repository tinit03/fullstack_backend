package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing image details.
 * <p>
 * This DTO encapsulates the details of an image, including its URL and public identifier.
 * </p>
 */
@Getter
@Setter
public class ImageDto {

  /**
   * The URL of the image.
   * <p>
   * This field contains the URL that points to the image resource.
   * </p>
   */
  private String url;

  /**
   * The public identifier of the image.
   * <p>
   * This field holds the unique identifier for the image in the cloud storage system.
   * </p>
   */
  private String publicId;
}
