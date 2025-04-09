package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private User admin;
  private User regularUser;

  @BeforeEach
  public void setUp() {

    //DELETE ALL ORDERS FROM THE PREVIOUS TEST
    orderRepository.deleteAll();
    userRepository.deleteAll();

    admin = User.builder()
        .firstName("Admin")
        .lastName("User")
        .email("admin@example.com")
        .password(passwordEncoder.encode("password"))
        .role(Role.ADMIN)
        .build();
    admin = userRepository.save(admin);

    regularUser = User.builder()
        .firstName("Regular")
        .lastName("User")
        .email("user@example.com")
        .password(passwordEncoder.encode("password"))
        .role(Role.USER)
        .build();
    regularUser = userRepository.save(regularUser);
  }

  @Test
  public void testFindConnectedUsersAsAdmin() throws Exception {
    // GET /users endpoint requires admin
    MvcResult result = mockMvc.perform(get("/users")
            .with(user(admin)))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("admin@example.com", "user@example.com");
  }

  @Test
  public void testFindConnectedUsersAsNonAdmin() throws Exception {
    // Non-admin should be forbidden to access /users
    mockMvc.perform(get("/users")
            .with(user(regularUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void testGetCurrentUser() throws Exception {
    // GET /me should return the currently authenticated user's details
    MvcResult result = mockMvc.perform(get("/me")
            .with(user(regularUser)))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    UserDto userDto = objectMapper.readValue(jsonResponse, UserDto.class);

    assertThat(userDto.getEmail()).isEqualTo(regularUser.getEmail());
    assertThat(userDto.getFirstName()).isEqualTo(regularUser.getFirstName());
  }

  @Test
  public void testUploadProfilePicture() throws Exception {
    // For simplicity, we send null
    ImageUploadDto imageDto = new ImageUploadDto();
    imageDto.setUrl(null);
    String requestBody = objectMapper.writeValueAsString(imageDto);

    // Call POST /profilePicture as regularUser
    MvcResult result = mockMvc.perform(post("/profilePicture")
            .with(user(regularUser))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    UserDto userDto = objectMapper.readValue(jsonResponse, UserDto.class);

    assertThat(userDto.getProfilePicture()).isNull();
  }

  @Test
  public void testUpdatePassword() throws Exception {
    UpdatePasswordDto pwdDto = new UpdatePasswordDto();
    pwdDto.setCurrentPassword("password");
    pwdDto.setNewPassword("newStrongPassword");
    String requestBody = objectMapper.writeValueAsString(pwdDto);

    // Call PUT /updatePassword as regularUser
    MvcResult result = mockMvc.perform(put("/updatePassword")
            .with(user(regularUser))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();
    // Verify the response message
    String responseMsg = result.getResponse().getContentAsString();
    assertThat(responseMsg).contains("Password changed");
    // Verify that the password was actually updated
    User updated = userRepository.findById(regularUser.getId()).orElseThrow();
    boolean matches = passwordEncoder.matches("newStrongPassword", updated.getPassword());
    assertThat(matches).isTrue();
  }

  @Test
  public void testDeleteUser() throws Exception {
    // Call DELETE /me with regularUser.
    MvcResult result = mockMvc.perform(delete("/me")
            .with(user(regularUser)))
        .andExpect(status().isOk())
        .andReturn();

    String responseMsg = result.getResponse().getContentAsString();
    assertThat(responseMsg).contains("User deleted successfully");
    // Check if the refresh token cookie is cleared.
    String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
    assertThat(setCookieHeader).contains("Max-Age=0");

    // Check also if the user is deleted from the repository
    boolean exists = userRepository.findById(regularUser.getId()).isPresent();
    assertThat(exists).isFalse();
  }
}
