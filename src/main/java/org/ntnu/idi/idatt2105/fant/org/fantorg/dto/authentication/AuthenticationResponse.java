package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for encapsulating the authentication response.
 * <p>
 * This class holds the access and refresh tokens generated after a successful
 * authentication process.
 * </p>
 * @author Tini Tran
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthenticationResponse {

  /**
   * The JWT access token issued after successful authentication.
   */
  private String accessToken;

  /**
   * The JWT refresh token used to obtain a new access token when the current one expires.
   */
  private String refreshToken;
}
