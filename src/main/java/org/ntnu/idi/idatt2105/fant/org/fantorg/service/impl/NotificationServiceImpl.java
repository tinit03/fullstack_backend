package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.NotificationRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  @Override
  public void send(User recipient, Map<String, String> args ,NotificationType type, String link) {
    Notification notification = Notification.builder()
        .recipient(recipient)
        .args(args)
        .type(type)
        .isRead(false)
        .link(link)
        .createdAt(LocalDateTime.now())
        .build();
    notificationRepository.save(notification);

  }

  @Override
  public Page<Notification> getNotifications(User user, Pageable pageable) {
    return notificationRepository.findByRecipient(user, pageable);
  }

  @Override
  public void markAsRead(Long notificationId, User user) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new RuntimeException("Notification not found"));

    if (!notification.getRecipient().getId().equals(user.getId())) {
      throw new SecurityException("You can only modify your own notifications");
    }

    notification.setRead(true);
    notificationRepository.save(notification);
  }

  public Map<String, String> buildArgs(NotificationType type, Object... args) {
    return switch (type) {
      case NEW_BID -> Map.of("user", args[0].toString(), "item", args[1].toString());
      case BID_ACCEPTED -> Map.of("item", args[0].toString());
      case ITEM_SOLD -> Map.of("item", args[0].toString());
      case ITEM_SOLD_ELSE -> Map.of("item", args[0].toString());
      case MESSAGE_RECEIVED -> Map.of("from", args[0].toString());
    };
  }
}
