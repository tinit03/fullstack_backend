package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for forgot password.
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class ForgotPasswordDto {

  /** The email of the account which the password has been forgotten. */
  @Email(message = "Invalid email format")
  private String email;
}
