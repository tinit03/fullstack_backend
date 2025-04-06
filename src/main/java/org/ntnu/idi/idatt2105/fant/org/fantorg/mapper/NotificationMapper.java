package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;

public class NotificationMapper {
  public static NotificationDto toDto(Notification notification) {
    NotificationDto dto = new NotificationDto();
    dto.setId(notification.getId());
    dto.setType(notification.getType());
    dto.setArgs(notification.getArgs());
    dto.setLink(notification.getLink());
    dto.setRead(notification.isRead());
    dto.setCreatedAt(notification.getCreatedAt());
    return dto;
  }
}