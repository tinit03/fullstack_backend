package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.EmailServiceImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailServiceImpl emailService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
    ReflectionTestUtils.setField(emailService, "frontendResetUrl", "http://test.com/reset");
  }

  @Test
  public void testSendPasswordResetEmail_Success() throws Exception {
    String recipient = "to@example.com";
    String token = "abc123";

    // Create a dummy session and a spy for MimeMessage so we can intercept method calls.
    Session session = Session.getInstance(new Properties());
    MimeMessage mimeMessage = spy(new MimeMessage(session));

    // Stub the mailSender to return our spy when createMimeMessage is called
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);


    String expectedResetLink = "http://test.com/reset?token=" + token + "&email=" + recipient;


    String expectedContent = "Test email content with reset URL: " + expectedResetLink;

    doReturn(expectedContent).when(mimeMessage).getContent();

    // Now call the method to send the password reset email
    emailService.sendPasswordResetEmail(recipient, token);

    // Verify that the mailSender's send method was called with our message
    verify(mailSender, times(1)).send(mimeMessage);



    assertThat(mimeMessage.getSubject()).isEqualTo("Reset your Shop-it password");

    assertThat(mimeMessage.getAllRecipients()[0].toString()).isEqualTo(recipient);

    Object content = mimeMessage.getContent();
    assertThat(content).asInstanceOf(InstanceOfAssertFactories.STRING).contains(expectedResetLink);
  }
}
