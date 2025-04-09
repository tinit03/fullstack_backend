package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"Chat_message\"")
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private User sender;

  @ManyToOne
  private User recipient;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  private String content;

  private String chatId;

  private MessageType type;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime timestamp;
}

