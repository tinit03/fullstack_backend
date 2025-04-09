package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ImageServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private CloudinaryService cloudinaryService;

  @InjectMocks
  private ImageServiceImpl imageService;

  private Item sampleItem;
  private Image sampleImage; // used to simulate an existing image

  @BeforeEach
  public void setUp() {
    sampleItem = new Item();
    ReflectionTestUtils.setField(sampleItem, "itemId", 10L);
    sampleItem.setTitle("Test Item");

    sampleImage = new Image();
    sampleImage.setUrl("https://old.example.com/old.jpg");
    sampleImage.setPublicId("old_public_id");
  }

  @Test
  public void testSaveImage_Success() {
    ImageItemUploadDto dto = new ImageItemUploadDto();
    dto.setUrl("https://example.com/new.jpg");
    dto.setCaption("New Image");

    // When itemRepository.findById is called, return sampleItem.
    when(itemRepository.findById(10L)).thenReturn(java.util.Optional.of(sampleItem));

    // Simulate saving image and then return the saved image.
    when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> {
      Image image = invocation.getArgument(0);
      ReflectionTestUtils.setField(image, "id", 100L);
      return image;
    });

    // Call saveImage.
    ImageItemDto result = imageService.saveImage(dto, 10L);

    // Verify the image was linked to the sample item.
    assertThat(result).isNotNull();
    assertThat(result.getUrl()).isEqualTo("https://example.com/new.jpg");
    // Verify that itemRepository.findById and imageRepository.save were called.
    verify(itemRepository, times(1)).findById(10L);
    verify(imageRepository, times(1)).save(any(Image.class));
  }

  @Test
  public void testGetImagesByItemId_Success() {
    //Ensure item exists.
    when(itemRepository.existsById(10L)).thenReturn(true);
    // Create a list of images.
    List<Image> images = Collections.singletonList(sampleImage);
    when(imageRepository.findByItem_ItemId(10L)).thenReturn(images);

    List<ImageItemDto> result = imageService.getImagesByItemId(10L);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUrl()).isEqualTo("https://old.example.com/old.jpg");
  }

  @Test
  public void testGetImagesByItemId_ItemNotFound() {
    when(itemRepository.existsById(10L)).thenReturn(false);
    assertThrows(EntityNotFoundException.class, () -> {
      imageService.getImagesByItemId(10L);
    });
  }

  @Test
  public void testDeleteImage_Success() throws IOException {
    // We don't throw exception when image is not null
    // Stub cloudinaryService.deleteImage to do nothing
    doNothing().when(cloudinaryService).deleteImage("old_public_id");
    // Call deleteImage
    imageService.deleteImage(sampleImage);
    // Verify that cloudinaryService.deleteImage and imageRepository.delete were called once
    verify(cloudinaryService, times(1)).deleteImage("old_public_id");
    verify(imageRepository, times(1)).delete(sampleImage);
  }

  @Test
  public void testUpdateImage_NullUrl_CallsDelete() throws IOException {
    // When URL is null, then deleteImage should be called
    // In this test, the current image is sampleImage
    // Stub: doNothing on deleteImage.
    doNothing().when(cloudinaryService).deleteImage(any());
    // Call updateImage with null url
    Image result = imageService.updateImage(null, sampleImage);
    // Should return null
    assertThat(result).isNull();
    // Verify deleteImage was called.
    verify(cloudinaryService, never()).uploadBase64Image(any());
  }

  @Test
  public void testUpdateImage_HttpUrl_NoUpdate() throws IOException {
    String httpUrl = "https://example.com/already.jpg";
    Image result = imageService.updateImage(httpUrl, sampleImage);
    // In this case, no update should be performed – current image is returned.
    assertThat(result).isEqualTo(sampleImage);
    verify(cloudinaryService, never()).uploadBase64Image(any());
  }

  @Test
  public void testUpdateImage_Base64Url_Update() throws IOException {
    // For this test, we simulate that the current image is being replaced
    String base64Url = "data:image/png;base64,abc123";
    // Stub cloudinaryService.uploadBase64Image to return desired upload result
    Map<String, String> uploadResult = Map.of(
        "url", "https://example.com/new.jpg",
        "public_id", "new_public_id"
    );
    when(cloudinaryService.uploadBase64Image(base64Url)).thenReturn(uploadResult);
    // imageRepository.save should save and return the new image.
    when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));
    // Stub so deleteImage does nothing so we simulate deletion
    doNothing().when(cloudinaryService).deleteImage("old_public_id");
    // Call updateImage
    Image result = imageService.updateImage(base64Url, sampleImage);
    // Verify the result image contains updated fields
    assertThat(result.getUrl()).isEqualTo("https://example.com/new.jpg");
    assertThat(result.getPublicId()).isEqualTo("new_public_id");
    // Verify that deleteImage was called on the current image
    verify(cloudinaryService, times(1)).deleteImage("old_public_id");
  }
}
