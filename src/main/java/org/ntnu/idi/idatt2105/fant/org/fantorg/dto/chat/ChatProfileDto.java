package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatProfileDto {
  private String url;
  private String fullName;
}
