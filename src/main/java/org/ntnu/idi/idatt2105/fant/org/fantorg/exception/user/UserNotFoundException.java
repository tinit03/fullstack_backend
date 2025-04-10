package org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user;

/**
 * Exception thrown when a user with a specific email or ID is not found.
 * <p>
 * This exception is typically thrown when an operation attempts to access or perform actions on a user
 * that does not exist in the system.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

  /**
   * Constructs a new {@code UserNotFoundException} with a detailed message using the user's email.
   * <p>
   * The message indicates the email of the user that was not found.
   * </p>
   *
   * @param email The email of the user that was not found.
   */
  public UserNotFoundException(String email) {
    super("User with email: " + email + " not found");
  }

  /**
   * Constructs a new {@code UserNotFoundException} with a detailed message using the user's ID.
   * <p>
   * The message indicates the ID of the user that was not found.
   * </p>
   *
   * @param id The ID of the user that was not found.
   */
  public UserNotFoundException(Long id) {
    super("User id: " + id + " not found");
  }
}
