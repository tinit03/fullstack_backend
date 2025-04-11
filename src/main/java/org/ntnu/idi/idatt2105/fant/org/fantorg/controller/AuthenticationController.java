package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto loginRequest, HttpServletResponse response) {
    try {
      AuthenticationResponse tokens = userService.authenticateAndGenerateToken(loginRequest);
      Cookie refreshCookie = new Cookie("refreshToken",tokens.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 dager
      refreshCookie.setSecure(false); // Setter false, ut av at vi bruker kun http
      refreshCookie.setAttribute("SameSite", "Lax"); 
      
      response.addCookie(refreshCookie);
      return ResponseEntity.ok(new JwtTokenDto(tokens.getAccessToken()));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid credentials");
    }
  }
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto registerDto, HttpServletResponse response) {
    try {
      AuthenticationResponse token = userService.registerUser(registerDto);
      Cookie refreshCookie = new Cookie("refreshToken",token.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(false);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60);
      refreshCookie.setAttribute("SameSite", "Lax");

      response.addCookie(refreshCookie);
      return ResponseEntity.ok(new JwtTokenDto(token.getAccessToken()));
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
  public ResponseEntity<?> refreshToken(HttpServletRequest request) {
    String refreshToken = null;
    
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new AuthenticationResponse("No refresh token provided", null));
    }

    RefreshToken token = refreshTokenService.validateToken(refreshToken);
    User user = token.getUser();
    String newAccessToken = jwtService.generateToken(user, 30); // 30 min access token
    return ResponseEntity.ok(new JwtTokenDto(newAccessToken));
  }
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/logout")
  public ResponseEntity<String> logout(@AuthenticationPrincipal User user, HttpServletResponse response) {
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
    try {
      userService.logout(user);
      Cookie refreshCookie = new Cookie("refreshToken", null);
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(false);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(0);
      response.addCookie(refreshCookie);
      return ResponseEntity.ok("Logged out successfully");
    } catch (EntityNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
  }

  @GetMapping("/username")
  public ResponseEntity<String> getUsername(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(user.getEmail());
  }
}
