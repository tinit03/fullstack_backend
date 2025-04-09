package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.Map;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

public interface NotificationService {
  void send(User recipient, Map<String,String> args, NotificationType type, String link);
  Page<NotificationDto> getNotifications(User user, Pageable pageable);
  void markAsRead(Long notificationId, User user);

  void deleteNotification(Long id, User user);

  void deleteAll(User user);
}
