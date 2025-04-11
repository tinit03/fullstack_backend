package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.time.LocalDateTime;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;

/**
 * Utility class for converting between ChatMessage entities and their corresponding DTOs.
 *
 * <p>The ChatMessageMapper class provides methods to map ChatMessage entities to ChatMessageDto
 * objects and vice versa. It also handles the conversion between the ChatMessageCreateDto and
 * ChatMessage entities.
 */
public class ChatMessageMapper {

  /**
   * Converts a ChatMessage entity to its corresponding ChatMessageDto.
   *
   * <p>This method maps the relevant fields from the ChatMessage entity, including sender and
   * recipient emails, item ID, chat ID, message content, timestamp, and message type.
   *
   * @param chatMessage The ChatMessage entity to be converted.
   * @return The ChatMessageDto containing message details.
   */
  public static ChatMessageDto toDto(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .senderId(chatMessage.getSender().getEmail())
        .recipientId(chatMessage.getRecipient().getEmail())
        .itemId(chatMessage.getItem().getItemId())
        .chatId(chatMessage.getChatId())
        .content(chatMessage.getContent())
        .timestamp(chatMessage.getTimestamp())
        .type(chatMessage.getType() == null ? MessageType.NORMAL : chatMessage.getType())
        .build();
  }

  /**
   * Converts a ChatMessageCreateDto to a ChatMessage entity.
   *
   * <p>This method creates a new ChatMessage entity using the provided ChatMessageCreateDto,
   * sender, recipient, item, and chatId. It sets the message timestamp to the current time and the
   * message type to 'NORMAL' if not provided in the DTO.
   *
   * @param chatMessageDto The ChatMessageCreateDto containing the content and message type.
   * @param sender The User entity representing the sender of the message.
   * @param recipient The User entity representing the recipient of the message.
   * @param item The Item entity associated with the chat message.
   * @param chatId The ID of the chat where the message is being sent.
   * @return The ChatMessage entity created from the provided data.
   */
  public static ChatMessage toEntity(
      ChatMessageCreateDto chatMessageDto, User sender, User recipient, Item item, String chatId) {
    return ChatMessage.builder()
        .sender(sender)
        .recipient(recipient)
        .item(item)
        .chatId(chatId)
        .content(chatMessageDto.getContent())
        .timestamp(LocalDateTime.now())
        .type(chatMessageDto.getType() == null ? MessageType.NORMAL : chatMessageDto.getType())
        .build();
  }
}
