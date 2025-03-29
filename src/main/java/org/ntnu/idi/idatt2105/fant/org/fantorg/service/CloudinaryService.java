package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

  private final Cloudinary cloudinary;
  private final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
  // Maximum allowed file size (in bytes) – here it should be 5MB
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

  // Allowed MIME types for images
  private static final List<String> ALLOWED_MIME_TYPES = List.of("image/png", "image/jpeg", "image/jpg");

//  public String uploadImage(MultipartFile file) throws IOException {
////    if (file.getSize() > MAX_FILE_SIZE) {
////      throw new IllegalArgumentException("File size exceeds maximum allowed limit of 5MB.");
////    }
////
////    String contentType = file.getContentType();
////    if (!ALLOWED_MIME_TYPES.contains(contentType)) {
////      throw new IllegalArgumentException("Invalid file type. Only PNG and JPG images are allowed.");
////    }
////    Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
////    return result.get("secure_url").toString();
////  }


  /**
   * Uploads a base64-encoded image string to Cloudinary.
   */
  public Map<String, String> uploadBase64Image(String base64Image) throws IOException {
    // Add MIME prefix if missing
    if (!base64Image.startsWith("data:image")) {
      base64Image = "data:image/png;base64," + base64Image; // default to PNG
    }

    Map<?, ?> result = cloudinary.uploader().upload(base64Image, ObjectUtils.emptyMap());
    logger.info("Cloudinary upload result: {}", result);
    return extractUploadResponse(result);
  }

  /**
   * Deletes an image from Cloudinary using its public ID.
   */
  public void deleteImage(String publicId) throws IOException {
    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
  }

  /**
   * Helper to extract useful fields from Cloudinary upload response.
   */
  private Map<String, String> extractUploadResponse(Map<?, ?> result) {
    Map<String, String> response = new HashMap<>();
    response.put("url", result.get("secure_url").toString());
    response.put("public_id", result.get("public_id").toString());
    logger.info("RESPONSE {}",response);
    return response;
  }
}