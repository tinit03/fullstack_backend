package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.notification.NotificationDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing notifications.
 * <p>
 * Provides endpoints for retrieving, marking as read, and deleting notifications
 * for the authenticated user.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * Retrieves a paginated list of notifications for the authenticated user.
   *
   * @param user the authenticated user
   * @param page the page number for pagination (default is 0)
   * @param size the number of notifications per page (default is 10)
   * @return a ResponseEntity containing a paginated list of NotificationDto objects
   */
  @Operation(
      summary = "Get My Notifications",
      description = "Retrieves the notifications of the authenticated user in a paginated format."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
  })
  @GetMapping("/me")
  public ResponseEntity<Page<NotificationDto>> getMyNotifications(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(notificationService.getNotifications(user, pageable));
  }

  /**
   * Marks a specific notification as read.
   *
   * @param id the ID of the notification to mark as read
   * @param user the authenticated user performing the action
   * @return a ResponseEntity with no content upon successful update
   */
  @Operation(
      summary = "Mark Notification as Read",
      description = "Marks the specified notification as read for the authenticated user."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Notification marked as read successfully")
  })
  @PatchMapping("/{id}/read")
  public ResponseEntity<Void> markAsRead(
      @PathVariable Long id,
      @AuthenticationPrincipal User user
  ) {
    notificationService.markAsRead(id, user);
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes a specific notification.
   *
   * @param id the ID of the notification to delete
   * @param user the authenticated user performing the deletion
   * @return a ResponseEntity with no content upon successful deletion
   */
  @Operation(
      summary = "Delete Notification",
      description = "Deletes the specified notification for the authenticated user."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Notification deleted successfully")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable Long id,
      @AuthenticationPrincipal User user
  ) {
    notificationService.deleteNotification(id, user);
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes all notifications for the authenticated user.
   *
   * @param user the authenticated user whose notifications are to be deleted
   * @return a ResponseEntity with no content upon successful deletion
   */
  @Operation(
      summary = "Delete All Notifications",
      description = "Deletes all notifications for the authenticated user."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "All notifications deleted successfully")
  })
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteAllNotifications(
      @AuthenticationPrincipal User user
  ) {
    notificationService.deleteAll(user);
    return ResponseEntity.noContent().build();
  }
}
