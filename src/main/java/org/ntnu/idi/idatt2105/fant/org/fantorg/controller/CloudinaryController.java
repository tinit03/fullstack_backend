package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController()
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class CloudinaryController {
  private final CloudinaryService cloudinaryService;

//  @PostMapping("/upload")
//  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
//    try {
//      String url = cloudinaryService.uploadImage(file);
//      return ResponseEntity.ok(url);
//    } catch (IllegalArgumentException e) {
//      // Validation error (e.g., file too big or wrong type)
//      return ResponseEntity.badRequest().body(e.getMessage());
//    } catch (IOException e) {
//      // IO error during upload
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//          .body("Upload failed: " + e.getMessage());
//    }
//  }
}
