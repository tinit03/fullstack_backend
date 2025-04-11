package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;

/**
 * Represents a notification in the system.
 *
 * <p>A notification is sent to a user to inform them about a certain event, such as a new bid, item
 * sold, or message received. It includes details like the notification type, any arguments related
 * to the notification, whether the notification has been read, and a link (optional) to an item,
 * chat, or other resources.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

  /** The unique identifier of the notification. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The user who is the recipient of the notification. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id", nullable = false)
  private User recipient;

  /** The type of the notification, indicating what event triggered it. */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  /**
   * A map of arguments related to the notification. The map contains key-value pairs that provide
   * additional context or data related to the notification.
   */
  @ElementCollection
  @CollectionTable(name = "notification_args", joinColumns = @JoinColumn(name = "notification_id"))
  @MapKeyColumn(name = "arg_key")
  @Column(name = "arg_value")
  private Map<String, String> args = new HashMap<>();

  /**
   * Indicates whether the notification has been read by the recipient. By default, a notification
   * is marked as unread.
   */
  @Column(nullable = false)
  private boolean isRead = false;

  /**
   * An optional link that can be used to reference an item, chat, or other related resource. This
   * link allows users to directly navigate to the relevant content.
   */
  private String link;

  /**
   * The timestamp indicating when the notification was created. It is set to the current time by
   * default.
   */
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
