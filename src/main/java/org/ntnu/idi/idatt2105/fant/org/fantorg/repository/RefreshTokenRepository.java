package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  Optional<RefreshToken> findByUser(User user);
  void deleteByUser(User user);
  void deleteAllByExpiryDateBefore(LocalDateTime localDateTime);
}
