package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.JwtTokenDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/login")
  public ResponseEntity<JwtTokenDto> login(@Valid @RequestBody UserLoginDto loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        (new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword())));

    User user = (User) authentication.getPrincipal();

    String token = jwtService.generateToken(user,30);
    return ResponseEntity.ok(new JwtTokenDto(token));

  }
}
