package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.junit.jupiter.api.TestInstance;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ItemRepository itemRepository;

  private User admin;

  private User user;



  @BeforeEach
  public void setUp() {
    itemRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();
    // Create admin user
    admin = new User();
    admin.setFirstName("Admin");
    admin.setLastName("User");
    admin.setEmail("Admin@example.com");
    admin.setPassword(passwordEncoder.encode("password"));
    admin.setRole(Role.ADMIN);
    admin = userRepository.save(admin);

    user = new User();
    user.setFirstName("NonAdmin");
    user.setLastName("User");
    user.setEmail("NonAdmin@example.com");
    user.setPassword(passwordEncoder.encode("password"));
    user.setRole(Role.USER);
    System.out.println(user.getAuthorities());
    user = userRepository.save(user);
  }

  @Test
  public void testCreateCategoryAsAdmin() throws Exception {
    // Create a new parent category payload.
    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("AdminCategory");

    String requestBody = objectMapper.writeValueAsString(dto);

    // Simulate an admin user using user(...) with "ADMIN" role
    MvcResult result = mockMvc.perform(post("/categories")
            .with(user(admin))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    CategoryDto responseDto = objectMapper.readValue(jsonResponse, CategoryDto.class);
    assertThat(responseDto.getName()).isEqualTo("AdminCategory");
  }

  @Test
  public void testCreateCategoryAsNonAdmin() throws Exception {
    // Non-admin user should get forbidden
    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("UserCategory");

    String requestBody = objectMapper.writeValueAsString(dto);

    mockMvc.perform(post("/categories")
            .with(user(user))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isForbidden());
  }

  @Test
  public void testDeleteCategoryAsAdmin() throws Exception {
    // First, create a category as admin
    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("DeleteCategory");
    String requestBody = objectMapper.writeValueAsString(dto);

    MvcResult createResult = mockMvc.perform(post("/categories")
            .with(user(admin))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();

    CategoryDto createdCategory = objectMapper.readValue(createResult.getResponse().getContentAsString(), CategoryDto.class);

    // Now, delete the category as admin
    mockMvc.perform(delete("/categories/{id}", createdCategory.getId())
            .with(user(admin)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testDeleteCategoryAsNonAdmin() throws Exception {
    // First, create a category as admin
    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("ProtectedCategory");
    String requestBody = objectMapper.writeValueAsString(dto);

    MvcResult createResult = mockMvc.perform(post("/categories")
            .with(user(admin))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();

    CategoryDto createdCategory = objectMapper.readValue(createResult.getResponse().getContentAsString(), CategoryDto.class);

    mockMvc.perform(delete("/categories/{id}", createdCategory.getId())
            .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void testGetAllCategoriesIsPublic() throws Exception {

    CategoryCreateDto dto1 = new CategoryCreateDto();
    dto1.setName("CategoryOne");
    String req1 = objectMapper.writeValueAsString(dto1);
    mockMvc.perform(post("/categories")
            .with(user(admin))
            .contentType(MediaType.APPLICATION_JSON)
            .content(req1))
        .andExpect(status().isOk());

    CategoryCreateDto dto2 = new CategoryCreateDto();
    dto2.setName("CategoryTwo");
    String req2 = objectMapper.writeValueAsString(dto2);
    mockMvc.perform(post("/categories")
            .with(user(admin))
            .contentType(MediaType.APPLICATION_JSON)
            .content(req2))
        .andExpect(status().isOk());


    MvcResult result = mockMvc.perform(get("/categories")
            .param("includeSubCategories", "false"))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("CategoryOne", "CategoryTwo");
  }
}
