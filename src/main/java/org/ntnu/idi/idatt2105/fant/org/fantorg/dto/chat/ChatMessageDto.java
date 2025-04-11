package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;

/**
 * Data Transfer Object (DTO) for representing a chat message.
 * <p>
 * This DTO encapsulates the details of a chat message, including sender, recipient,
 * item, content, timestamp, and message type.
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
public class ChatMessageDto {

  /**
   * The identifier of the sender of the message.
   * <p>
   * This field represents the sender's user ID.
   * </p>
   */
  private String senderId;

  /**
   * The identifier of the recipient of the message.
   * <p>
   * This field represents the recipient's user ID.
   * </p>
   */
  private String recipientId;

  /**
   * The unique identifier of the item related to the message.
   * <p>
   * This field represents the ID of the item being discussed or associated with the chat message.
   * </p>
   */
  private Long itemId;

  /**
   * The unique identifier of the chat session.
   * <p>
   * This field represents the chat session that the message belongs to.
   * </p>
   */
  private String chatId;

  /**
   * The content of the chat message.
   * <p>
   * This field represents the actual message content being sent.
   * </p>
   */
  private String content;

  /**
   * The timestamp of when the message was sent.
   * <p>
   * This field represents the date and time when the message was created and sent.
   * </p>
   */
  private LocalDateTime timestamp;

  /**
   * The type of the message, represented by an enum {@link MessageType}.
   * <p>
   * This field indicates the type of the message (e.g., text, image, etc.).
   * </p>
   */
  private MessageType type;
}
