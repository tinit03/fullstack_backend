package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

  private final SimpMessageSendingOperations messagingTemplate;
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String mail = headerAccessor.getSessionAttributes().get("mail").toString();

    if (mail != null) {
      log.info("User disconnected: {}", mail);
      var chatMessage = ChatMessage.builder()
          .type(MessageType.LEAVE)
          .sender(mail)
          .build();
      messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
  }
}
