package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for refreshing password
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class ResetPasswordDto {
  /**
   * Email of account for which password should be reset.
   */
  private String email;

  /**
   * The authentication token to authenticate the account.
   */
  private String token;

  /**
   * The password which the old one is changed to.
   */
  private String newPassword;
}
