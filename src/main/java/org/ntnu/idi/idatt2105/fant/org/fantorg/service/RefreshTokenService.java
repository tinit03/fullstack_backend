package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/** Service interface for managing refresh tokens in the authentication system. */
public interface RefreshTokenService {

  /**
   * Creates a new refresh token for the specified user.
   *
   * @param user The user for whom the token will be created.
   * @return The newly created RefreshToken.
   */
  RefreshToken createToken(User user);

  /**
   * Retrieves a refresh token entity based on its string token.
   *
   * @param token The token string.
   * @return The RefreshToken object associated with the token.
   */
  RefreshToken getByToken(String token);

  /**
   * Validates the provided refresh token string.
   *
   * @param token The token string to validate.
   * @return The valid RefreshToken object if valid.
   * @throws RuntimeException if the token is invalid or expired.
   */
  RefreshToken validateToken(String token);

  /**
   * Revokes all active refresh tokens for the given user.
   *
   * @param user The user whose tokens should be revoked.
   */
  void revokeToken(User user);
}
