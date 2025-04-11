package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

/** Service interface for handling email operations. */
public interface EmailService {

  /**
   * Sends a password reset email to the specified recipient with a reset token.
   *
   * @param to The email address of the recipient.
   * @param token The password reset token to include in the email.
   */
  void sendPasswordResetEmail(String to, String token);
}
