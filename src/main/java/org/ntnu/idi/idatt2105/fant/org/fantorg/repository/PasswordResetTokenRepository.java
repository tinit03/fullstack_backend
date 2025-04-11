package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link PasswordResetToken} entities. This interface extends
 * {@link JpaRepository} to provide basic CRUD operations for {@link PasswordResetToken} entities,
 * including token-based operations for password reset functionality.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  /**
   * Finds a {@link PasswordResetToken} by its token.
   *
   * @param token the reset token.
   * @return an {@link Optional} containing the {@link PasswordResetToken} if found, otherwise
   *     empty.
   */
  Optional<PasswordResetToken> findByToken(String token);

  /**
   * Finds a {@link PasswordResetToken} associated with a specific user.
   *
   * @param user the user associated with the token.
   * @return an {@link Optional} containing the {@link PasswordResetToken} if found, otherwise
   *     empty.
   */
  Optional<PasswordResetToken> findByUser(User user);

  /**
   * Deletes a {@link PasswordResetToken} associated with a specific user.
   *
   * @param user the user whose password reset token should be deleted.
   */
  void deleteByUser(User user);
}
