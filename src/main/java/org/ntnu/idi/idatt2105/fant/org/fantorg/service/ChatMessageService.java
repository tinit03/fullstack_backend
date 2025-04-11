package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for handling chat messages between users, including bid-related messages. */
public interface ChatMessageService {

  /**
   * Saves a general chat message to the database.
   *
   * @param msgDto the DTO containing the chat message details
   * @return the saved chat message as a {@link ChatMessageDto}
   */
  ChatMessageDto save(ChatMessageCreateDto msgDto);

  /**
   * Retrieves chat messages exchanged between a sender and recipient related to a specific item.
   *
   * @param senderId the ID of the sender
   * @param recipientId the ID of the recipient
   * @param itemId the ID of the item the chat is about
   * @param pageable pagination information
   * @return a page of {@link ChatMessageDto}
   */
  Page<ChatMessageDto> findChatMessages(
      String senderId, String recipientId, Long itemId, Pageable pageable);

  /**
   * Saves a chat message that is associated with a new bid.
   *
   * @param msgDto the DTO containing the bid message details
   * @return the saved bid message as a {@link ChatMessageDto}
   */
  ChatMessageDto saveNewBidMessage(ChatMessageCreateDto msgDto);

  /**
   * Sends a chat message through the appropriate channel (e.g., WebSocket).
   *
   * @param msgDto the DTO containing the message to send
   */
  void send(ChatMessageCreateDto msgDto);
}
