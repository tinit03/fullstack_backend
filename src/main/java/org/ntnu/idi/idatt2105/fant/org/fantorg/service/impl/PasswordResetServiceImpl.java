package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Objects;
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

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

  private final PasswordResetTokenRepository tokenRepository;
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;
  @Override
  public PasswordResetToken createTokenForUser(ForgotPasswordDto dto) {
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new UserNotFoundException("User is not found!"));
    //Delete existing token, if user requests twice
    tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
    PasswordResetToken token = new PasswordResetToken();
    token.setToken(UUID.randomUUID().toString());
    token.setUser(user);
    token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
    return tokenRepository.save(token);
  }

  @Override
  public void resetPassword(ResetPasswordDto dto) {
    validateToken(dto.getToken(), dto.getEmail());
    User user = getUserByToken(dto.getToken());
    if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
      throw new IllegalArgumentException("New password must be different from the current password.");
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
    markTokenAsUsed(dto.getToken());
  }

  @Override
  public void validateToken(String token, String email) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token)
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

    @Override
    public User getUserByToken(String token) {
      return tokenRepository.findByToken(token)
          .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
          .map(PasswordResetToken::getUser)
          .orElseThrow(() -> new EntityNotFoundException("Invalid or expired token"));
    }

    @Override
    public void invalidateToken(String token) {
      tokenRepository.findByToken(token).ifPresent(tokenRepository::delete);
    }

  @Override
  public void markTokenAsUsed(String token) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
    resetToken.setUsed(true);
    tokenRepository.save(resetToken);
  }

}
