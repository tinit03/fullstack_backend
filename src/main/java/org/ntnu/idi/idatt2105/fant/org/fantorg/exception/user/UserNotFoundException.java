package org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String email) {
    super("User with email: " + email + " not found");
  }
}
