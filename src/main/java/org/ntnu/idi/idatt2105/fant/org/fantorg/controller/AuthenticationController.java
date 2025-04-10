package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * REST controller for handling authentication related operations such as login,
 * registration, password reset, token refresh, and logout.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService userService;
  private final PasswordResetService resetService;
  private final EmailService emailService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  /**
   * Authenticates a user with the provided credentials and generates access and refresh tokens.
   * The refresh token is stored in an HttpOnly cookie.
   *
   * @param loginRequest the login credentials
   * @param response the HTTP servlet response
   * @return a JWT token DTO with the access token if authentication is successful;
   *         otherwise, an unauthorized response is returned.
   */
  @Operation(summary = "User Login",
      description = "Authenticates the user with provided credentials and returns a JWT access token. "
          + "The refresh token is set as an HttpOnly cookie.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto loginRequest, HttpServletResponse response) {
    try {
      AuthenticationResponse tokens = userService.authenticateAndGenerateToken(loginRequest);
      Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
      refreshCookie.setSecure(false); // Using HTTP only; set true for HTTPS
      refreshCookie.setAttribute("SameSite", "Lax");

      response.addCookie(refreshCookie);
      return ResponseEntity.ok(new JwtTokenDto(tokens.getAccessToken()));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid credentials");
    }
  }

  /**
   * Registers a new user with the provided registration data and generates corresponding JWT tokens.
   * The refresh token is stored in an HttpOnly cookie.
   *
   * @param registerDto the user registration information
   * @param response the HTTP servlet response
   * @return a JWT token DTO with the access token if registration is successful;
   *         otherwise, an unauthorized response is returned.
   */
  @Operation(summary = "User Registration",
      description = "Registers a new user and returns a JWT access token. "
          + "The refresh token is set as an HttpOnly cookie.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Registration successful"),
      @ApiResponse(responseCode = "401", description = "Registration failed due to invalid data")
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto registerDto, HttpServletResponse response) {
    try {
      AuthenticationResponse token = userService.registerUser(registerDto);
      Cookie refreshCookie = new Cookie("refreshToken", token.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(false);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60);
      refreshCookie.setAttribute("SameSite", "Lax");

      response.addCookie(refreshCookie);
      return ResponseEntity.ok(new JwtTokenDto(token.getAccessToken()));
    } catch (BadCredentialsException ex){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Something happened: " + ex);
    }
  }

  /**
   * Initiates the forgot-password process by generating a password reset token for the user
   * and sending a reset email.
   *
   * @param dto the forgot password data transfer object containing the user's email
   * @return a response entity with a confirmation message if the email was sent successfully
   */
  @Operation(summary = "Forgot Password",
      description = "Generates a password reset token and sends an email with reset instructions to the user.")
  @ApiResponse(responseCode = "200", description = "Password reset email sent")
  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDto dto) {
    PasswordResetToken token = resetService.createTokenForUser(dto);
    emailService.sendPasswordResetEmail(dto.getEmail(), token.getToken());
    return ResponseEntity.ok("sent mail");
  }

  /**
   * Validates a password reset token for the provided email.
   *
   * @param token the password reset token to validate
   * @param email the email address associated with the reset token
   * @return a TokenValidDto indicating whether the token is valid and a corresponding message,
   *         or a bad request with an error message if invalid.
   */
  @Operation(summary = "Validate Reset Token",
      description = "Validates the password reset token provided via query parameters, "
          + "ensuring it is valid for the provided email address.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token is valid"),
      @ApiResponse(responseCode = "400", description = "Invalid or expired token")
  })
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

  /**
   * Resets the password for a user using the provided reset password data.
   *
   * @param dto the reset password data transfer object containing the new password and token details
   * @return a response entity with a success message if the password was reset; otherwise, a bad request with an error message.
   */
  @Operation(summary = "Reset Password",
      description = "Resets the user's password using the provided token and new password.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password successfully reset"),
      @ApiResponse(responseCode = "400", description = "Invalid token or password reset error")
  })
  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto dto) {
    try {
      resetService.resetPassword(dto);
      return ResponseEntity.ok("Password successfully reset");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Generates a new JWT access token based on a valid refresh token that is sent as an HttpOnly cookie.
   *
   * @param request the HTTP servlet request containing the cookie
   * @return a JWT token DTO with a new access token if the refresh token is valid;
   *         otherwise, an unauthorized response is returned.
   */
  @Operation(summary = "Refresh Token",
      description = "Generates a new access token using the refresh token provided in the cookies.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New access token generated"),
      @ApiResponse(responseCode = "401", description = "No refresh token provided or refresh token invalid")
  })
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

  /**
   * Logs out the authenticated user by invalidating the refresh token and clearing its cookie.
   *
   * @param user the currently authenticated user (obtained via Spring Security)
   * @param response the HTTP servlet response used to clear the refresh token cookie
   * @return a response entity with a success message if logout is successful;
   *         otherwise, an error response if the user is not authenticated or not found.
   */
  @Operation(summary = "Logout",
      description = "Logs out the current user by invalidating the refresh token and clearing the cookie.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Logged out successfully"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
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

}
