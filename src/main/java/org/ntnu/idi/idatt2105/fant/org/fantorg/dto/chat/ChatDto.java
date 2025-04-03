package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatDto {
    private String lastMessageContent;
    private LocalDateTime lastMessageTimestamp;
    private String lastSenderId;
    private String senderId;
    private String recipientId;
    private long itemId;
    private Status status;
    private ImageDto image;
    private String itemTitle;
    private ImageDto recipientProfilePic;
}
