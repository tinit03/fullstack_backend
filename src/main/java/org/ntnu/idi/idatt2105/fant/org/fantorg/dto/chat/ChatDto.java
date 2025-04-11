package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

/**
 * Data Transfer Object (DTO) for representing a chat.
 *
 * <p>This DTO is used to encapsulate the data related to a chat, including the last message, the
 * sender, recipient, item details, status, and profile image details.
 *
 * @author Harry Xu
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatDto {

  /** The content of the last message sent in the chat. */
  private String lastMessageContent;

  /** The timestamp of the last message sent in the chat. */
  private LocalDateTime lastMessageTimestamp;

  /** The identifier of the last sender of the message. */
  private String lastSenderId;

  /** The identifier of the current sender of the message. */
  private String senderId;

  /** The identifier of the recipient of the message. */
  private String recipientId;

  /** The unique identifier of the item associated with the chat. */
  private long itemId;

  /** The status of the chat, represented by an enum {@link Status}. */
  private Status status;

  /**
   * The image associated with the chat (could be the profile picture of the sender or recipient).
   */
  private String image;

  /** The title of the item associated with the chat. */
  private String itemTitle;

  /** The profile picture of the recipient of the message. */
  private String recipientProfilePic;
}
