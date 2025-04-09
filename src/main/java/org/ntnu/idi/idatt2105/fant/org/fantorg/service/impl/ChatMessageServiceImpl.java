package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.chat.ChatRoomNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ChatMessageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatMessageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final UserServiceImpl userService;
  private final ChatRoomServiceImpl chatRoomService;
  private final ItemServiceImpl itemService;

  @Override
  public ChatMessageDto save(ChatMessageCreateDto msgDto) {
    //log.info("Getting chatId from ChatMessageServiceImpl");

    String chatId = chatRoomService
        .getChatRoomId(msgDto.getSenderId(), msgDto.getRecipientId(), msgDto.getItemId(), true)
        .orElseThrow(
            () -> new ChatRoomNotFoundException(msgDto.getSenderId() + " " + msgDto.getRecipientId() + " " + msgDto.getItemId()));


    User sender = userService.findByEmail(msgDto.getSenderId());
    User recipient = userService.findByEmail(msgDto.getRecipientId());
    Item item = itemService.getItemById(msgDto.getItemId());

    ChatMessage chatMessage = ChatMessageMapper.toEntity(msgDto, sender, recipient, item, chatId);
    chatMessageRepository.save(chatMessage);

    //log.info("ChatId: {}", chatId);

    return ChatMessageMapper.toDto(chatMessage);
    }

  @Override
  public Page<ChatMessageDto> findChatMessages(String senderId, String recipientId, Long itemId, Pageable pageable) {

    String chatId = chatRoomService.getChatRoomId(senderId, recipientId, itemId, false)
        .orElseThrow(() -> new ChatRoomNotFoundException(senderId + " " + recipientId + " " + itemId));
    List<ChatMessage> messages = chatMessageRepository.findByChatId(chatId);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), messages.size());
    return new PageImpl<>(messages.stream().map(ChatMessageMapper::toDto).toList().subList(start, end), pageable, messages.size());
  }
}
