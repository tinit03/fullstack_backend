package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.*;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.*;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.*;
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
public class BiddingIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private ItemRepository itemRepository;
  @Autowired private BidRepository bidRepository;
  @Autowired private CategoryRepository categoryRepository;

  @Autowired private OrderRepository orderRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private User seller;
  private User buyer;
  private Item item;

  @BeforeEach
  public void setup() {
    bidRepository.deleteAll();
    orderRepository.deleteAll();
    itemRepository.deleteAll();
    userRepository.deleteAll();
    categoryRepository.deleteAll();

    Category category = new Category();
    category.setCategoryName("Electronics");
    category = categoryRepository.save(category);

    Category sub = new Category();
    sub.setCategoryName("Laptops");
    sub.setParentCategory(category);
    sub = categoryRepository.save(sub);

    seller = new User();
    seller.setFirstName("Seller");
    seller.setLastName("Example");
    seller.setEmail("seller@example.com");
    seller.setPassword(passwordEncoder.encode("oassword"));
    seller.setRole(Role.USER);

    seller = userRepository.save(seller);

    buyer = new User();
    buyer.setFirstName("Buyer");
    buyer.setLastName("Example");
    buyer.setEmail("buyer@example.com");
    buyer.setPassword(passwordEncoder.encode("oassword"));
    buyer.setRole(Role.USER);

    buyer = userRepository.save(buyer);

    item = new Item();
    item.setTitle("Gaming Laptop");
    item.setDescription("Fast and sleek");
    item.setPrice(new BigDecimal("1000"));
    item.setCondition(Condition.NEW);
    item.setStatus(Status.ACTIVE);
    item.setListingType(ListingType.BID);
    item.setSeller(seller);
    item.setSubCategory(sub);
    item = itemRepository.save(item);
  }

  @Test
  public void testPlaceBidAndGetBids() throws Exception {
    BidCreateDto dto = new BidCreateDto();
    dto.setItemId(item.getItemId());
    dto.setAmount(new BigDecimal("1100"));

    // Place bid
    MvcResult bidResult =
        mockMvc
            .perform(
                post("/bids")
                    .with(user(buyer))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn();

    String json = bidResult.getResponse().getContentAsString();
    BidDto bidDto = objectMapper.readValue(json, BidDto.class);

    assertThat(bidDto.getAmount()).isEqualTo(new BigDecimal("1100"));
    assertThat(bidDto.getItemId()).isEqualTo(item.getItemId());

    // Fetch bids for item
    MvcResult getResult =
        mockMvc
            .perform(get("/bids/item/{itemId}", item.getItemId()).with(user(seller)))
            .andExpect(status().isOk())
            .andReturn();

    String getJson = getResult.getResponse().getContentAsString();
    assertThat(getJson).contains("1100");
  }

  @Test
  public void testAcceptBid() throws Exception {
    // First place a bid
    BidCreateDto dto = new BidCreateDto();
    dto.setItemId(item.getItemId());
    dto.setAmount(new BigDecimal("900"));

    MvcResult bidResult =
        mockMvc
            .perform(
                post("/bids")
                    .with(user(buyer))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn();

    BidDto bidDto =
        objectMapper.readValue(bidResult.getResponse().getContentAsString(), BidDto.class);

    // Accept the bid as seller
    MvcResult acceptResult =
        mockMvc
            .perform(post("/bids/" + bidDto.getId() + "/accept").with(user(seller)))
            .andExpect(status().isOk())
            .andReturn();

    String response = acceptResult.getResponse().getContentAsString();
    assertThat(response).contains("\"itemId\":" + item.getItemId());
  }
}
