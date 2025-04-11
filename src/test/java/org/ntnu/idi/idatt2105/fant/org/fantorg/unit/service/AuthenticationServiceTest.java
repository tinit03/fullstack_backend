package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.AuthenticationResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserRegisterDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.security.JwtService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.AuthenticationService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private RefreshTokenService refreshTokenService;

  @InjectMocks private AuthenticationService authService;

  private User testUser;
  private UserRegisterDto registerDto;
  private UserLoginDto loginDto;

  @BeforeEach
  public void setUp() {
    testUser = new User();
    ReflectionTestUtils.setField(testUser, "id", 1L);
    testUser.setEmail("john.doe@example.com");
    testUser.setFirstName("John");
    testUser.setLastName("Doe");

    testUser.setPassword("hashedPassword");
    testUser.setRole(Role.USER);

    registerDto = new UserRegisterDto();
    registerDto.setEmail("john.doe@example.com");
    registerDto.setPassword("password123");
    registerDto.setFirstName("John");
    registerDto.setLastName("Doe");

    loginDto = new UserLoginDto();
    loginDto.setEmail("john.doe@example.com");
    loginDto.setPassword("password123");
  }

  @Test
  public void testRegisterUser_Success() {

    when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User u = invocation.getArgument(0);
              ReflectionTestUtils.setField(u, "id", 1L);
              return u;
            });

    when(jwtService.generateToken(any(User.class), eq(30L))).thenReturn("accessToken");

    RefreshToken dummyRefreshToken = new RefreshToken();
    dummyRefreshToken.setToken("refreshToken");
    when(refreshTokenService.createToken(any(User.class))).thenReturn(dummyRefreshToken);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    AuthenticationResponse response = authService.registerUser(registerDto);

    verify(userRepository).existsByEmail("john.doe@example.com");
    verify(userRepository).save(any(User.class));
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService).generateToken(any(User.class), eq(30L));
    verify(refreshTokenService).createToken(any(User.class));

    assertThat(response.getAccessToken()).isEqualTo("accessToken");
    assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
  }

  @Test
  public void testRegisterUser_EmailAlreadyExists() {
    when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);
    assertThrows(IllegalArgumentException.class, () -> authService.registerUser(registerDto));
  }

  @Test
  public void testAuthenticateAndGenerateToken_Success() {

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    when(jwtService.generateToken(testUser, 30L)).thenReturn("accessToken");

    RefreshToken dummyToken = new RefreshToken();
    dummyToken.setToken("refreshToken");
    when(refreshTokenService.createToken(testUser)).thenReturn(dummyToken);

    AuthenticationResponse response = authService.authenticateAndGenerateToken(loginDto);

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService).generateToken(testUser, 30L);
    verify(refreshTokenService).createToken(testUser);

    assertThat(response.getAccessToken()).isEqualTo("accessToken");
    assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
  }

  @Test
  public void testLogout() {
    doNothing().when(refreshTokenService).revokeToken(testUser);
    authService.logout(testUser);
    verify(refreshTokenService, times(1)).revokeToken(testUser);
  }
}
