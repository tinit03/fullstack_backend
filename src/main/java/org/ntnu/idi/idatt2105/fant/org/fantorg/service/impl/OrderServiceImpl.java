package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.OrderMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link OrderService} interface. Handles creation, retrieval, and listing of
 * orders. Automatically updates item status and notifies the seller when a purchase is made.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final NotificationService notificationService;
  private final ChatMessageService chatMessageService;

  /**
   * Creates a new order for a given item and user. This method also updates the item's status to
   * SOLD, sends a notification to the seller, and initiates a purchase chat message.
   *
   * @param dto The data transfer object containing itemId.
   * @param user The buyer placing the order.
   * @return The created {@link OrderDto}.
   * @throws EntityNotFoundException If the item does not exist.
   * @throws IllegalArgumentException If the item is already sold.
   */
  @Override
  public OrderDto createOrder(OrderCreateDto dto, User user) {
    long itemId = dto.getItemId();

    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    if (item.getStatus() == Status.SOLD)
      throw new IllegalArgumentException("You cannot buy sold items");

    Order order = new Order();
    order.setPrice(item.getPrice());
    order.setItem(item);
    order.setBuyer(user);
    order.setOrderDate(LocalDateTime.now());
    item.setStatus(Status.SOLD);
    Order savedOrder = orderRepository.save(order);

    Map<String, String> args =
        Map.of(
            "user",
            savedOrder.getBuyer().getFirstName() + " " + savedOrder.getBuyer().getLastName(),
            "item",
            item.getTitle());

    String link =
        "/messages?itemId=" + item.getItemId() + "&recipientId=" + order.getBuyer().getEmail();
    notificationService.send(item.getSeller(), args, NotificationType.ITEM_SOLD, link);
    chatMessageService.send(
        ChatMessageCreateDto.builder()
            .senderId(savedOrder.getBuyer().getEmail())
            .recipientId(item.getSeller().getEmail())
            .itemId(item.getItemId())
            .type(MessageType.PURCHASE)
            .content(savedOrder.getId().toString())
            .build());
    return OrderMapper.toDto(savedOrder);
  }

  /**
   * Retrieves an order by ID if the user is the buyer or the item's seller.
   *
   * @param orderId The ID of the order.
   * @param user The user requesting the order.
   * @return The corresponding {@link OrderDto}.
   * @throws EntityNotFoundException If the order does not exist.
   * @throws SecurityException If the user is not authorized to view the order.
   */
  @Override
  public OrderDto getOrderWithId(long orderId, User user) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

    if (user.getId().equals(order.getBuyer().getId())
        || user.getId().equals(order.getItem().getSeller().getId())) {
      return OrderMapper.toDto(order);
    } else {
      throw new SecurityException("The user is not a part of the order");
    }
  }

  /**
   * Retrieves all orders in the system.
   *
   * @return A list of all {@link OrderDto}s.
   */
  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders.stream().map(OrderMapper::toDto).toList();
  }
}
