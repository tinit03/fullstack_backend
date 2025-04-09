package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;

@Getter
@Setter
public class NotificationDto {
  private Long id;
  private NotificationType type;
  private Map<String, String> args;
  private String link;
  private boolean isRead;
  private LocalDateTime createdAt;
}