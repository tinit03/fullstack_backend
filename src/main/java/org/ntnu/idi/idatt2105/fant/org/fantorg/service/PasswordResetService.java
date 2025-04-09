package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ForgotPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ResetPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;

public interface PasswordResetService {
  PasswordResetToken createTokenForUser(ForgotPasswordDto dto);

  void resetPassword(ResetPasswordDto dto);
  void validateToken(String token, String email);
  User getUserByToken(String token);
  void invalidateToken(String token);
  void markTokenAsUsed(String token);

}