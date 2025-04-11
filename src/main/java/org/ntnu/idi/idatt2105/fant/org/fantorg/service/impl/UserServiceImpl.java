package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.UserMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.RefreshTokenService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ReviewService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link UserService} interface. Handles user management logic such as
 * fetching users, updating profile pictures, changing passwords, and deleting user accounts.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final ImageService imageService;
  private final PasswordEncoder passwordEncoder;

  private final ReviewService reviewService;

  private final RefreshTokenService refreshTokenService;

  /**
   * Finds a user by their email address.
   *
   * @param email the user's email
   * @return the {@link User} object
   * @throws UserNotFoundException if user is not found
   */
  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
  }

  /**
   * Finds a user by their ID and returns their DTO.
   *
   * @param id the user ID
   * @return the {@link UserDto}
   * @throws UserNotFoundException if user is not found
   */
  @Override
  public UserDto findById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    return mapUser(user);
  }

  /**
   * Retrieves all users and maps them to DTOs.
   *
   * @return list of {@link UserDto}
   */
  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    return users.stream().map(this::mapUser).collect(Collectors.toList());
  }

  /**
   * Updates the user's profile picture with a new image from the provided URL.
   *
   * @param url the new image URL
   * @param user the user whose profile picture is being updated
   * @return the updated {@link User}
   */
  @Override
  public User updateProfilePicture(String url, User user) {
    Image currentImage = user.getProfileImage();
    if (((url == null || url.isBlank()) || !url.startsWith("http"))) {
      user.setProfileImage(null);
      userRepository.save(user);
    }
    Image newProfileImage = imageService.updateImage(url, currentImage);
    user.setProfileImage(newProfileImage);
    return userRepository.save(user);
  }

  /**
   * Updates the user's password after verifying the current password.
   *
   * @param user the user
   * @param dto the DTO containing the current and new password
   * @throws IllegalArgumentException if current password is incorrect or new password is the same
   */
  @Override
  public void updatePassword(User user, UpdatePasswordDto dto) {
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }
    if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
      throw new IllegalArgumentException("New password cannot be the same as the old!");
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
  }

  /**
   * Deletes a user account, revokes refresh tokens and removes profile image if present.
   *
   * @param userId the ID of the user to delete
   * @throws UserNotFoundException if user is not found
   * @throws IllegalArgumentException if image deletion fails
   */
  @Override
  @Transactional
  public void deleteUser(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

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

  /**
   * Maps a {@link User} entity to a {@link UserDto}, including review statistics.
   *
   * @param user the user to map
   * @return the mapped DTO
   */
  public UserDto mapUser(User user) {
    UserDto dto = UserMapper.toDto(user);
    dto.setAverageRating(reviewService.getAverageRatingForSeller(user));
    dto.setTotalReviews(reviewService.getReviewCountForSeller(user));
    return dto;
  }

  /**
   * Retrieves chat-specific profile information for a user by email.
   *
   * @param email the user's email
   * @return {@link ChatProfileDto}
   * @throws UserNotFoundException if user is not found
   */
  public ChatProfileDto findChatProfile(String email) {
    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    return UserMapper.toChatProfileDto(user);
  }
}
