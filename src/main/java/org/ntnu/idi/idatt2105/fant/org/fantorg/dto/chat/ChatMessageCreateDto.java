package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;

/**
 * Data Transfer Object (DTO) for creating a chat message.
 *
 * <p>This DTO is used to encapsulate the data required for sending a chat message, including
 * sender, recipient, item, message content, and the type of the message.
 *
 * @author Harry Xu
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessageCreateDto {

  /**
   * The identifier of the sender of the message.
   *
   * <p>This field is required and cannot be null.
   */
  @NotNull(message = "Sender ID is required")
  private String senderId;

  /**
   * The identifier of the recipient of the message.
   *
   * <p>This field is required and cannot be null.
   */
  @NotNull(message = "Recipient ID is required")
  private String recipientId;

  /**
   * The unique identifier of the item related to the message.
   *
   * <p>This field is required and cannot be null.
   */
  @NotNull(message = "Item ID is required")
  private Long itemId;

  /**
   * The content of the chat message.
   *
   * <p>The message must not exceed 200 characters.
   */
  @Size(max = 200, message = "Message cannot be longer than 200 characters")
  private String content;

  /**
   * The type of the message, represented by an enum {@link MessageType}.
   *
   * <p>The type can indicate whether the message is a text, image, or other types.
   */
  private MessageType type;
}
