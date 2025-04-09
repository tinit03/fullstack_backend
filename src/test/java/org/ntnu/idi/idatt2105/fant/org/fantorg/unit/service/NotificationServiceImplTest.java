package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Notification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.NotificationRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.NotificationServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationServiceImpl notificationService;


  private User sampleUser;
  private Notification sampleNotification;

  @BeforeEach
  public void setUp() {
    sampleUser = new User();


    sampleUser.setFirstName("Test");
    sampleUser.setLastName("User");
    sampleUser.setEmail("test@example.com");


    sampleUser = User.builder()
        .firstName("Test")
        .lastName("User")
        .email("test@example.com")
        .password("dummy")
        .role(Role.USER) // role not needed for these tests
        .build();

    ReflectionTestUtils.setField(sampleUser, "id", 1L);

    sampleNotification = Notification.builder()
        .id(100L)
        .recipient(sampleUser)
        .args(new HashMap<>(Map.of("key", "value")))
        .type(NotificationType.NEW_BID)
        .isRead(false)
        .link("/items/10")
        .createdAt(LocalDateTime.now().minusDays(1))
        .build();
  }

  @Test
  public void testSend_Success() {
    // When send() is called, a new notification is built and saved
    Map<String, String> args = Map.of("user", "John Doe", "item", "Test Item");
    String link = "/items/10";


    when(notificationRepository.save(any(Notification.class)))
        .thenAnswer(invocation -> {
          Notification notif = invocation.getArgument(0);
          ReflectionTestUtils.setField(notif, "id", 101L);
          return notif;
        });

    notificationService.send(sampleUser, args, NotificationType.NEW_BID, link);

    // Check that notificationRepository.save() was called once
    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  public void testGetNotifications_Success() {
    List<Notification> notifications = Arrays.asList(sampleNotification);
    Page<Notification> page = new PageImpl<>(notifications);
    Pageable pageable = Pageable.unpaged();
    when(notificationRepository.findByRecipient(eq(sampleUser), any(Pageable.class)))
        .thenReturn(page);

    Page<NotificationDto> result = notificationService.getNotifications(sampleUser, pageable);
    assertThat(result.getTotalElements()).isEqualTo(1);
    NotificationDto dto = result.getContent().get(0);
    assertThat(dto.getLink()).isEqualTo(sampleNotification.getLink());
    assertThat(dto.getType()).isEqualTo(sampleNotification.getType());
  }

  @Test
  public void testMarkAsRead_Success() {
    when(notificationRepository.findById(100L))
        .thenReturn(java.util.Optional.of(sampleNotification));

    notificationService.markAsRead(100L, sampleUser);
    assertThat(sampleNotification.isRead()).isTrue();
    verify(notificationRepository, times(1)).save(sampleNotification);
  }

  @Test
  public void testMarkAsRead_SecurityException() {
    User otherUser = User.builder()
        .firstName("Other")
        .lastName("User")
        .email("other@example.com")
        .password("dummy")
        .build();
    org.springframework.test.util.ReflectionTestUtils.setField(otherUser, "id", 2L);

    when(notificationRepository.findById(100L))
        .thenReturn(java.util.Optional.of(sampleNotification));

    assertThrows(SecurityException.class, () -> {
      notificationService.markAsRead(100L, otherUser);
    });
  }

  @Test
  public void testDeleteNotification_Success() {
    when(notificationRepository.findById(100L))
        .thenReturn(java.util.Optional.of(sampleNotification));
    notificationService.deleteNotification(100L, sampleUser);
    // Check that repository.delete() was called once
    verify(notificationRepository, times(1)).delete(sampleNotification);
  }

  @Test
  public void testDeleteNotification_SecurityException() {
    // Create another user.
    User otherUser = User.builder()
        .firstName("Other")
        .lastName("User")
        .email("other@example.com")
        .password("dummy")
        .build();
    ReflectionTestUtils.setField(otherUser, "id", 2L);
    when(notificationRepository.findById(100L))
        .thenReturn(java.util.Optional.of(sampleNotification));
    // Expect a SecurityException cause other user tries to delete
    assertThrows(SecurityException.class, () -> {
      notificationService.deleteNotification(100L, otherUser);
    });
  }

  @Test
  public void testDeleteAll() {
    List<Notification> notifications = Collections.singletonList(sampleNotification);
    when(notificationRepository.findByRecipient(sampleUser)).thenReturn(notifications);

    notificationService.deleteAll(sampleUser);
    // Verify that repository.deleteAll is called with the notifications
    verify(notificationRepository, times(1)).deleteAll(notifications);
  }
}
