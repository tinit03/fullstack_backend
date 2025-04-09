package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenValidDto {
  private boolean valid;
  private String message;
}
