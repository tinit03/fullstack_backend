package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;

/**
 * DTO representing a notification.
 *
 * <p>This class is used to transfer information about a notification, including its type,
 * arguments, creation timestamp, and its read status. The notification is linked to a particular
 * event or action in the system that needs to be communicated to the user.
 */
@Getter
@Setter
public class NotificationDto {

  /**
   * The unique identifier for the notification.
   *
   * <p>This ID helps to uniquely identify a notification within the system.
   */
  private Long id;

  /**
   * The type of the notification.
   *
   * <p>This field specifies the type of the notification using a value from the {@link
   * NotificationType} enum. The type indicates the kind of event or action that triggered the
   * notification.
   */
  private NotificationType type;

  /**
   * A map of key-value pairs representing arguments related to the notification.
   *
   * <p>This can be used to store dynamic data relevant to the notification (e.g., user names, item
   * names, etc.). The keys represent the argument names and the values represent the corresponding
   * data.
   */
  private Map<String, String> args;

  /**
   * A link associated with the notification.
   *
   * <p>This link could direct the user to a relevant page or resource, for example, a page related
   * to the action that triggered the notification.
   */
  private String link;

  /**
   * A flag indicating whether the notification has been read by the user.
   *
   * <p>If {@code true}, the notification has been read; if {@code false}, it is unread.
   */
  private boolean isRead;

  /**
   * The timestamp when the notification was created.
   *
   * <p>This indicates when the notification was generated and is used to track when the event
   * occurred.
   */
  private LocalDateTime createdAt;
}
