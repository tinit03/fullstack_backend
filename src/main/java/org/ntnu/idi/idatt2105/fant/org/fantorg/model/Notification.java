package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id", nullable = false)
  private User recipient;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @ElementCollection
  @CollectionTable(name = "notification_args", joinColumns = @JoinColumn(name = "notification_id"))
  @MapKeyColumn(name = "arg_key")
  @Column(name = "arg_value")
  private Map<String, String> args = new HashMap<>();

  @Column(nullable = false)
  private boolean isRead = false;

  private String link; // optional reference to item/chat/etc

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
