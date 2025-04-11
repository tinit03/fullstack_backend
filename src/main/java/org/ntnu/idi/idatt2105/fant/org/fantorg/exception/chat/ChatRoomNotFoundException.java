package org.ntnu.idi.idatt2105.fant.org.fantorg.exception.chat;

/**
 * Exception thrown when a chat room with a specific ID is not found.
 * <p>
 * This exception is typically thrown when an operation attempts to access or perform actions on a chat room
 * that does not exist in the system.
 * </p>
 */
public class ChatRoomNotFoundException extends RuntimeException {

  /**
   * Constructs a new {@code ChatRoomNotFoundException} with a detailed message.
   * <p>
   * The message indicates the chat room ID that was not found.
   * </p>
   *
   * @param id The ID of the chat room that was not found.
   */
  public ChatRoomNotFoundException(String id) {
    super("Chat room with id " + id + " not found");
  }
}
