package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing token validation results.
 *
 * <p>This class encapsulates the result of token validation, indicating whether the token is valid
 * and providing an accompanying message.
 *
 * @author Tini Tran
 */
@Getter
@Setter
@AllArgsConstructor
public class TokenValidDto {

  /** Indicates whether the token is valid. */
  private boolean valid;

  /** A message providing additional details about the validation result. */
  private String message;
}
