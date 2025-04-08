package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ImageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.UserMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.RefreshTokenRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.RefreshTokenService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ReviewService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final CloudinaryService cloudinaryService;
  private final ImageService imageService;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository tokenRepository;

  private final ReviewService reviewService;

  private final RefreshTokenService refreshTokenService;
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
    Image currentImage = user.getProfileImage();
    if(((url == null || url.isBlank()) || !url.startsWith("http"))){
      user.setProfileImage(null);
      userRepository.save(user);
    }
    Image newProfileImage = imageService.updateImage(url,currentImage);
    user.setProfileImage(newProfileImage);
    return userRepository.save(user);
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

  @Override
  @Transactional
  public void deleteUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    refreshTokenService.revokeToken(user);

    if (user.getProfileImage() != null) {
      try {
        imageService.deleteImage(user.getProfileImage());
      } catch (IOException ex) {
        throw new IllegalArgumentException("Failed to delete image", ex);
      }
    }
    userRepository.delete(user);
  }
  public UserDto mapUser(User user){
    UserDto dto = UserMapper.toDto(user);
    dto.setAverageRating(reviewService.getAverageRatingForSeller(user));
    dto.setTotalReviews(reviewService.getReviewCountForSeller(user));
    return dto;
  }

}
