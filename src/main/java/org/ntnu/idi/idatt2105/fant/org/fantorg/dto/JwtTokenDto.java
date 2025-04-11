package org.ntnu.idi.idatt2105.fant.org.fantorg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a JWT (JSON Web Token).
 * <p>
 * This class is used to transfer the JWT token used for authentication and authorization.
 * </p>
 */
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class JwtTokenDto {

  /**
   * The JWT token as a string.
   * <p>
   * This field contains the token that is used to authenticate the user and grant access to protected resources.
   * </p>
   */
  private String token;
}
