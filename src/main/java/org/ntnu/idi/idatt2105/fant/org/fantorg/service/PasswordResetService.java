package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ForgotPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ResetPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/** Service for handling password reset functionality. */
public interface PasswordResetService {

  /**
   * Creates a password reset token for the user based on the provided DTO.
   *
   * @param dto The DTO containing the user's email.
   * @return A new PasswordResetToken object.
   */
  PasswordResetToken createTokenForUser(ForgotPasswordDto dto);

  /**
   * Resets the user's password using the provided token and new password.
   *
   * @param dto The DTO containing the reset token, email, and new password.
   */
  void resetPassword(ResetPasswordDto dto);

  /**
   * Validates that a given token is valid and matches the provided email.
   *
   * @param token The password reset token.
   * @param email The email address associated with the token.
   */
  void validateToken(String token, String email);

  /**
   * Retrieves the user associated with a given password reset token.
   *
   * @param token The reset token.
   * @return The user associated with the token.
   */
  User getUserByToken(String token);

  /**
   * Invalidates the given token (e.g., after expiration or manual revocation).
   *
   * @param token The token to invalidate.
   */
  void invalidateToken(String token);

  /**
   * Marks the token as used to prevent reuse.
   *
   * @param token The token to mark.
   */
  void markTokenAsUsed(String token);
}
