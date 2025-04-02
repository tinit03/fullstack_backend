package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.Optional;
import java.util.Set;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for communication of data layer for user.
 *
 * @author Harry Xu
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user from a given email
     * @param email Email
     * @return Optional email
     */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    @Query("SELECT i.itemId FROM User u JOIN u.bookmarkedItems i WHERE u.id = :userId")
    Set<Long> findBookmarkedItemIds(@Param("userId") Long userId);
}
