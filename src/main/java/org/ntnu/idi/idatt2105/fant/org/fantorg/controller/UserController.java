package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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


@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDto>> findConnectedUsers() {
    return ResponseEntity.ok(userService.findAll());
  }
  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.findById(user.getId()));
  }

  @PostMapping("/profilePicture")
  public ResponseEntity<UserDto> uploadProfilePicture(
      @RequestBody @Validated ImageUploadDto dto,
      @AuthenticationPrincipal User user
  ) {
    User updatedUser = userService.updateProfilePicture(dto.getUrl(), user);
    return ResponseEntity.ok(UserMapper.toDto(updatedUser));
  }
  @PutMapping("/updatePassword")
  public ResponseEntity<String> updatePassword(
      @RequestBody @Validated UpdatePasswordDto dto,
      @AuthenticationPrincipal User user
  ) {
    userService.updatePassword(user,dto);
    return ResponseEntity.ok("Password changed");
  }

  @DeleteMapping("/me")
  public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user, HttpServletResponse response) {
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
