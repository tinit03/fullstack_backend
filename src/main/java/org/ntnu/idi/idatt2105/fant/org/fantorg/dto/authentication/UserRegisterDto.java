package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used for registering a new user.
 * <p>
 * This DTO encapsulates the information required to create a new user account,
 * including email address, first name, last name, and password.
 * </p>
 */
@Getter
@Setter
public class UserRegisterDto {

  /**
   * The email address of the user.
   */
  private String email;

  /**
   * The first name of the user.
   */
  private String firstName;

  /**
   * The last name of the user.
   */
  private String lastName;

  /**
   * The password for the user account.
   */
  private String password;
}
