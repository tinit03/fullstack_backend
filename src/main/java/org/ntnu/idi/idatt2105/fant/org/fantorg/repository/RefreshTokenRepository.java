package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link RefreshToken} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations for {@link RefreshToken} entities and additional
 * operations related to token management.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Finds a {@link RefreshToken} by its token.
   *
   * @param token the refresh token.
   * @return an {@link Optional} containing the {@link RefreshToken} if found, otherwise empty.
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Finds a {@link RefreshToken} associated with a specific user.
   *
   * @param user the user associated with the refresh token.
   * @return an {@link Optional} containing the {@link RefreshToken} if found, otherwise empty.
   */
  Optional<RefreshToken> findByUser(User user);

  /**
   * Deletes the {@link RefreshToken} associated with a specific user.
   *
   * @param user the user whose refresh token should be deleted.
   */
  void deleteByUser(User user);

  /**
   * Deletes all {@link RefreshToken} entities that have expired before the specified date.
   *
   * @param localDateTime the expiration date; refresh tokens before this time will be deleted.
   */
  void deleteAllByExpiryDateBefore(LocalDateTime localDateTime);
}
