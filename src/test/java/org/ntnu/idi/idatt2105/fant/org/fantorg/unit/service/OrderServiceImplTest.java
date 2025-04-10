package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatMessageServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.OrderServiceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private ChatMessageService chatMessageService;

  @InjectMocks
  private OrderServiceImpl orderService;

  private User buyer;
  private User seller;
  private Item sampleItem;

  @BeforeEach
  public void setUp() {
    buyer = new User();
    buyer.setFirstName("John");
    buyer.setLastName("Doe");
    buyer.setEmail("john.doe@example.com");

    ReflectionTestUtils.setField(buyer, "id", 1L);

    sampleItem = new Item();
    sampleItem.setTitle("Sample Item");
    sampleItem.setDescription("A sample item description");
    sampleItem.setPrice(new BigDecimal(100));
    seller = new User();
    seller.setEmail("test@example.com");
    sampleItem.setSeller(seller);
    // Set the item ID using ReflectionTestUtils.
    ReflectionTestUtils.setField(sampleItem, "itemId", 10L);
  }

  @Test
  public void testCreateOrder_Success() {
    OrderCreateDto orderCreateDto = new OrderCreateDto();
    orderCreateDto.setItemId(10L);

    when(itemRepository.findById(10L)).thenReturn(Optional.of(sampleItem));

    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      ReflectionTestUtils.setField(order, "id", 100L);

      if (order.getOrderDate() == null) {
        order.setOrderDate(LocalDateTime.now());
      }
      return order;
    });

    ChatMessageCreateDto chatMessageCreateDto = ChatMessageCreateDto.builder()
        .senderId(buyer.getEmail())
        .recipientId(seller.getEmail())
        .itemId(sampleItem.getItemId())
        .type(MessageType.PURCHASE)
        .content(null)
        .build();

    when(chatMessageService.save(any(chatMessageCreateDto.getClass()))).thenReturn(null);

    OrderDto result = orderService.createOrder(orderCreateDto, buyer);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(100L);
    assertThat(result.getItemId()).isEqualTo(10L);
    assertThat(result.getBuyerId()).isEqualTo(1L);
    assertThat(result.getOrderDate()).isNotNull();
  }

  @Test
  public void testCreateOrder_ItemNotFound() {
    OrderCreateDto orderCreateDto = new OrderCreateDto();
    orderCreateDto.setItemId(99L); // Non-existent item ID

    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      orderService.createOrder(orderCreateDto, buyer);
    });
  }
}
