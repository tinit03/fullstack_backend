package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {
  private String email;
  private String token;
  private String newPassword;
}
