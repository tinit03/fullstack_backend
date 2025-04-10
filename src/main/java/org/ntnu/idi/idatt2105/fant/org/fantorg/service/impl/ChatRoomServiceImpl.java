package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final UserServiceImpl userService;
  private final ItemServiceImpl itemService;
  private final ChatMessageRepository chatMessageRepository;

  /**
   * Method for finding a chat room from the sender, recipient and item. If it does not
   * exist, the flag determines whether a new one should be created.
   *
   * @param senderId Mail of the sender.
   * @param recipientId Recipient of message.
   * @param itemId Identifier of the item of the seller
   * @param createNewRoomIfExists Flag for whether a new room should be created
   * @return
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

    //log.info("FInished saving chat rooms");
    return chatId;
  }

  public Page<ChatDto> getChats(String senderId, Pageable pageable) {
    List<ChatDto> chats = new ArrayList<>();
    User sender = userService.findByEmail(senderId);
    List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsBySender(sender);
    //log.info("starting loop");
    chatRooms.forEach(chatRoom -> {
      Item item = itemService.getItemById(chatRoom.getItem().getItemId());
      User recipient = userService.findByEmail(chatRoom.getRecipient().getEmail());
      List<ChatMessage> messages;
      ChatMessage lastMessage;
      String chatId = String.format("%s_%s_%s", sender.getEmail(), recipient.getEmail(), item.getItemId());
      messages = chatMessageRepository.findByChatId(chatId);
      if (messages.isEmpty()) {
        chatId = String.format("%s_%s_%s", recipient.getEmail(), sender.getEmail(), item.getItemId());
        messages = chatMessageRepository.findByChatId(chatId);
      }
      if (!messages.isEmpty()) {
        lastMessage = messages.getLast();
        //log.info("got message {}", lastMessage.getContent());
        //log.info("creating dto");
        ChatDto chat =
            ChatDto.builder()
                .lastMessageContent(lastMessage.getContent())
                .lastMessageTimestamp(lastMessage.getTimestamp())
                .lastSenderId(lastMessage.getSender().getEmail())
                .senderId(senderId)
                .recipientId(recipient.getEmail())
                .itemId(item.getItemId())
                .status(item.getStatus())
                .itemTitle(item.getTitle())
                .build();
        chats.add(chat);

        if (!item.getImages().isEmpty()) {
          chat.setImage(item.getImages().getFirst().getUrl());
        }

        if (recipient.getProfileImage() != null) {
          chat.setRecipientProfilePic(recipient.getProfileImage().getUrl());
        }
      }
    });
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), chats.size());

    return new PageImpl<>(chats.subList(start, end), pageable, chats.size());
  }
}
