package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;

/**
 * Entity representing a chat message exchanged between users.
 * <p>
 * This entity maps to the "Chat_message" table in the database and stores details about each chat message, including:
 * <ul>
 *   <li><b>sender</b>: The user who sent the message.</li>
 *   <li><b>recipient</b>: The user who is the recipient of the message.</li>
 *   <li><b>item</b>: The item related to the message (if applicable).</li>
 *   <li><b>content</b>: The text content of the message.</li>
 *   <li><b>chatId</b>: The identifier for the chat session this message belongs to.</li>
 *   <li><b>type</b>: The type of the message, which indicates the context or action associated with the message (e.g., normal message, bid, purchase, etc.).</li>
 *   <li><b>timestamp</b>: The timestamp when the message was sent.</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"Chat_message\"")
public class ChatMessage {

  /**
   * The unique identifier of the chat message.
   * It is auto-generated.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who sent the message.
   * This is a many-to-one relationship with the {@link User} entity.
   */
  @ManyToOne
  private User sender;

  /**
   * The user who is the recipient of the message.
   * This is a many-to-one relationship with the {@link User} entity.
   */
  @ManyToOne
  private User recipient;

  /**
   * The item related to the chat message, if applicable.
   * This is a many-to-one relationship with the {@link Item} entity.
   */
  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  /**
   * The content of the message.
   */
  private String content;

  /**
   * The identifier for the chat session this message belongs to.
   * This chatId groups messages into a single conversation or chat.
   */
  private String chatId;

  /**
   * The type of the message (e.g., normal, bid, purchase, etc.).
   * This is used to distinguish the nature or purpose of the message.
   */
  private MessageType type;

  /**
   * The timestamp when the message was sent.
   * This field is mandatory and cannot be null.
   */
  @NotNull
  @Column(nullable = false)
  private LocalDateTime timestamp;
}
