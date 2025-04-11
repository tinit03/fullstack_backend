package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for refresh token request.
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class RefreshTokenRequest {

  /** Refresh token for renewing accessing token. */
  private String refreshToken;
}
