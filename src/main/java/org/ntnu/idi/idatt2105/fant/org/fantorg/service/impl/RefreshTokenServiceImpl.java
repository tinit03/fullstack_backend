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

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
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

  @Override
  public RefreshToken getByToken(String token) {
    return refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new EntityNotFoundException("Invalid refresh token"));
  }

  @Override
  @Transactional
  public RefreshToken validateToken(String token) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new EntityNotFoundException("Token not found"));
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new IllegalArgumentException("Refresh token is expired");
    } else return refreshToken;
  }

  @Override
  public void revokeToken(User user) {
    refreshTokenRepository.deleteByUser(user);
    refreshTokenRepository.flush();
  }

  @Scheduled(cron = "0 0 3 * * ?") // Every day at 3AM
  public void purgeExpiredTokens() {
    refreshTokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
  }
}
