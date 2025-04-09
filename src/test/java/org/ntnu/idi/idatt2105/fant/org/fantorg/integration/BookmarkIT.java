package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import static org.assertj.core.api.Assertions.assertThat;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BookmarkRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookmarkIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private BookmarkRepository bookmarkRepository;
  private User seller;
  private User buyer;
  private Item item;


  @BeforeEach
  public void setUp() {
    // Clean-up before each test
    bookmarkRepository.deleteAll();
    itemRepository.deleteAll();
    userRepository.deleteAll();
    categoryRepository.deleteAll();


    Category parentCategory = new Category();
    parentCategory.setCategoryName("Parent Category");
    parentCategory = categoryRepository.save(parentCategory);


    Category subCategory = new Category();
    subCategory.setCategoryName("Sub Category");
    subCategory.setParentCategory(parentCategory);
    subCategory = categoryRepository.save(subCategory);

    seller = new User();
    seller.setFirstName("Seller");
    seller.setLastName("User");
    seller.setEmail("seller@example.com");
    seller.setPassword(passwordEncoder.encode("password"));
    seller.setRole(Role.USER);
    seller = userRepository.save(seller);

    buyer = new User();
    buyer.setFirstName("Buyer");
    buyer.setLastName("User");
    buyer.setEmail("buyer@example.com");
    buyer.setPassword(passwordEncoder.encode("password"));
    buyer.setRole(Role.USER);
    buyer = userRepository.save(buyer);
    System.out.println("Buyer ID: " + buyer.getId());

    item = new Item();
    item.setTitle("Test Item");
    item.setDescription("A test item");
    item.setPrice(new BigDecimal("100.00"));
    item.setSeller(seller);
    item.setStatus(Status.ACTIVE);
    item = itemRepository.save(item);
    item.setSubCategory(subCategory);
    item = itemRepository.save(item);
  }

  @Test
  public void testBookmarkItem() throws Exception {
    // Buyer bookmarks the item
    mockMvc.perform(post("/bookmark/{itemId}", item.getItemId())
            .with(user(buyer)))
        .andExpect(status().isOk());


    // Verify by fetching bookmarked items
    MvcResult result = mockMvc.perform(get("/bookmark/me")
            .with(user(buyer))
            .param("page", "0")
            .param("size", "10")
            .param("date", "desc"))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("Test Item");
  }

  @Test
  public void testRemoveBookmark() throws Exception {
    // First bookmark the item.
    mockMvc.perform(post("/bookmark/{itemId}", item.getItemId())
            .with(user(buyer)))
        .andExpect(status().isOk());

    // After remove the bookmark.
    mockMvc.perform(delete("/bookmark/{itemId}", item.getItemId())
            .with(user(buyer)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testGetBookmarkedItems() throws Exception {
    // Bookmark the item first
    mockMvc.perform(post("/bookmark/{itemId}", item.getItemId())
            .with(user(buyer)))
        .andExpect(status().isOk());

    // Retrieve the bookmarked items
    MvcResult result = mockMvc.perform(get("/bookmark/me")
            .with(user(buyer))
            .param("page", "0")
            .param("size", "10")
            .param("date", "desc"))
        .andExpect(status().isOk())
        .andReturn();

    // Check the response contains the correct item
    String jsonResponse = result.getResponse().getContentAsString();
    assertThat(jsonResponse).contains("Test Item");
  }
}
