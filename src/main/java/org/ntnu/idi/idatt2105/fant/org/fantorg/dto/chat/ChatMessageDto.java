package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessageDto {

  private Long id;

  private String senderId;

  private String recipientId;

  private Long itemId;

  private String chatId;

  private String content;

  private Date timestamp;
}
