package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.AuthenticationResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserRegisterDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  public AuthenticationResponse registerUser(UserRegisterDto dto) {
    if(userRepository.existsByEmail(dto.getEmail())){
      throw new IllegalArgumentException("Email already in use!");
    }
    User user = new User();
    user.setRole(Role.USER);
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    userRepository.save(user);
    return authenticateAndGenerateToken(new UserLoginDto(dto.getEmail(),dto.getPassword()));
  }
  public AuthenticationResponse authenticateAndGenerateToken(UserLoginDto loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword()
        )
    );
    User user = (User) authentication.getPrincipal();
    String accessToken = jwtService.generateToken(user,30);
    String refreshToken = refreshTokenService.createToken(user).getToken();
    return new AuthenticationResponse(accessToken,refreshToken);
  }
}
