package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for editing image details.
 * <p>
 * This DTO is used to represent the details that can be modified for an image,
 * including its URL, caption, and public identifier.
 * </p>
 */
@Getter
@Setter
public class ImageEditDto {

  /**
   * The URL of the image.
   * <p>
   * This field contains the URL pointing to the image resource. It can be updated
   * if the image URL needs to be changed.
   * </p>
   */
  private String url;

  /**
   * The caption for the image.
   * <p>
   * This field holds a textual description or caption that provides additional context
   * about the image.
   * </p>
   */
  private String caption;

  /**
   * The public identifier of the image.
   * <p>
   * This field holds the unique identifier assigned to the image in the cloud storage
   * system, used for image retrieval or deletion operations.
   * </p>
   */
  private String publicId;
}
