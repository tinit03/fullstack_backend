package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status.SOLD;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchFilter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemStatusUpdate;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ItemRepository itemRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User seller;
  private User buyer;
  private Category category;
  private Item item;

  @BeforeEach
  public void setUp() {
    // Clear existing seeded data
    itemRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    Category parentCat = new Category();
    parentCat.setCategoryName("Parent Category");
    parentCat = categoryRepository.save(parentCat);

    category = new Category();
    category.setCategoryName("SubCategory");
    category.setParentCategory(parentCat);
    category = categoryRepository.save(category);

    seller = new User();
    seller.setFirstName("Seller");
    seller.setLastName("User");
    seller.setEmail("seller@example.com");
    seller.setPassword(passwordEncoder.encode("password"));
    seller.setRole(Role.USER);
    seller = userRepository.save(seller);

    // Create a buyer user
    buyer = new User();
    buyer.setFirstName("Buyer");
    buyer.setLastName("User");
    buyer.setEmail("buyer@example.com");
    buyer.setPassword(passwordEncoder.encode("password"));
    buyer.setRole(Role.USER);
    buyer = userRepository.save(buyer);

    item = new Item();
    item.setTitle("Test Item");
    item.setDescription("A test item for integration testing");
    item.setPrice(new BigDecimal("100.00"));
    item.setSeller(seller);
    item.setStatus(Status.ACTIVE);
    item.setSubCategory(category);
    item.setLocation(new Location("1234", "SomeCounty", "SomeCity", "0.0", "0.0"));
    item.setListingType(ListingType.BID);
    item.setCondition(Condition.NEW);
    item = itemRepository.save(item);
  }

  @Test
  public void testSearchItems() throws Exception {
    ItemSearchFilter filter = new ItemSearchFilter();
    filter.setKeyword("Test");

    MvcResult result =
        mockMvc
            .perform(
                get("/items/search")
                    .with(user(buyer))
                    .param("keyword", "Test")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortField", "publishedAt")
                    .param("sortDir", "desc"))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("Test Item");
  }

  @Test
  public void testGetAllItems() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/items")
                    .with(user(buyer))
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortField", "publishedAt")
                    .param("sortDir", "desc")
                    .param("status", "ACTIVE"))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("Test Item");
  }

  @Test
  public void testGetItemDetail() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/items/{id}", item.getItemId()).with(user(buyer)))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("Test Item");
  }

  @Test
  public void testCreateItem() throws Exception {
    ItemCreateDto createDto = new ItemCreateDto();
    createDto.setItemName("New Item");
    createDto.setDescription("A newly created item");
    createDto.setPrice(new BigDecimal("200.00"));
    createDto.setSubcategoryId(category.getCategoryId());
    createDto.setPostalCode("1723");

    createDto.setCondition(Condition.LIKE_NEW);
    createDto.setListingType(ListingType.BID);
    // We do not send any images for simplicity
    createDto.setImages(null);

    String requestBody = objectMapper.writeValueAsString(createDto);

    MvcResult result =
        mockMvc
            .perform(
                post("/items")
                    .with(user(seller))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ItemDto responseDto = objectMapper.readValue(jsonResponse, ItemDto.class);
    assertThat(responseDto.getName()).isEqualTo("New Item");
  }

  @Test
  public void testUpdateItem() throws Exception {
    // Prepare an update payload.
    ItemEditDto editDto = new ItemEditDto();
    editDto.setItemName("Updated Test Item");
    editDto.setDescription("Updated description");
    editDto.setPrice(new BigDecimal("150.00"));
    editDto.setSubcategoryId(category.getCategoryId());
    editDto.setPostalCode("1723");
    editDto.setCondition(Condition.NEW);
    editDto.setListingType(ListingType.CONTACT);
    editDto.setImages(null);
    editDto.setStatus(Status.INACTIVE);
    String requestBody = objectMapper.writeValueAsString(editDto);

    MvcResult result =
        mockMvc
            .perform(
                put("/items/{itemId}", item.getItemId())
                    .with(user(seller))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ItemDto updatedDto = objectMapper.readValue(jsonResponse, ItemDto.class);
    assertThat(updatedDto.getName()).isEqualTo("Updated Test Item");
  }

  @Test
  public void testUpdateStatus() throws Exception {
    ItemStatusUpdate statusUpdate = new ItemStatusUpdate();
    statusUpdate.setStatus(SOLD);

    String requestBody = objectMapper.writeValueAsString(statusUpdate);

    // Update the item's status using the seller
    MvcResult result =
        mockMvc
            .perform(
                put("/items/{itemId}/status", item.getItemId())
                    .with(user(seller))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ItemDto statusDto = objectMapper.readValue(jsonResponse, ItemDto.class);
    assertThat(statusDto.getStatus()).isEqualTo(SOLD);
  }

  @Test
  public void testDeleteItem() throws Exception {
    mockMvc
        .perform(delete("/items/{itemId}", item.getItemId()).with(user(seller)))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/items/{id}", item.getItemId()).with(user(buyer)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetOwnItems() throws Exception {
    ItemCreateDto createDto = new ItemCreateDto();
    createDto.setItemName("Seller's Second Item");
    createDto.setDescription("Second item description");
    createDto.setPrice(new BigDecimal("300.00"));
    createDto.setSubcategoryId(category.getCategoryId());
    createDto.setPostalCode("1723");
    createDto.setImages(null);
    createDto.setListingType(ListingType.DIRECT);
    createDto.setCondition(Condition.GOOD);
    createDto.setStatus(Status.ACTIVE);
    mockMvc
        .perform(
            post("/items")
                .with(user(seller))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated());

    MvcResult result =
        mockMvc
            .perform(
                get("/items/me")
                    .with(user(seller))
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortField", "publishedAt")
                    .param("sortDir", "desc"))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("Test Item", "Seller's Second Item");
  }
}
