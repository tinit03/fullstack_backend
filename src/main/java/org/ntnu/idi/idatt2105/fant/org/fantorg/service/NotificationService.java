package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.Map;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for handling notifications to users. */
public interface NotificationService {

  /**
   * Sends a notification to a user.
   *
   * @param recipient The user receiving the notification.
   * @param args A map of arguments used to format the message.
   * @param type The type of the notification.
   * @param link An optional link related to the notification (e.g., to an item or chat).
   */
  void send(User recipient, Map<String, String> args, NotificationType type, String link);

  /**
   * Retrieves a paginated list of notifications for the user.
   *
   * @param user The user to retrieve notifications for.
   * @param pageable Pagination information.
   * @return A page of NotificationDto.
   */
  Page<NotificationDto> getNotifications(User user, Pageable pageable);

  /**
   * Marks a specific notification as read.
   *
   * @param notificationId The ID of the notification.
   * @param user The user marking the notification as read.
   */
  void markAsRead(Long notificationId, User user);

  /**
   * Deletes a specific notification.
   *
   * @param id The ID of the notification to delete.
   * @param user The user requesting deletion.
   */
  void deleteNotification(Long id, User user);

  /**
   * Deletes all notifications for a given user.
   *
   * @param user The user whose notifications should be deleted.
   */
  void deleteAll(User user);
}
