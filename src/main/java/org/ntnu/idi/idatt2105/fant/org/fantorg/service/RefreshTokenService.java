package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public interface RefreshTokenService {
  RefreshToken createToken(User user);
  RefreshToken getByToken(String token);
  RefreshToken validateToken(String token);
  void revokeToken(User user);
}
