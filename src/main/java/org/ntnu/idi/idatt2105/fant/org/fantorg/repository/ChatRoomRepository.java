package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatRoom;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  Optional<ChatRoom> findBySenderAndRecipientAndItem(User sender, User recipient, Item item);
}
