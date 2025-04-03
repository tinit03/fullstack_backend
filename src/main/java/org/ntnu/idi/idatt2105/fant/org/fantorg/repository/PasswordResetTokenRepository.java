package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByToken(String token);

  Optional<PasswordResetToken> findByUser(User user);

  void deleteByUser(User user);
}