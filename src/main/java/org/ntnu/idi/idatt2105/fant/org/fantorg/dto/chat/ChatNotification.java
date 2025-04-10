package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for representing a chat notification.
 * <p>
 * This DTO encapsulates the details of a chat notification, including sender, recipient,
 * item, content, and the timestamp of when the notification was generated.
 * </p>
 *
 * @author Harry Xu
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatNotification {

  /**
   * The identifier of the sender of the notification.
   * <p>
   * This field represents the sender's user ID.
   * </p>
   */
  private String senderId;

  /**
   * The identifier of the recipient of the notification.
   * <p>
   * This field represents the recipient's user ID.
   * </p>
   */
  private String recipientId;

  /**
   * The unique identifier of the item related to the notification.
   * <p>
   * This field represents the ID of the item associated with the notification.
   * </p>
   */
  private Long itemId;

  /**
   * The content of the chat notification.
   * <p>
   * This field represents the message or notification content being sent.
   * </p>
   */
  private String content;

  /**
   * The timestamp of when the notification was generated.
   * <p>
   * This field represents the date and time when the notification was created.
   * </p>
   */
  private LocalDateTime timestamp;
}
