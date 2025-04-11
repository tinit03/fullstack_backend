package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.RefreshTokenRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link RefreshTokenService}. Handles creation, validation, revocation, and
 * cleanup of refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  /**
   * Creates a new refresh token for the given user. If a token already exists for the user, it will
   * be overwritten with a new one.
   *
   * @param user The user for whom the refresh token is created.
   * @return The newly created or updated {@link RefreshToken}.
   */
  @Override
  @Transactional
  public RefreshToken createToken(User user) {
    Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

    if (existingTokenOpt.isPresent()) {
      RefreshToken existingToken = existingTokenOpt.get();
      existingToken.setToken(UUID.randomUUID().toString());
      existingToken.setExpiryDate(LocalDateTime.now().plusDays(7));
      existingToken.setUsed(false);
      return refreshTokenRepository.save(existingToken);
    } else {
      // Create and save a new refresh token if none exists
      RefreshToken token = new RefreshToken();
      token.setUser(user);
      token.setToken(UUID.randomUUID().toString());
      token.setExpiryDate(LocalDateTime.now().plusDays(7));
      return refreshTokenRepository.save(token);
    }
  }

  /**
   * Retrieves a refresh token by its token string.
   *
   * @param token The token string to search for.
   * @return The matching {@link RefreshToken}.
   * @throws EntityNotFoundException If the token does not exist.
   */
  @Override
  public RefreshToken getByToken(String token) {
    return refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new EntityNotFoundException("Invalid refresh token"));
  }

  /**
   * Validates a refresh token by checking its expiration. If the token is expired, it is deleted
   * and an exception is thrown.
   *
   * @param token The token string to validate.
   * @return The valid {@link RefreshToken}.
   * @throws EntityNotFoundException If the token is not found.
   * @throws IllegalArgumentException If the token is expired.
   */
  @Override
  @Transactional
  public RefreshToken validateToken(String token) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Token not found"));
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new IllegalArgumentException("Refresh token is expired");
    } else return refreshToken;
  }

  /**
   * Revokes all refresh tokens for the given user by deleting them.
   *
   * @param user The user whose tokens should be revoked.
   */
  @Override
  public void revokeToken(User user) {
    refreshTokenRepository.deleteByUser(user);
    refreshTokenRepository.flush();
  }

  /** Scheduled job that runs every day at 3 AM to delete all expired tokens. */
  @Scheduled(cron = "0 0 3 * * ?")
  public void purgeExpiredTokens() {
    refreshTokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
  }
}
