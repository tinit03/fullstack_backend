package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ForgotPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ResetPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.PasswordResetTokenRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.PasswordResetServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceImplTest {

  @Mock private PasswordResetTokenRepository tokenRepository;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private PasswordResetServiceImpl passwordResetService;

  private User sampleUser;
  private PasswordResetToken sampleToken;

  @BeforeEach
  public void setUp() {
    sampleUser = new User();

    ReflectionTestUtils.setField(sampleUser, "id", 1L);
    sampleUser.setFirstName("John");
    sampleUser.setLastName("Doe");
    sampleUser.setEmail("john.doe@example.com");
    sampleUser.setPassword("old_hashed_password"); // raw value not used because we stub the encoder

    sampleToken = new PasswordResetToken();
    sampleToken.setToken(UUID.randomUUID().toString());
    sampleToken.setUser(sampleUser);
    sampleToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
    sampleToken.setUsed(false);
  }

  @Test
  public void testCreateTokenForUser_Success() {

    ForgotPasswordDto forgotDto = new ForgotPasswordDto();
    forgotDto.setEmail("john.doe@example.com");

    when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));

    when(tokenRepository.findByUser(sampleUser)).thenReturn(Optional.empty());

    when(tokenRepository.save(any(PasswordResetToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PasswordResetToken createdToken = passwordResetService.createTokenForUser(forgotDto);

    assertNotNull(createdToken.getToken());
    assertThat(createdToken.getUser()).isEqualTo(sampleUser);
    assertThat(createdToken.getExpiryDate()).isAfter(LocalDateTime.now());
    verify(tokenRepository).findByUser(sampleUser);
    verify(tokenRepository).save(any(PasswordResetToken.class));
  }

  @Test
  public void testCreateTokenForUser_UserNotFound() {
    ForgotPasswordDto forgotDto = new ForgotPasswordDto();
    forgotDto.setEmail("nonexistent@example.com");

    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class, () -> passwordResetService.createTokenForUser(forgotDto));
  }

  @Test
  public void testResetPassword_Success() {
    ResetPasswordDto resetDto = new ResetPasswordDto();
    resetDto.setToken(sampleToken.getToken());
    resetDto.setEmail("john.doe@example.com");
    resetDto.setNewPassword("new_secure_password");

    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));
    when(passwordEncoder.matches("new_secure_password", sampleUser.getPassword()))
        .thenReturn(false);
    when(passwordEncoder.encode("new_secure_password")).thenReturn("hashed_new_password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenRepository.save(any(PasswordResetToken.class)))
        .thenAnswer(
            invocation -> {
              PasswordResetToken t = invocation.getArgument(0);
              return t;
            });

    passwordResetService.resetPassword(resetDto);

    verify(userRepository).save(argThat(user -> "hashed_new_password".equals(user.getPassword())));
    verify(tokenRepository, times(3)).findByToken(sampleToken.getToken());
    verify(tokenRepository).save(argThat(token -> token.isUsed()));
  }

  @Test
  public void testResetPassword_NewPasswordSameAsOld() {
    ResetPasswordDto resetDto = new ResetPasswordDto();
    resetDto.setToken(sampleToken.getToken());
    resetDto.setEmail("john.doe@example.com");
    resetDto.setNewPassword("old_password");

    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));
    when(passwordEncoder.matches("old_password", sampleUser.getPassword())).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> passwordResetService.resetPassword(resetDto));
    assertThat(exception.getMessage()).contains("New password must be different");
  }

  @Test
  public void testValidateToken_Success() {

    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));

    assertDoesNotThrow(
        () -> passwordResetService.validateToken(sampleToken.getToken(), "john.doe@example.com"));
  }

  @Test
  public void testValidateToken_InvalidToken() {
    when(tokenRepository.findByToken("invalid_token")).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> passwordResetService.validateToken("invalid_token", "john.doe@example.com"));
    assertThat(exception.getMessage()).contains("Invalid reset token");
  }

  @Test
  public void testValidateToken_ExpiredToken() {
    PasswordResetToken expiredToken = new PasswordResetToken();
    expiredToken.setToken(UUID.randomUUID().toString());
    expiredToken.setUser(sampleUser);
    expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(5)); // expired 5 minutes ago
    expiredToken.setUsed(false);

    when(tokenRepository.findByToken(expiredToken.getToken()))
        .thenReturn(Optional.of(expiredToken));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () ->
                passwordResetService.validateToken(
                    expiredToken.getToken(), "john.doe@example.com"));
    assertThat(exception.getMessage()).contains("Token has expired");
  }

  @Test
  public void testValidateToken_TokenAlreadyUsed() {
    PasswordResetToken usedToken = new PasswordResetToken();
    usedToken.setToken(UUID.randomUUID().toString());
    usedToken.setUser(sampleUser);
    usedToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
    usedToken.setUsed(true);

    when(tokenRepository.findByToken(usedToken.getToken())).thenReturn(Optional.of(usedToken));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> passwordResetService.validateToken(usedToken.getToken(), "john.doe@example.com"));
    assertThat(exception.getMessage()).contains("Token has already been used");
  }

  @Test
  public void testValidateToken_WrongEmail() {

    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));

    SecurityException exception =
        assertThrows(
            SecurityException.class,
            () -> passwordResetService.validateToken(sampleToken.getToken(), "other@example.com"));
    assertThat(exception.getMessage()).contains("Token does not belong");
  }

  @Test
  public void testGetUserByToken_Success() {
    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));
    User user = passwordResetService.getUserByToken(sampleToken.getToken());
    assertThat(user).isEqualTo(sampleUser);
  }

  @Test
  public void testGetUserByToken_InvalidOrExpired() {
    // Given no token is found
    when(tokenRepository.findByToken("nonexistent")).thenReturn(Optional.empty());
    assertThrows(
        EntityNotFoundException.class, () -> passwordResetService.getUserByToken("nonexistent"));

    // Also simulate expired token
    PasswordResetToken expiredToken = new PasswordResetToken();
    expiredToken.setToken(UUID.randomUUID().toString());
    expiredToken.setUser(sampleUser);
    expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
    expiredToken.setUsed(false);
    when(tokenRepository.findByToken(expiredToken.getToken()))
        .thenReturn(Optional.of(expiredToken));
    assertThrows(
        EntityNotFoundException.class,
        () -> passwordResetService.getUserByToken(expiredToken.getToken()));
  }

  @Test
  public void testInvalidateToken() {
    // Given a valid token.
    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));
    passwordResetService.invalidateToken(sampleToken.getToken());
    verify(tokenRepository, times(1)).delete(sampleToken);
  }

  @Test
  public void testMarkTokenAsUsed_Success() {
    when(tokenRepository.findByToken(sampleToken.getToken())).thenReturn(Optional.of(sampleToken));
    when(tokenRepository.save(any(PasswordResetToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    passwordResetService.markTokenAsUsed(sampleToken.getToken());
    assertTrue(sampleToken.isUsed());
    verify(tokenRepository).save(sampleToken);
  }
}
