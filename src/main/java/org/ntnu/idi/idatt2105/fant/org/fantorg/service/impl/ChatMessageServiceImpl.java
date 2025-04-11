package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatNotification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.chat.ChatRoomNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ChatMessageMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatMessageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling chat message functionality, including saving messages,
 * retrieving messages by chat room, and sending notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final UserServiceImpl userService;
  private final ChatRoomServiceImpl chatRoomService;
  private final ItemServiceImpl itemService;
  private final NotificationService notificationService;
  SimpMessagingTemplate messagingTemplate;

  /**
   * Saves a new chat message, sends a notification to the recipient, and links it to the correct
   * chat room.
   *
   * @param msgDto The chat message data transfer object containing sender, recipient, and message
   *     content.
   * @return The saved {@link ChatMessageDto}.
   * @throws ChatRoomNotFoundException If the chat room for the given users and item doesn't exist.
   */
  @Override
  public ChatMessageDto save(ChatMessageCreateDto msgDto) {

    String chatIdA =
        chatRoomService
            .getChatRoomId(msgDto.getSenderId(), msgDto.getRecipientId(), msgDto.getItemId(), true)
            .orElseThrow(
                () ->
                    new ChatRoomNotFoundException(
                        msgDto.getSenderId()
                            + " "
                            + msgDto.getRecipientId()
                            + " "
                            + msgDto.getItemId()));

    String chatIdB =
        chatRoomService
            .getChatRoomId(msgDto.getRecipientId(), msgDto.getSenderId(), msgDto.getItemId(), true)
            .orElseThrow(
                () ->
                    new ChatRoomNotFoundException(
                        msgDto.getSenderId()
                            + " "
                            + msgDto.getRecipientId()
                            + " "
                            + msgDto.getItemId()));

    List<String> chatIds = List.of(chatIdA, chatIdB);

    chatRoomService.newEntry(chatIds);

    User sender = userService.findByEmail(msgDto.getSenderId());
    User recipient = userService.findByEmail(msgDto.getRecipientId());
    Item item = itemService.getItemById(msgDto.getItemId());

    ChatMessage chatMessage = ChatMessageMapper.toEntity(msgDto, sender, recipient, item, chatIdA);
    chatMessageRepository.save(chatMessage);

    if (MessageType.NORMAL == chatMessage.getType()) {
      Map<String, String> args =
          Map.of(
              "user",
              sender.getFirstName() + " " + sender.getLastName(),
              "messageType",
              MessageType.NORMAL.toString());
      String link =
          "/messages?itemId="
              + item.getItemId()
              + "&recipientId="
              + chatMessage.getSender().getEmail();
      notificationService.send(recipient, args, NotificationType.MESSAGE_RECEIVED, link);
    }

    return ChatMessageMapper.toDto(chatMessage);
  }

  /**
   * Retrieves paginated chat messages between two users regarding a specific item.
   *
   * @param senderId The sender's email.
   * @param recipientId The recipient's email.
   * @param itemId The ID of the item in context.
   * @param pageable The pagination information.
   * @return A page of {@link ChatMessageDto} objects.
   * @throws ChatRoomNotFoundException If the chat room does not exist.
   */
  @Override
  public Page<ChatMessageDto> findChatMessages(
      String senderId, String recipientId, Long itemId, Pageable pageable) {

    String chatId =
        chatRoomService
            .getChatRoomId(senderId, recipientId, itemId, false)
            .orElseThrow(
                () -> new ChatRoomNotFoundException(senderId + " " + recipientId + " " + itemId));
    List<ChatMessage> messages = chatMessageRepository.findByChatId(chatId);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), messages.size());
    return new PageImpl<>(
        messages.stream().map(ChatMessageMapper::toDto).toList().subList(start, end),
        pageable,
        messages.size());
  }

  /**
   * Saves a new bid message. This method ensures that the message type is BID before saving.
   *
   * @param chatMessageCreateDto The bid message data transfer object.
   * @return The saved {@link ChatMessageDto}.
   * @throws IllegalArgumentException If the message type is not {@link MessageType#BID}.
   */
  @Override
  public ChatMessageDto saveNewBidMessage(ChatMessageCreateDto chatMessageCreateDto) {
    if (chatMessageCreateDto.getType() != MessageType.BID) {
      throw new IllegalArgumentException(
          "The message type is not a bid: " + chatMessageCreateDto.getType());
    }

    return save(chatMessageCreateDto);
  }

  @Override
  public void send(ChatMessageCreateDto dto) {
    ChatMessageDto savedMsgDto = save(dto);
    messagingTemplate.convertAndSendToUser(
        savedMsgDto.getRecipientId(),
        "/queue/messages",
        ChatNotification.builder()
            .senderId(savedMsgDto.getSenderId())
            .recipientId(savedMsgDto.getRecipientId())
            .itemId(savedMsgDto.getItemId())
            .content(savedMsgDto.getContent())
            .timestamp(LocalDateTime.now())
            .build());
  }
}
