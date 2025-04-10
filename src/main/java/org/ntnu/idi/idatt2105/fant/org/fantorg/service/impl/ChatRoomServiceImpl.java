package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatRoom;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatMessageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatRoomRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ChatRoomService}, responsible for managing chat rooms between users,
 * creating new chat connections, retrieving user-specific chat lists, and maintaining timestamps.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final UserServiceImpl userService;
  private final ItemServiceImpl itemService;
  private final ChatMessageRepository chatMessageRepository;

  /**
   * Retrieves an existing chat room ID between two users regarding a specific item.
   * If not found and {@code createNewRoomIfExists} is {@code true}, creates a new chat room.
   *
   * @param senderId               The sender's email address.
   * @param recipientId            The recipient's email address.
   * @param itemId                 The item ID involved in the conversation.
   * @param createNewRoomIfExists  Whether to create a new chat room if none is found.
   * @return An {@link Optional} containing the chat ID, or empty if not found and creation is disabled.
   */
  @Override
  public Optional<String> getChatRoomId(String senderId, String recipientId, Long itemId,
      boolean createNewRoomIfExists) {
    //log.info("Finding optional chat ID for {} {} {}", senderId, recipientId, itemId);
    User sender = userService.findByEmail(senderId);
    User recipient  = userService.findByEmail(recipientId);
    Item item = itemService.getItemById(itemId);

    return chatRoomRepository.findBySenderAndRecipientAndItem(sender, recipient, item)
        .map(ChatRoom::getChatId)
        .or(() -> {
          if (createNewRoomIfExists) {
            String chatId = createChatId(sender, recipient, item);
            return Optional.of(chatId);
          }
          return Optional.empty();
        });
  }

  /**
   * Creates two {@link ChatRoom}s from a sender, recipient and item
   * for the duplex connection.
   *
   * @param sender Sender of message.
   * @param recipient Recipient of message.
   * @param item Item of the seller.
   * @return String of the chatId.
   */
  private String createChatId(User sender, User recipient, Item item) {
    //log.info("Creatnig chatId");
    String chatId = String.format("%s_%s_%s", sender.getEmail(), recipient.getEmail(), item.getItemId());

    ChatRoom senderRecipient = ChatRoom
        .builder()
        .sender(sender)
        .recipient(recipient)
        .item(item)
        .chatId(chatId)
        .build();

    ChatRoom recipientSender = ChatRoom
        .builder()
        .sender(recipient)
        .recipient(sender)
        .item(item)
        .chatId(chatId)
        .build();

    //log.info("Saving chat rooms");
    chatRoomRepository.save(senderRecipient);
    chatRoomRepository.save(recipientSender);

    //log.info("Finished saving chat rooms");
    return chatId;
  }

  /**
   * Updates the {@code lastEntry} timestamp for all chat rooms matching the given chat IDs.
   *
   * @param chatIds List of chat room IDs to update.
   */
  public void newEntry(List<String> chatIds) {
    List<ChatRoom> chatRooms = chatRoomRepository.findByChatIdIn(chatIds);
    LocalDateTime newEntryTime = LocalDateTime.now();
    for (ChatRoom chatRoom : chatRooms) {
      chatRoom.setLastEntry(newEntryTime);
      chatRoomRepository.save(chatRoom);
    }
  }

  /**
   * Retrieves a paginated list of chat rooms for a given sender, including metadata such as
   * the last message content, timestamp, item details, and recipient profile info.
   *
   * @param senderId  The sender's email.
   * @param pageable  The paging parameters.
   * @return A {@link Page} of {@link ChatDto} representing the user's chat overview.
   */
  public Page<ChatDto> getChats(String senderId, Pageable pageable) {
    User sender = userService.findByEmail(senderId);
    Page<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsBySender(sender, pageable);

    Page<ChatDto> chatDtoPage = chatRooms.map(chatRoom -> {
      Item item = itemService.getItemById(chatRoom.getItem().getItemId());
      User recipient = userService.findByEmail(chatRoom.getRecipient().getEmail());
      String chatId = String.format("%s_%s_%s", sender.getEmail(), recipient.getEmail(), item.getItemId());
      List<ChatMessage> messages = chatMessageRepository.findByChatId(chatId);

      if (messages.isEmpty()) {
        chatId = String.format("%s_%s_%s", recipient.getEmail(), sender.getEmail(), item.getItemId());
        messages = chatMessageRepository.findByChatId(chatId);
      }

      ChatMessage lastMessage = messages.getLast();
      log.warn(messages.getLast().toString());

      ChatDto chat = ChatDto.builder()
              .lastMessageContent(lastMessage.getContent())
              .lastMessageTimestamp(lastMessage.getTimestamp())
              .lastSenderId(lastMessage.getSender().getEmail())
              .senderId(senderId)
              .recipientId(recipient.getEmail())
              .itemId(item.getItemId())
              .status(item.getStatus())
              .itemTitle(item.getTitle())
              .build();

      if (!item.getImages().isEmpty()) {
        chat.setImage(item.getImages().getFirst().getUrl());
      }

      if (recipient.getProfileImage() != null) {
        chat.setRecipientProfilePic(recipient.getProfileImage().getUrl());
      }
      return chat;
    });
    return chatDtoPage;
  }
}
