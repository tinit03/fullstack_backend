package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Notification} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations for {@link Notification} and custom queries for
 * notifications associated with a user.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  /**
   * Finds a page of {@link Notification} records by recipient. This method retrieves notifications
   * sent to the specified recipient, with pagination support.
   *
   * @param recipient the user who is the recipient of the notifications
   * @param pageable the pagination information
   * @return a page of {@link Notification} records sent to the specified recipient
   */
  Page<Notification> findByRecipient(User recipient, Pageable pageable);

  /**
   * Finds a list of {@link Notification} records by recipient. This method retrieves all
   * notifications sent to the specified recipient without pagination.
   *
   * @param recipient the user who is the recipient of the notifications
   * @return a list of {@link Notification} records sent to the specified recipient
   */
  List<Notification> findByRecipient(User recipient);
}
