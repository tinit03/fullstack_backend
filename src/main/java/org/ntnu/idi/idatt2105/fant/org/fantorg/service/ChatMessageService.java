package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {


  ChatMessageDto save(ChatMessageCreateDto msgDto);

  Page<ChatMessageDto> findChatMessages(String senderId, String recipientId, Long itemId, Pageable pageable);

  ChatMessageDto saveNewBidMessage(ChatMessageCreateDto msgDto);
}
