package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.JwtTokenDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ForgotPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.RefreshTokenRequest;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.AuthenticationResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.ResetPasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.TokenValidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserRegisterDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.PasswordResetToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.security.JwtService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.AuthenticationService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.EmailService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.PasswordResetService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService userService;
  private final PasswordResetService resetService;
  private final EmailService emailService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;


  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto loginRequest) {
    try {
      AuthenticationResponse tokens = userService.authenticateAndGenerateToken(loginRequest);
      return ResponseEntity.ok(tokens);
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid credentials");
    }
  }
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto registerDto) {
    try {
      AuthenticationResponse token = userService.registerUser(registerDto);
      return ResponseEntity.ok(token);
    } catch (BadCredentialsException ex){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Something happened: "+ex);
    }
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDto dto) {
    PasswordResetToken token = resetService.createTokenForUser(dto);

    emailService.sendPasswordResetEmail(dto.getEmail(),token.getToken());
    return ResponseEntity.ok("sent mail");
  }

  @GetMapping("/validate-reset-token")
  public ResponseEntity<TokenValidDto> validateResetToken(
      @RequestParam("token") String token,
      @RequestParam("email") String email) {
    try {
      resetService.validateToken(token, email);
      return ResponseEntity.ok(new TokenValidDto(true, "Token is valid"));
    } catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
      return ResponseEntity.badRequest().body(new TokenValidDto(false, ex.getMessage()));
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto dto) {
    try {
      resetService.resetPassword(dto);
      return ResponseEntity.ok("Password successfully reset");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
    String requestToken = request.getRefreshToken();

    RefreshToken refreshToken = refreshTokenService.validateToken(requestToken);
    User user = refreshToken.getUser();
    String newAccessToken = jwtService.generateToken(user, 30); // 30 min access token
    return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, requestToken));
  }
}
