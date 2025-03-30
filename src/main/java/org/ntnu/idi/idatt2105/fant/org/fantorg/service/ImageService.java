package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;

public interface ImageService {
  ImageDto saveImage(ImageCreateDto dto, Long itemId);
  List<ImageDto> getImagesByItemId(Long itemId);
  void deleteImage(Long imageId);
}
