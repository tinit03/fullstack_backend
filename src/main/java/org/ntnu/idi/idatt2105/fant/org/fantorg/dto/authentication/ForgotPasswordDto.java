package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class ForgotPasswordDto {
  @Email(message = "Invalid email format")
  private String email;
}
