package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public class ChatMessageMapper {

  public static ChatMessageDto toDto(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .id(chatMessage.getId())
        .senderId(chatMessage.getSender().getEmail())
        .recipientId(chatMessage.getRecipient().getEmail())
        .itemId(chatMessage.getItem().getItemId())
        .chatId(chatMessage.getChatId())
        .content(chatMessage.getContent())
        .timestamp(chatMessage.getTimestamp())
        .build();
  }


    public static ChatMessage toEntity(ChatMessageCreateDto chatMessageDto, User sender, User recipient, Item item, String chatId) {
    return ChatMessage.builder()
        .sender(sender)
        .recipient(recipient)
        .item(item)
        .chatId(chatId)
        .content(chatMessageDto.getContent())
        .timestamp(chatMessageDto.getTimestamp())
        .build();
  }
}
