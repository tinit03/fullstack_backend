package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for encapsulating user login credentials.
 * <p>
 * This class stores the necessary information required for a user to log in,
 * including the email address and password. Validation annotations ensure that:
 * </p>
 * <ul>
 *   <li>The email is provided, not blank, and conforms to a valid email format.</li>
 *   <li>The password is provided and is not blank.</li>
 * </ul>
 *
 * @author Tini Tran.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

  /**
   * The user's email address.
   * <p>
   * This field is mandatory and must be a valid email format.
   * </p>
   */
  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;

  /**
   * The user's password.
   * <p>
   * This field is mandatory and must not be blank.
   * </p>
   */
  @NotBlank(message = "Password is required")
  private String password;
}
