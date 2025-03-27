package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

  private final Cloudinary cloudinary;

  // Maximum allowed file size (in bytes) – here it should be 5MB
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

  // Allowed MIME types for images
  private static final List<String> ALLOWED_MIME_TYPES = List.of("image/png", "image/jpeg", "image/jpg");

  public String uploadImage(MultipartFile file) throws IOException {
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("File size exceeds maximum allowed limit of 5MB.");
    }

    String contentType = file.getContentType();
    if (!ALLOWED_MIME_TYPES.contains(contentType)) {
      throw new IllegalArgumentException("Invalid file type. Only PNG and JPG images are allowed.");
    }

    Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    return result.get("secure_url").toString();
  }
}