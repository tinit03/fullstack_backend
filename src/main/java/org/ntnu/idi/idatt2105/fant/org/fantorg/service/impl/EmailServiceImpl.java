package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Service implementation for sending emails. Implements {@link EmailService} to handle
 * email-related operations.
 *
 * <p>This service is responsible for sending password reset emails to users. It constructs the
 * reset password URL, loads the HTML email template, and sends the email using {@link
 * JavaMailSender}.
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${app.frontend.reset-url}")
  private String frontendResetUrl;

  /**
   * Sends a password reset email to the specified recipient.
   *
   * <p>This method constructs a reset link using the provided token and email address, loads an
   * HTML email template, and sends the password reset email to the user.
   *
   * @param to The recipient's email address.
   * @param token The password reset token.
   * @throws RuntimeException If an error occurs while sending the email.
   */
  @Override
  public void sendPasswordResetEmail(String to, String token) {

    String resetLink =
        UriComponentsBuilder.fromUriString(frontendResetUrl)
            .queryParam("token", token)
            .queryParam("email", to)
            .toUriString();
    String content = loadResetPasswordEmailTemplate(resetLink);
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());
      helper.setTo(to);
      helper.setSubject("Reset your Shop-it password");
      helper.setFrom(fromEmail);
      helper.setText(content, true);
      mailSender.send(message);
      logger.info("Password reset email sent to {}", to);
    } catch (MessagingException e) {
      logger.error("Failed to send password reset email to {}: {}", to, e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }

  /**
   * Loads the HTML template for the password reset email and replaces the placeholder with the
   * actual reset link.
   *
   * @param resetLink The password reset link to be embedded in the email.
   * @return The populated HTML email content.
   * @throws RuntimeException If an error occurs while loading the email template.
   */
  private String loadResetPasswordEmailTemplate(String resetLink) {
    try {
      Resource resource = new ClassPathResource("mail/reset-password-email.html");
      String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      return content.replace("${resetUrl}", resetLink);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load email template", e);
    }
  }
}
