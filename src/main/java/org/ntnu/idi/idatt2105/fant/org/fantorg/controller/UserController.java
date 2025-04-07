package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.UserMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
}
