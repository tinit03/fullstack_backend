package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;

public interface ChatMessageService {


  ChatMessageDto save(ChatMessageCreateDto msgDto);

  List<ChatMessageDto> findChatMessages(String senderId, String recipientId, Long itemId);
}
