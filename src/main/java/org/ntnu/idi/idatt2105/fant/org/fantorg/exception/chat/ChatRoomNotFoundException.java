package org.ntnu.idi.idatt2105.fant.org.fantorg.exception.chat;

public class ChatRoomNotFoundException extends RuntimeException {
  public ChatRoomNotFoundException(String id) {
    super("Chat room with id " + id + " not found");
  }
}
