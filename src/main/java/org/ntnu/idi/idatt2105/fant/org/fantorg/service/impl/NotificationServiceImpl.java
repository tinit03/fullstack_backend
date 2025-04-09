package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.NotificationMapper;
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
  public Page<NotificationDto> getNotifications(User user, Pageable pageable) {
    Page<Notification> notifications= notificationRepository.findByRecipient(user, pageable);
    return notifications.map(NotificationMapper::toDto);
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

  @Override
  public void deleteNotification(Long id, User user){
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
    if (!notification.getRecipient().getId().equals(user.getId())) {
      throw new SecurityException("You can only delete your own notifications");
    }
    notificationRepository.delete(notification);
  }

  @Override
  public void deleteAll(User user) {
    List<Notification> userNotifications = notificationRepository.findByRecipient(user);
    notificationRepository.deleteAll(userNotifications);
  }

}
