package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.JwtTokenDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserRegisterDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthenticationIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private UserService userService;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private ObjectMapper objectMapper; // For JSON conversion

  @BeforeEach
  public void setUp() {
    // Clean-up before each test.
    userRepository.findAll().forEach(user -> userService.deleteUser(user.getId()));
  }

  @Test
  public void testRegisterSetsRefreshCookieAndReturnsAccessToken() throws Exception {
    // Create a new user registration payload
    UserRegisterDto registerDto = new UserRegisterDto();
    registerDto.setFirstName("Test");
    registerDto.setLastName("User");
    registerDto.setEmail("register@example.com");
    registerDto.setPassword("password");

    String requestBody = objectMapper.writeValueAsString(registerDto);

    // Perform the registration request
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isOk())
            // Expect the response to contain an access token
            .andExpect(jsonPath("$.token").exists())
            // Check if a Set-Cookie header to be present.
            .andExpect(header().exists("Set-Cookie"))
            .andReturn();

    String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
    assertThat(setCookieHeader).contains("refreshToken=");
  }

  @Test
  public void testLoginSetsRefreshCookieAndReturnsAccessToken() throws Exception {
    // First, register a user
    User user = new User();
    user.setFirstName("Login");
    user.setLastName("Test");
    user.setEmail("login@example.com");
    user.setPassword(passwordEncoder.encode("password"));
    user.setRole(Role.USER);
    userRepository.save(user);

    // Try to log in with the user
    UserLoginDto loginDto = new UserLoginDto();
    loginDto.setEmail("login@example.com");
    loginDto.setPassword("password");

    String requestBody = objectMapper.writeValueAsString(loginDto);

    // Perform the login request.
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(header().exists("Set-Cookie"))
            .andReturn();
    // We should check if the user retrieves a refreshtoken in cookie
    String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
    assertThat(setCookieHeader).contains("refreshToken=");
  }

  @Test
  public void testRefreshEndpointReturnsNewAccessToken() throws Exception {
    // First, register user.
    UserRegisterDto registerDto = new UserRegisterDto();
    registerDto.setFirstName("Refresh");
    registerDto.setLastName("Test");
    registerDto.setEmail("refresh@example.com");
    registerDto.setPassword("password");

    String registerRequest = objectMapper.writeValueAsString(registerDto);
    MvcResult loginResult =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerRequest))
            .andExpect(status().isOk())
            .andReturn();
    // Register user and check if we get refreshToken.
    String setCookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
    String refreshToken = extractCookieValue(setCookieHeader, "refreshToken");
    assertThat(refreshToken).isNotEmpty();

    MvcResult refreshResult =
        mockMvc
            .perform(post("/auth/refresh").cookie(new Cookie("refreshToken", refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn();

    // We should get a token, when we call refresh
    String jsonResponse = refreshResult.getResponse().getContentAsString();
    JwtTokenDto tokenDto = objectMapper.readValue(jsonResponse, JwtTokenDto.class);
    assertThat(tokenDto.getToken()).isNotEmpty();
  }

  @Test
  public void testLogoutClearsRefreshCookie() throws Exception {
    // Register and login a user
    UserRegisterDto registerDto = new UserRegisterDto();
    registerDto.setFirstName("Logout");
    registerDto.setLastName("Test");
    registerDto.setEmail("logout@example.com");
    registerDto.setPassword("password");

    String registerRequest = objectMapper.writeValueAsString(registerDto);
    MvcResult loginResult =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerRequest))
            .andExpect(status().isOk())
            .andReturn();

    // Extract refresh token cookie from login response
    String setCookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
    String refreshToken = extractCookieValue(setCookieHeader, "refreshToken");
    assertThat(refreshToken).isNotEmpty();

    String jsonResponse = loginResult.getResponse().getContentAsString();
    JwtTokenDto jwtTokenDto = objectMapper.readValue(jsonResponse, JwtTokenDto.class);
    String accessToken = jwtTokenDto.getToken();
    assertThat(accessToken).isNotEmpty();

    MvcResult logoutResult =
        mockMvc
            .perform(
                post("/auth/logout")
                    .header("Authorization", "Bearer " + accessToken)
                    .cookie(new Cookie("refreshToken", refreshToken))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String logoutCookieHeader = logoutResult.getResponse().getHeader("Set-Cookie");
    // Verify that the refresh token cookie has been cleared
    assertThat(logoutCookieHeader).contains("Max-Age=0");
  }

  // Helper method to extract a cookie's value from a Set-Cookie header.
  private String extractCookieValue(String setCookieHeader, String cookieName) {
    if (setCookieHeader == null || cookieName == null) return null;
    // Example: "refreshToken=abc123; Path=/; HttpOnly; Max-Age=604800; SameSite=Lax"
    for (String part : setCookieHeader.split(";")) {
      part = part.trim();
      if (part.startsWith(cookieName + "=")) {
        return part.substring((cookieName + "=").length());
      }
    }
    return null;
  }
}
