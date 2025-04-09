package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.NotificationRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationIT {

  @Autowired
  private MockMvc mockMvc;


  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private User testUser;

  @BeforeEach
  public void setUp() {
    notificationRepository.deleteAll();
    userRepository.deleteAll();

    testUser = new User();
    testUser.setFirstName("Notification");
    testUser.setLastName("Tester");
    testUser.setEmail("notify@example.com");
    testUser.setPassword(passwordEncoder.encode("password"));
    testUser.setRole(Role.USER);
    testUser = userRepository.save(testUser);

    Notification notif1 = Notification.builder()
        .recipient(testUser)
        .type(NotificationType.NEW_BID)
        .isRead(false)
        .link("/items/1")
        .createdAt(LocalDateTime.now().minusMinutes(10))
        .build();
    Notification notif2 = Notification.builder()
        .recipient(testUser)
        .type(NotificationType.BID_ACCEPTED)
        .isRead(false)
        .link("/orders/123")
        .createdAt(LocalDateTime.now().minusMinutes(5))
        .build();
    notificationRepository.saveAll(List.of(notif1, notif2));
  }

  @Test
  public void testGetMyNotifications() throws Exception {
    // Call GET /notifications/me as testUser.
    MvcResult result = mockMvc.perform(get("/notifications/me")
            .with(user(testUser))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    // We should see at least one of the notification types in the response
    assertThat(jsonResponse).contains("NEW_BID", "BID_ACCEPTED");
  }

  @Test
  public void testMarkAsRead() throws Exception {
    // Retrieve a notification from repository
    List<Notification> notifications = notificationRepository.findByRecipient(testUser);
    Long notifId = notifications.get(0).getId();

    // Call PATCH /notifications/{id}/read
    mockMvc.perform(patch("/notifications/{id}/read", notifId)
            .with(user(testUser)))
        .andExpect(status().isNoContent());

    // Check that the notification is now marked as read
    Notification updated = notificationRepository.findById(notifId)
        .orElseThrow();
    assertThat(updated.isRead()).isTrue();
  }

  @Test
  public void testDeleteNotification() throws Exception {
    // Retrieve one notification
    List<Notification> notifications = notificationRepository.findByRecipient(testUser);
    Long notifId = notifications.get(0).getId();

    // Call DELETE /notifications/{id}
    mockMvc.perform(delete("/notifications/{id}", notifId)
            .with(user(testUser)))
        .andExpect(status().isNoContent());

    // Check if the notification is removed.
    Optional<Notification> opt = notificationRepository.findById(notifId);
    assertThat(opt).isEmpty();
  }

  @Test
  public void testDeleteAllNotifications() throws Exception {
    // Call DELETE /notifications/me to remove all notifications for testUser
    mockMvc.perform(delete("/notifications/me")
            .with(user(testUser)))
        .andExpect(status().isNoContent());

    // Check that the user no longer has any notifications
    List<Notification> notifications = notificationRepository.findByRecipient(testUser);
    assertThat(notifications).isEmpty();
  }
}
