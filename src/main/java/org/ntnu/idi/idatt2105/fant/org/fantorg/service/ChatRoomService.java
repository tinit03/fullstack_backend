package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.Optional;

/**
 * Service interface for managing chat room identifiers between users in the context of specific
 * items.
 */
public interface ChatRoomService {

  /**
   * Retrieves the chat room ID for a conversation between two users about a specific item.
   *
   * @param senderId the ID of the user sending the message
   * @param recipientId the ID of the user receiving the message
   * @param itemId the ID of the item the chat is about
   * @param createNewRoomIfExists if true, creates a new chat room if one doesn't already exist
   * @return an {@link Optional} containing the chat room ID if it exists (or was created), or empty
   *     if not found and not created
   */
  Optional<String> getChatRoomId(
      String senderId, String recipientId, Long itemId, boolean createNewRoomIfExists);
}
