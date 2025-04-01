package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

public interface EmailService {
  void sendPasswordResetEmail(String to, String token);
}
