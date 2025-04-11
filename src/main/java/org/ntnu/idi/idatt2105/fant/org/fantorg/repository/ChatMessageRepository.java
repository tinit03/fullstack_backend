package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link ChatMessage} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations
 * and additional methods for querying {@link ChatMessage} records in the database.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Finds a list of {@link ChatMessage} records based on the sender, recipient, and associated item.
     * This method retrieves all chat messages exchanged between two users related to a specific item.
     *
     * @param sender the user who sent the message
     * @param recipient the user who received the message
     * @param item the item related to the chat messages
     * @return a list of {@link ChatMessage} records exchanged between the sender and recipient for the given item
     */
    List<ChatMessage> findBySenderAndRecipientAndItem(User sender, User recipient, Item item);

    /**
     * Finds a list of {@link ChatMessage} records based on the unique chat ID.
     * This method retrieves all messages that belong to a specific chat, identified by the chat ID.
     *
     * @param chatId the unique identifier for the chat
     * @return a list of {@link ChatMessage} records belonging to the specified chat
     */
    List<ChatMessage> findByChatId(String chatId);
}
