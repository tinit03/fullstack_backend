package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BidRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.BidServiceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BidServiceImplTest {

  @Mock
  private BidRepository bidRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private ChatMessageService chatMessageService;

  @InjectMocks
  private BidServiceImpl bidService;

  private User seller;
  private User bidder;
  private Item sampleItem;
  private Bid sampleBid;

  @BeforeEach
  public void setUp() {
    seller = new User();

    org.springframework.test.util.ReflectionTestUtils.setField(seller, "id", 1L);
    seller.setFirstName("Seller");
    seller.setLastName("User");
    seller.setEmail("seller@example.com");

    bidder = new User();
    org.springframework.test.util.ReflectionTestUtils.setField(bidder, "id", 2L);
    bidder.setFirstName("Bidder");
    bidder.setLastName("User");
    bidder.setEmail("bidder@example.com");


    sampleItem = new Item();
    org.springframework.test.util.ReflectionTestUtils.setField(sampleItem, "itemId", 10L);
    sampleItem.setTitle("Test Item");
    sampleItem.setDescription("Item description");
    sampleItem.setPrice(new java.math.BigDecimal("100.00"));
    sampleItem.setSeller(seller);
    sampleItem.setListingType(ListingType.BID);

    sampleItem.setStatus(Status.ACTIVE);
    sampleItem.setPublishedAt(LocalDateTime.now());

    // Create a sample bid.
    sampleBid = new Bid();
    ReflectionTestUtils.setField(sampleBid, "id", 20L);
    sampleBid.setAmount(new java.math.BigDecimal("110.00"));
    sampleBid.setBidder(bidder);
    sampleBid.setItem(sampleItem);
    sampleBid.setStatus(BidStatus.PENDING);
    sampleBid.setBidTime(LocalDateTime.now());
  }

  @Test
  public void testPlaceBid_Success() {
    BidCreateDto createDto = new BidCreateDto();
    createDto.setItemId(sampleItem.getItemId());
    createDto.setAmount(new java.math.BigDecimal("110.00"));

    when(itemRepository.findById(sampleItem.getItemId()))
        .thenReturn(Optional.of(sampleItem));

    // The seller should be the owner of the item, so bidder is placing a bid
    when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> {
      Bid bidArg = invocation.getArgument(0);
      ReflectionTestUtils.setField(bidArg, "id", 20L);
      return bidArg;
    });

    BidDto returnedDto = bidService.placeBid(createDto, bidder);

    assertThat(returnedDto).isNotNull();
    assertThat(returnedDto.getAmount()).isEqualByComparingTo(createDto.getAmount());

    verify(notificationService, times(1))
        .send(eq(sampleItem.getSeller()), any(Map.class), eq(NotificationType.NEW_BID), anyString());
  }

  @Test
  public void testPlaceBid_ItemNotBiddable_ThrowsException() {
    BidCreateDto createDto = new BidCreateDto();
    createDto.setItemId(sampleItem.getItemId());
    createDto.setAmount(new java.math.BigDecimal("120.00"));

    sampleItem.setListingType(ListingType.DIRECT);

    when(itemRepository.findById(sampleItem.getItemId()))
        .thenReturn(Optional.of(sampleItem));

    // Expect an IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> bidService.placeBid(createDto, bidder));
  }

  @Test
  public void testGetBidsForItem_Success() {
    List<Bid> bids = Collections.singletonList(sampleBid);
    when(bidRepository.findByItem_ItemId(sampleItem.getItemId())).thenReturn(bids);

    List<BidDto> bidDtos = bidService.getBidsForItem(sampleItem.getItemId());
    assertThat(bidDtos).hasSize(1);

    assertThat(bidDtos.get(0).getAmount()).isEqualByComparingTo(sampleBid.getAmount());
  }

  @Test
  public void testAcceptBid_Success() {
    // First, ensure that the sample bid exists
    when(bidRepository.findById(sampleBid.getId())).thenReturn(Optional.of(sampleBid));

    sampleBid.getItem().setSeller(seller);

    when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order orderArg = invocation.getArgument(0);
      ReflectionTestUtils.setField(orderArg, "id", 30L);
      return orderArg;
    });

    // Execute acceptBid.
    OrderDto orderDto = bidService.acceptBid(sampleBid.getId(), seller, true);

    // the bid status should be updated to ACCEPTED
    assertThat(sampleBid.getStatus()).isEqualTo(BidStatus.ACCEPTED);
    // the order should contain the correct item and buyer
    assertThat(orderDto).isNotNull();
    assertThat(orderDto.getBuyerId()).isEqualTo(bidder.getId()); // because OrderMapper maps the buyer info

    // Verify that a notification is sent to the bidder for bid acceptance.
    verify(notificationService, times(1))
        .send(eq(bidder), any(Map.class), eq(NotificationType.BID_ACCEPTED), anyString());
  }

  @Test
  public void testRejectBid_Success() {
    // First, ensure that the sample bid exists
    when(bidRepository.findById(sampleBid.getId())).thenReturn(Optional.of(sampleBid));

    sampleBid.getItem().setSeller(seller);

    when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order orderArg = invocation.getArgument(0);
      ReflectionTestUtils.setField(orderArg, "id", 30L);
      return orderArg;
    });

    // Execute acceptBid.
    OrderDto orderDto = bidService.acceptBid(sampleBid.getId(), seller, false);

    // the bid status should be updated to ACCEPTED
    assertThat(sampleBid.getStatus()).isEqualTo(BidStatus.REJECTED);
    // the order should contain the correct item and buyer
    assertThat(orderDto).isNotNull();
    assertThat(orderDto.getBuyerId()).isEqualTo(bidder.getId()); // because OrderMapper maps the buyer info

    // Verify that a notification is sent to the bidder for bid acceptance.
    verify(notificationService, times(1))
        .send(eq(bidder), any(Map.class), eq(NotificationType.BID_ACCEPTED), anyString());
  }
}
