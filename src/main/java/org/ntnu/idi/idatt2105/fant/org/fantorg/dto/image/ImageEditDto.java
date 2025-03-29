package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageEditDto {
  private String base64Url;
  private String url;
  private String caption;
  private String publicId;
}
