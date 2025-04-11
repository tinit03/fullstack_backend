package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Entity representing a chat room, which stores the context of an ongoing conversation between two users.
 * <p>
 * This entity maps to the "Chat_room" table in the database and holds details about the conversation:
 * <ul>
 *   <li><b>sender</b>: The user who initiated the conversation.</li>
 *   <li><b>recipient</b>: The user who is the recipient of the conversation.</li>
 *   <li><b>item</b>: The item associated with the chat, if applicable.</li>
 *   <li><b>lastEntry</b>: The timestamp of the last message exchanged in this chat room.</li>
 *   <li><b>chatId</b>: The unique identifier for the chat session, used to group all related messages in the chat room.</li>
 * </ul>
 * </p>
 */
@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "\"Chat_room\"")
@ToString
public class ChatRoom {

  /**
   * The unique identifier of the chat room.
   * It is auto-generated.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who initiated the conversation (sender).
   * This is a many-to-one relationship with the {@link User} entity.
   */
  @ManyToOne
  private User sender;

  /**
   * The user who is the recipient of the conversation.
   * This is a many-to-one relationship with the {@link User} entity.
   */
  @ManyToOne
  private User recipient;

  /**
   * The item associated with the chat, if applicable.
   * This is a many-to-one relationship with the {@link Item} entity.
   */
  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  /**
   * The timestamp of the last entry or message exchanged in the chat room.
   */
  private LocalDateTime lastEntry;

  /**
   * The unique identifier for the chat session.
   * This identifier is used to group all messages that belong to this particular conversation.
   */
  private String chatId;
}
