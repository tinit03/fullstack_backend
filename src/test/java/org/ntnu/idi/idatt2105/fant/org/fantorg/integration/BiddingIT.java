package org.ntnu.idi.idatt2105.fant.org.fantorg.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.JwtTokenDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserRegisterDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.integration.util.TestHelpers;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class BiddingIT {
  private static final Logger logger = LoggerFactory.getLogger(BiddingIT.class);

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;


  @BeforeAll
  public void setUpUsers() {
    TestHelpers.registerUser(restTemplate,baseUrl(),"seller@example.com", "password", "Seller", "One", "oslo");
    TestHelpers.registerUser(restTemplate,baseUrl(),"buyer@example.com", "password", "Buyer", "Two", "bergen");}
  private String baseUrl() {
    return "http://localhost:" + port;
  }

  @Test
  public void testBiddingFlow() {
    // 1. Register and Login as seller and buyer

    String sellerToken = TestHelpers.loginAndGetToken(restTemplate,baseUrl(),"seller@example.com", "password");
    String buyerToken = TestHelpers.loginAndGetToken(restTemplate,baseUrl(),"buyer@example.com", "password");
    Long subCategoryId = TestHelpers.createCategoryAndSubcategory(restTemplate,baseUrl(),"Collectibles", "Antiques", sellerToken);

    // 2. Seller creates an item (listing type set to BID)
    ItemCreateDto itemCreateDto = new ItemCreateDto();
    itemCreateDto.setItemName("Antique Vase");
    itemCreateDto.setDescription("A beautiful, rare antique vase in excellent condition.");
    itemCreateDto.setPrice(new BigDecimal("150.00"));
    itemCreateDto.setSubcategoryId(subCategoryId);
    itemCreateDto.setCondition(Condition.GOOD);
    itemCreateDto.setPostalCode("0150");
    itemCreateDto.setListingType(ListingType.BID); // ensure this field exists in your DTO
    itemCreateDto.setStatus(Status.ACTIVE);
    // Set other required fields as needed...

    HttpHeaders sellerHeaders = new HttpHeaders();
    sellerHeaders.setBearerAuth(sellerToken);
    sellerHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ItemCreateDto> itemRequest = new HttpEntity<>(itemCreateDto, sellerHeaders);

    ResponseEntity<ItemDto> itemResponse = restTemplate.exchange(
        baseUrl() + "/items",
        HttpMethod.POST,
        itemRequest,
        ItemDto.class
    );
    assertEquals(HttpStatus.CREATED, itemResponse.getStatusCode());
    assertNotNull(itemResponse.getBody());
    Long itemId = itemResponse.getBody().getId();
    logger.info("Seller created item with id: {} and title: {}", itemId, itemResponse.getBody().getName());

    // 3. Buyer places a bid on the created item
    BidCreateDto bidCreateDto = new BidCreateDto();
    bidCreateDto.setItemId(itemId);
    bidCreateDto.setAmount(new BigDecimal("160.00"));

    HttpHeaders buyerHeaders = new HttpHeaders();
    buyerHeaders.setBearerAuth(buyerToken);
    buyerHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<BidCreateDto> bidRequest = new HttpEntity<>(bidCreateDto, buyerHeaders);

    ResponseEntity<BidDto> bidResponse = restTemplate.exchange(
        baseUrl() + "/bids",
        HttpMethod.POST,
        bidRequest,
        BidDto.class
    );
    assertEquals(HttpStatus.OK, bidResponse.getStatusCode());
    assertNotNull(bidResponse.getBody());
    Long bidId = bidResponse.getBody().getId();
    logger.info("Buyer placed bid with id: {} on item: {}", bidId, itemId);

    // 4. Seller accepts the bid, which creates an order
    HttpEntity<Void> acceptRequest = new HttpEntity<>(sellerHeaders);
    ResponseEntity<OrderDto> orderResponse = restTemplate.exchange(
        baseUrl() + "/bids/" + bidId + "/accept",
        HttpMethod.POST,
        acceptRequest,
        OrderDto.class
    );
    assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
    OrderDto orderDto = orderResponse.getBody();
    assertNotNull(orderDto);
    assertEquals(itemId, orderDto.getItemId());
    logger.info("Seller accepted bid. Order created with id: {}", orderDto.getId());
  }

  @Test
  public void testUnauthorizedBidAcceptance() {
    // 1. Register and Login as seller and buyer
    String sellerToken = TestHelpers.loginAndGetToken(restTemplate,baseUrl(),"seller@example.com", "password");
    String buyerToken = TestHelpers.loginAndGetToken(restTemplate,baseUrl(),"buyer@example.com", "password");
    Long subCategoryId = TestHelpers.createCategoryAndSubcategory(restTemplate,baseUrl(),"Collectibles", "Antiques", sellerToken);

    // 2. Seller creates an item (listing type set to BID)
    ItemCreateDto itemCreateDto = new ItemCreateDto();
    itemCreateDto.setItemName("Antique Vase");
    itemCreateDto.setDescription("A beautiful, rare antique vase in excellent condition.");
    itemCreateDto.setPrice(new BigDecimal("150.00"));
    itemCreateDto.setSubcategoryId(subCategoryId);itemCreateDto.setPostalCode("0150");
    itemCreateDto.setListingType(ListingType.BID); // ensure this field exists in your DTO
    itemCreateDto.setStatus(Status.ACTIVE);
    itemCreateDto.setCondition(Condition.GOOD);
    HttpHeaders sellerHeaders = new HttpHeaders();
    sellerHeaders.setBearerAuth(sellerToken);
    sellerHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ItemCreateDto> itemRequest = new HttpEntity<>(itemCreateDto, sellerHeaders);

    ResponseEntity<ItemDto> itemResponse = restTemplate.exchange(
        baseUrl() + "/items",
        HttpMethod.POST,
        itemRequest,
        ItemDto.class
    );
    assertEquals(HttpStatus.CREATED, itemResponse.getStatusCode());
    assertNotNull(itemResponse.getBody());
    Long itemId = itemResponse.getBody().getId();
    logger.info("Seller created item with id: {} and title: {}", itemId, itemResponse.getBody().getName());

    // 3. Buyer places a bid on the created item
    BidCreateDto bidCreateDto = new BidCreateDto();
    bidCreateDto.setItemId(itemId);
    bidCreateDto.setAmount(new BigDecimal("160.00"));

    HttpHeaders buyerHeaders = new HttpHeaders();
    buyerHeaders.setBearerAuth(buyerToken);
    buyerHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<BidCreateDto> bidRequest = new HttpEntity<>(bidCreateDto, buyerHeaders);

    ResponseEntity<BidDto> bidResponse = restTemplate.exchange(
        baseUrl() + "/bids",
        HttpMethod.POST,
        bidRequest,
        BidDto.class
    );
    assertEquals(HttpStatus.OK, bidResponse.getStatusCode());
    assertNotNull(bidResponse.getBody());
    Long bidId = bidResponse.getBody().getId();
    logger.info("Buyer placed bid with id: {} on item: {}", bidId, itemId);

    // Buyer tries to accept the bid as a non-seller
    HttpHeaders nonSeller = new HttpHeaders();
    nonSeller.setBearerAuth(buyerToken);
    HttpEntity<Void> unauthorizedRequest = new HttpEntity<>(nonSeller);

    ResponseEntity<String> response = restTemplate.exchange(
        baseUrl() + "/bids/" + bidId + "/accept",
        HttpMethod.POST,
        unauthorizedRequest,
        String.class
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
}
