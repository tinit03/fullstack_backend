package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for communication of data layer for user.
 *
 * @author Harry Xu
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user from a given email.
   *
   * @param email Email
   * @return Optional email
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a user exits by email.
   *
   * @param email The mail of the user.
   * @return True, if exits, else returns false.
   */
  boolean existsByEmail(String email);
}
