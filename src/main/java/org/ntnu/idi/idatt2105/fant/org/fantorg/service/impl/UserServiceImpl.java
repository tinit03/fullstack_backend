package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ImageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.UserMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ReviewService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final CloudinaryService cloudinaryService;
  private final PasswordEncoder passwordEncoder;
  private final ImageRepository imageRepository;

  private final ReviewService reviewService;
  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
  }

  @Override
  public UserDto findById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    return mapUser(user);
  }

  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    return users.stream().map(this::mapUser).collect(Collectors.toList());
  }

  @Override
  public User updateProfilePicture(String url, User user) {
    try{
      if (url == null || url.isBlank()) {
        Image oldImage = user.getProfileImage();
        if (oldImage != null) {
          String oldPublicId = oldImage.getPublicId();
          if (oldPublicId != null && !oldPublicId.isBlank()) {
            cloudinaryService.deleteImage(oldPublicId);
          }
          user.setProfileImage(null);
          userRepository.save(user);
          imageRepository.delete(oldImage);
        }
        return user;
      }
      if (url.startsWith("http://") || url.startsWith("https://")) return user;
      if (user.getProfileImage() != null) {
        Image oldImage = user.getProfileImage();
        String oldPublicId = user.getProfileImage().getPublicId();
        if (oldPublicId != null && !oldPublicId.isBlank()) {
          cloudinaryService.deleteImage(oldPublicId);
        }
        user.setProfileImage(null);
        userRepository.save(user);
        imageRepository.delete(oldImage);
      }

      Map<String, String> uploadResult = cloudinaryService.uploadBase64Image(url);
      ImageProfileDto profileDto = new ImageProfileDto();
      profileDto.setUrl(uploadResult.get("url"));
      profileDto.setPublicId(uploadResult.get("public_id"));
      Image newProfileImage = ImageMapper.fromProfileDto(profileDto);
      imageRepository.save(newProfileImage);
      user.setProfileImage(newProfileImage);
      return userRepository.save(user);

    } catch (IOException e){
      throw new RuntimeException("Failed to upload profile picture", e);
    }

  }

  @Override
  public void updatePassword(User user, UpdatePasswordDto dto) {
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }
    if(passwordEncoder.matches(dto.getNewPassword(), user.getPassword())){
      throw new IllegalArgumentException("New password cannot be the same as the old!");
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
  }

  public UserDto mapUser(User user){
    UserDto dto = UserMapper.toDto(user);
    dto.setAverageRating(reviewService.getAverageRatingForSeller(user));
    dto.setTotalReviews(reviewService.getReviewCountForSeller(user));
    return dto;
  }


}
