package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.RefreshTokenService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ReviewService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.UserServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private RefreshTokenService refreshTokenService;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private ReviewService reviewService;

  @Mock private ImageService imageService;

  @InjectMocks private UserServiceImpl userService;

  private User user;
  private User anotherUser;

  @BeforeEach
  public void setUp() {
    user = new User();
    ReflectionTestUtils.setField(user, "id", 1L);
    user.setEmail("test@example.com");
    user.setFirstName("Test");
    user.setLastName("User");
    user.setPassword("hashedPassword");

    user.setRole(Role.USER);

    anotherUser = new User();
    ReflectionTestUtils.setField(anotherUser, "id", 2L);
    anotherUser.setEmail("other@example.com");
    anotherUser.setFirstName("Other");
    anotherUser.setLastName("User");
    anotherUser.setPassword("otherHashed");
    anotherUser.setRole(Role.USER);
  }

  @Test
  public void testFindByEmail_Success() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    User found = userService.findByEmail("test@example.com");

    assertThat(found).isEqualTo(user);
  }

  @Test
  public void testFindByEmail_NotFound() {
    when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
    assertThrows(
        UserNotFoundException.class, () -> userService.findByEmail("notfound@example.com"));
  }

  @Test
  public void testFindById_Success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(reviewService.getAverageRatingForSeller(user)).thenReturn(4.5);
    when(reviewService.getReviewCountForSeller(user)).thenReturn(10L);

    UserDto dto = userService.findById(1L);

    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getAverageRating()).isEqualTo(4.5);
    assertThat(dto.getTotalReviews()).isEqualTo(10L);
  }

  @Test
  public void testFindAll() {
    List<User> users = Arrays.asList(user, anotherUser);
    when(userRepository.findAll()).thenReturn(users);

    when(reviewService.getAverageRatingForSeller(any())).thenReturn(0.0);
    when(reviewService.getReviewCountForSeller(any())).thenReturn(0L);

    List<UserDto> dtos = userService.findAll();

    assertThat(dtos).hasSize(2);
  }

  @Test
  public void testUpdateProfilePicture_ValidUrl() throws IOException {

    Image currentImage = new Image();
    currentImage.setUrl("oldUrl");
    user.setProfileImage(currentImage);

    Image newImage = new Image();
    newImage.setUrl("https://new.example.com/image.jpg");
    when(imageService.updateImage("https://new.example.com/image.jpg", currentImage))
        .thenReturn(newImage);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User updated = userService.updateProfilePicture("https://new.example.com/image.jpg", user);

    assertThat(updated.getProfileImage().getUrl()).isEqualTo("https://new.example.com/image.jpg");
  }

  @Test
  public void testUpdatePassword_Success() {

    when(passwordEncoder.matches("currentPass", user.getPassword())).thenReturn(true);
    when(passwordEncoder.matches("newPass", user.getPassword())).thenReturn(false);
    when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UpdatePasswordDto dto = new UpdatePasswordDto();
    dto.setCurrentPassword("currentPass");
    dto.setNewPassword("newPass");

    userService.updatePassword(user, dto);

    assertThat(user.getPassword()).isEqualTo("hashedNewPass");
  }

  @Test
  public void testUpdatePassword_IncorrectCurrent() {
    when(passwordEncoder.matches("wrongPass", user.getPassword())).thenReturn(false);
    UpdatePasswordDto dto = new UpdatePasswordDto();
    dto.setCurrentPassword("wrongPass");
    dto.setNewPassword("newPass");

    assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(user, dto));
  }

  @Test
  public void testUpdatePassword_NewSameAsOld() {
    // If the new password, when matched, equals the current one.
    when(passwordEncoder.matches("currentPass", user.getPassword())).thenReturn(true);
    UpdatePasswordDto dto = new UpdatePasswordDto();
    dto.setCurrentPassword("currentPass");
    dto.setNewPassword("currentPass");

    assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(user, dto));
  }

  @Test
  public void testDeleteUser_Success() throws IOException {
    Image profileImage = new Image();
    profileImage.setUrl("https://example.com/image.jpg");
    user.setProfileImage(profileImage);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    doNothing().when(refreshTokenService).revokeToken(user);
    doNothing().when(imageService).deleteImage(profileImage);
    doNothing().when(userRepository).delete(user);

    userService.deleteUser(1L);

    verify(refreshTokenService).revokeToken(user);
    verify(imageService).deleteImage(profileImage);
    verify(userRepository).delete(user);
  }

  @Test
  public void testDeleteUser_NotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
  }
}
