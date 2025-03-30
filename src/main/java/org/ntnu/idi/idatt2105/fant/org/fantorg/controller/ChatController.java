package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatNotification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatRoom;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatRoomRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatMessageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

  private final ChatMessageServiceImpl chatMessageService;
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatRoomRepository chatRoomRepository;

  @GetMapping("/messages/{itemId}/{senderId}/{recipientId}")
  public ResponseEntity<List<ChatMessageDto>> findChatMessages(
      @PathVariable("itemId") Long itemId,
      @PathVariable("senderId") String senderId,
      @PathVariable("recipientId") String recipientId
  ) {
    log.info("Received GET request for /messages/{}/{}/{}", itemId, senderId, recipientId);
    List<ChatMessageDto> messages = chatMessageService.findChatMessages(senderId, recipientId, itemId);
    log.info("messages: {}", messages);
    return ResponseEntity.ok(messages);
  }

  @MessageMapping("/chat")
  public void processMessage(@Payload ChatMessageCreateDto messageDto) {
    log.info("Received message for /chat");
    log.info("Received chat msg: '{}'", messageDto.getContent());
    log.info("Saving chat msg: '{}'...", messageDto.getContent());
    ChatMessageDto savedMsgDto = chatMessageService.save(messageDto);

    log.info("Saved chat msg: '{}'", savedMsgDto);

    log.info("Sending message to /{}/queue/messages", savedMsgDto.getRecipientId());
    messagingTemplate.convertAndSendToUser(savedMsgDto.getRecipientId(), "/queue/messages/" + messageDto.getItemId(),
        ChatNotification.builder()
            .id(savedMsgDto.getId())
            .senderId(savedMsgDto.getSenderId())
            .recipientId(savedMsgDto.getRecipientId())
            .itemId(savedMsgDto.getItemId())
            .content(savedMsgDto.getContent())
            .build()
    );
    log.info("Sent to recipient");
  }

  @GetMapping("/getChats")
  public List<ChatRoom> getChatRooms() {
    return chatRoomRepository.findAll();
  }
}