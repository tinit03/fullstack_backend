package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessageCreateDto {

  @NotNull(message = "Sender ID is required")
  private String senderId;

  @NotNull(message = "Recipient ID is required")
  private String recipientId;

  @NotNull(message = "Item ID is required")
  private Long itemId;

  @Size(max = 200, message = "Message cannot be longer than 200 characters")
  private String content;

  private MessageType type;
}
