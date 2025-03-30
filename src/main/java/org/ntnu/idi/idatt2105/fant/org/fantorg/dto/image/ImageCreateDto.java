package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageCreateDto {
  private String base64Url;
  private String caption;
}
