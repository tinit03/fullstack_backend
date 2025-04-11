package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ForgotPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ResetPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.PasswordResetTokenRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.PasswordResetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PasswordResetService}.
 *
 * <p>Handles the creation, validation, and usage of password reset tokens, as well as updating user
 * passwords.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

  private final PasswordResetTokenRepository tokenRepository;
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  /**
   * Creates a reset token for a user based on the provided email. Deletes any existing token for
   * the same user.
   *
   * @param dto DTO containing the email address.
   * @return The created {@link PasswordResetToken}.
   * @throws UserNotFoundException If no user exists with the provided email.
   */
  @Override
  public PasswordResetToken createTokenForUser(ForgotPasswordDto dto) {
    User user =
        userRepository
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User is not found!"));
    // Delete existing token, if user requests twice
    tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
    PasswordResetToken token = new PasswordResetToken();
    token.setToken(UUID.randomUUID().toString());
    token.setUser(user);
    token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
    return tokenRepository.save(token);
  }

  /**
   * Resets a user's password after validating the token and input.
   *
   * @param dto DTO containing the token, email, and new password.
   * @throws IllegalArgumentException If the new password is the same as the current one.
   */
  @Override
  public void resetPassword(ResetPasswordDto dto) {
    validateToken(dto.getToken(), dto.getEmail());
    User user = getUserByToken(dto.getToken());
    if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
      throw new IllegalArgumentException(
          "New password must be different from the current password.");
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
    markTokenAsUsed(dto.getToken());
  }

  /**
   * Validates the given token against expiry, usage, and user email.
   *
   * @param token The reset token string.
   * @param email The email address of the user trying to reset the password.
   * @throws IllegalArgumentException If the token is invalid.
   * @throws IllegalStateException If the token has expired or is already used.
   * @throws SecurityException If the token does not match the provided email.
   */
  @Override
  public void validateToken(String token, String email) {
    PasswordResetToken resetToken =
        tokenRepository
            .findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
    if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Token has expired");
    }
    if (resetToken.isUsed()) {
      throw new IllegalStateException("Token has already been used");
    }
    if (!resetToken.getUser().getEmail().equalsIgnoreCase(email)) {
      throw new SecurityException("Token does not belong to the specified user");
    }
  }

  /**
   * Retrieves a user associated with the given token.
   *
   * @param token The reset token.
   * @return The associated {@link User}.
   * @throws EntityNotFoundException If the token is invalid or expired.
   */
  @Override
  public User getUserByToken(String token) {
    return tokenRepository
        .findByToken(token)
        .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
        .map(PasswordResetToken::getUser)
        .orElseThrow(() -> new EntityNotFoundException("Invalid or expired token"));
  }

  /**
   * Invalidates a token by removing it from the repository.
   *
   * @param token The token to invalidate.
   */
  @Override
  public void invalidateToken(String token) {
    tokenRepository.findByToken(token).ifPresent(tokenRepository::delete);
  }

  /**
   * Marks a token as used to prevent reuse.
   *
   * @param token The token to mark as used.
   * @throws IllegalArgumentException If the token is invalid.
   */
  @Override
  public void markTokenAsUsed(String token) {
    PasswordResetToken resetToken =
        tokenRepository
            .findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
    resetToken.setUsed(true);
    tokenRepository.save(resetToken);
  }
}
