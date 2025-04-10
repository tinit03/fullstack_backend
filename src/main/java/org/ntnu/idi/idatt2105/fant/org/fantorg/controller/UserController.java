package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.UserMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user-related operations.
 * <p>
 * This controller provides endpoints to retrieve connected users, fetch the authenticated user's details,
 * update profile pictures and passwords, and delete a user.
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * Retrieves a list of connected users.
   * <p>
   * This endpoint is accessible only to users with the ADMIN role.
   * </p>
   *
   * @return a ResponseEntity containing a list of UserDto objects representing connected users
   */
  @Operation(
      summary = "Find Connected Users",
      description = "Retrieves a list of all connected users. Access is restricted to ADMIN users."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Connected users retrieved successfully")
  })
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDto>> findConnectedUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  /**
   * Retrieves the details of the currently authenticated user.
   *
   * @param user the authenticated user obtained from the security context
   * @return a ResponseEntity containing the UserDto with the details of the current user
   */
  @Operation(
      summary = "Get Current User",
      description = "Retrieves the profile information of the currently authenticated user."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current user retrieved successfully")
  })
  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.findById(user.getId()));
  }

  /**
   * Updates the profile picture of the currently authenticated user.
   *
   * @param dto  the ImageUploadDto containing the URL of the new profile picture
   * @param user the authenticated user whose profile picture is being updated
   * @return a ResponseEntity containing the updated UserDto
   */
  @Operation(
      summary = "Upload Profile Picture",
      description = "Updates the profile picture of the authenticated user using the provided image URL."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile picture updated successfully")
  })
  @PostMapping("/profilePicture")
  public ResponseEntity<UserDto> uploadProfilePicture(
      @RequestBody @Validated ImageUploadDto dto,
      @AuthenticationPrincipal User user
  ) {
    User updatedUser = userService.updateProfilePicture(dto.getUrl(), user);
    return ResponseEntity.ok(UserMapper.toDto(updatedUser));
  }

  /**
   * Updates the password of the currently authenticated user.
   *
   * @param dto  the UpdatePasswordDto containing the new password details
   * @param user the authenticated user whose password is being updated
   * @return a ResponseEntity containing a success message if the password is updated successfully
   */
  @Operation(
      summary = "Update Password",
      description = "Updates the password for the authenticated user using the provided password details."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password updated successfully")
  })
  @PutMapping("/updatePassword")
  public ResponseEntity<String> updatePassword(
      @RequestBody @Validated UpdatePasswordDto dto,
      @AuthenticationPrincipal User user
  ) {
    userService.updatePassword(user, dto);
    return ResponseEntity.ok("Password changed");
  }

  /**
   * Deletes the currently authenticated user's account.
   * <p>
   * This endpoint also clears the refresh token stored in the browser cookies.
   * </p>
   *
   * @param user     the authenticated user who is to be deleted
   * @param response the HttpServletResponse used to clear the refresh token cookie
   * @return a ResponseEntity containing a message indicating the result of the deletion operation
   */
  @Operation(
      summary = "Delete User",
      description = "Deletes the currently authenticated user and clears associated session cookies."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User deleted successfully"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "500", description = "Error occurred during deletion")
  })
  @DeleteMapping("/me")
  public ResponseEntity<String> deleteUser(
      @AuthenticationPrincipal User user,
      HttpServletResponse response
  ) {
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
    try {
      userService.deleteUser(user.getId());

      Cookie refreshCookie = new Cookie("refreshToken", null);
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(false);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(0);
      response.addCookie(refreshCookie);

      return ResponseEntity.ok("User deleted successfully");
    } catch (UserNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error deleting user: " + ex.getMessage());
    }
  }
}
