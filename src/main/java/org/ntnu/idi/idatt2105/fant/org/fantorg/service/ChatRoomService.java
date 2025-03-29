package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.Optional;

public interface ChatRoomService {

  Optional<String> getChatRoomId(
      String senderId,
      String recipientId,
      Long itemId,
      boolean createNewRoomIfExists
  );
}
