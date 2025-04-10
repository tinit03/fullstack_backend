package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatNotification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatRoom;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatRoomRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatMessageServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatRoomServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.UserServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.SortUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatController {

  private final ChatMessageServiceImpl chatMessageService;
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatRoomRepository chatRoomRepository;
  private final UserServiceImpl userServiceImpl;
  private final ChatRoomServiceImpl chatRoomService;

  @GetMapping("/chats")
  public ResponseEntity<Page<ChatDto>> getUserChats(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "lastEntry") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir
  ) {
    log.info("Received GET request for /chats/{}", user.getEmail());

    Sort sort = SortUtil.buildSort(sortField,sortDir);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<ChatDto> chats = chatRoomService.getChats(user.getEmail(), pageable);
    log.info("Returning result for /chats/{}", user.getEmail());
    return ResponseEntity.ok(chats);
  }

  @GetMapping("/messages/{itemId}/{recipientId}")
  public ResponseEntity<Page<ChatMessageDto>> findChatMessages(
      @PathVariable("itemId") Long itemId,
      @AuthenticationPrincipal User user,
      @PathVariable("recipientId") String recipientId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size
  ) {
    log.info("Received GET request for /messages/{}/{}/{}", itemId, user.getEmail(), recipientId);
    Pageable pageable = PageRequest.of(page, size);
    Page<ChatMessageDto> messages = chatMessageService.findChatMessages(user.getEmail(), recipientId, itemId, pageable);
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
    messagingTemplate.convertAndSendToUser(savedMsgDto.getRecipientId(), "/queue/messages",
        ChatNotification.builder()
            .senderId(savedMsgDto.getSenderId())
            .recipientId(savedMsgDto.getRecipientId())
            .itemId(savedMsgDto.getItemId())
            .content(savedMsgDto.getContent())
            .timestamp(LocalDateTime.now())
            .build()
    );
    log.info("Sent to recipient");
  }

  @PostMapping("/chat")
  public ResponseEntity<ChatMessageDto> contactSeller(@RequestBody ChatMessageCreateDto chatMessageCreateDto) {
    ChatMessageDto savedMsgDto = chatMessageService.save(chatMessageCreateDto);
    messagingTemplate.convertAndSendToUser(savedMsgDto.getRecipientId(), "/queue/messages",
        ChatNotification.builder()
            .senderId(savedMsgDto.getSenderId())
            .recipientId(savedMsgDto.getRecipientId())
            .itemId(savedMsgDto.getItemId())
            .content(savedMsgDto.getContent())
            .timestamp(LocalDateTime.now())
            .build()
    );
    return ResponseEntity.ok(savedMsgDto);
  }

  @GetMapping("/chat/recipient/{recipientId}")
  public ChatProfileDto getRecipientInfo(@PathVariable String recipientId, @AuthenticationPrincipal User user) {
    return userServiceImpl.findChatProfile(recipientId);
  }
}