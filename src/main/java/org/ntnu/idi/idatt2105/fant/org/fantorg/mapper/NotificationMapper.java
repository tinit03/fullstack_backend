package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;

/**
 * Utility class for converting between Notification entities and their corresponding DTOs.
 *
 * <p>The NotificationMapper class provides methods to map Notification entities to NotificationDto
 * objects for reading notifications.
 */
public class NotificationMapper {

  /**
   * Converts a Notification entity to a NotificationDto.
   *
   * <p>This method maps all relevant fields from the Notification entity to a NotificationDto,
   * which is used to represent the notification details in a response.
   *
   * @param notification The Notification entity to be converted.
   * @return The NotificationDto containing the notification details.
   */
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
