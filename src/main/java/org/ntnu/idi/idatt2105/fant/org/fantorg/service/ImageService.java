package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.io.IOException;
import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;

public interface ImageService {
  ImageItemDto saveImage(ImageItemUploadDto dto, Long itemId);
  List<ImageItemDto> getImagesByItemId(Long itemId);
  Image updateImage(String url, Image currentImage);

  void deleteImage(Image image) throws IOException;
}
