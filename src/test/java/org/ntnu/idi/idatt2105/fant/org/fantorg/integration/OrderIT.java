package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
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
public class OrderIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ItemRepository itemRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User seller;
  private User buyer;
  private Item item;

  @BeforeEach
  public void setUp() {
    itemRepository.deleteAll();
    userRepository.deleteAll();

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

    item = new Item();
    item.setTitle("Test Item");
    item.setDescription("A test item for order creation");
    item.setPrice(new BigDecimal("100.00"));
    item.setSeller(seller);
    item.setStatus(Status.ACTIVE);
    item.setLocation(new Location("1234", "County", "City", "0.0", "0.0"));
    item.setListingType(ListingType.BID);
    item.setCondition(Condition.NEW);
    item = itemRepository.save(item);
  }

  @Test
  public void testCreateOrder() throws Exception {
    OrderCreateDto orderCreateDto = new OrderCreateDto();
    orderCreateDto.setItemId(item.getItemId());
    String requestBody = objectMapper.writeValueAsString(orderCreateDto);

    MvcResult result =
        mockMvc
            .perform(
                post("/order/create")
                    .with(user(buyer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    OrderDto orderDto = objectMapper.readValue(jsonResponse, OrderDto.class);

    // Check that the order has the correct item, buyer, and seller.
    assertThat(orderDto.getItemId()).isEqualTo(item.getItemId());
    assertThat(orderDto.getBuyerId()).isEqualTo(buyer.getId());
    assertThat(orderDto.getSellerId()).isEqualTo(seller.getId());
  }
}
