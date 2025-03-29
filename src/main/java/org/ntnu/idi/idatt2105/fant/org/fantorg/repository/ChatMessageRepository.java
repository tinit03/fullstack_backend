package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findBySenderAndRecipientAndItem(User sender, User recipient, Item item);

  List<ChatMessage> findByChatId(String chatId);
}
