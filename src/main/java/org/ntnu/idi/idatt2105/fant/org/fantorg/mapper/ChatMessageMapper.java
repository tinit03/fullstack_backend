package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.time.LocalDateTime;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;

public class ChatMessageMapper {

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


    public static ChatMessage toEntity(ChatMessageCreateDto chatMessageDto, User sender, User recipient, Item item, String chatId) {
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
