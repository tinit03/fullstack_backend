package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing an image associated with an item.
 *
 * <p>This DTO is used to transfer the image's details, such as its URL, caption, and public
 * identifier. It is typically used when displaying images related to an item in the application.
 */
@Getter
@Setter
public class ImageItemDto {

  /**
   * The URL of the image.
   *
   * <p>This field contains the URL pointing to the image resource. It is used to retrieve and
   * display the image in the application.
   */
  private String url;

  /**
   * The caption for the image.
   *
   * <p>This field holds a textual description or caption for the image, providing additional
   * context or description related to the image.
   */
  private String caption;

  /**
   * The public identifier of the image.
   *
   * <p>This field contains the unique identifier assigned to the image in the cloud storage system.
   * It is used to manage and reference the image resource in the application.
   */
  private String publicId;
}
