package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatRoom;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link ChatRoom} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations and additional methods for querying {@link
 * ChatRoom} records in the database.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  /**
   * Finds a {@link ChatRoom} based on the sender, recipient, and associated item. This method
   * retrieves a chat room between two users related to a specific item.
   *
   * @param sender the user who initiated the chat
   * @param recipient the user who is the recipient of the chat
   * @param item the item related to the chat room
   * @return an {@link Optional} containing the {@link ChatRoom} if found, or empty if no chat room
   *     exists
   */
  Optional<ChatRoom> findBySenderAndRecipientAndItem(User sender, User recipient, Item item);

  /**
   * Finds a paginated list of {@link ChatRoom} records for a specific sender. This method retrieves
   * all chat rooms initiated by a specific user (sender).
   *
   * @param sender the user who initiated the chat rooms
   * @param pageable the pagination information
   * @return a {@link Page} containing {@link ChatRoom} records for the specified sender
   */
  Page<ChatRoom> findChatRoomsBySender(User sender, Pageable pageable);

  /**
   * Finds a list of {@link ChatRoom} records based on a list of chat IDs. This method retrieves
   * chat rooms with the specified chat IDs.
   *
   * @param chatIds the list of chat IDs to search for
   * @return a list of {@link ChatRoom} records matching the provided chat IDs
   */
  List<ChatRoom> findByChatIdIn(List<String> chatIds);
}
