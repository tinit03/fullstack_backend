package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
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

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final NotificationService notificationService;
  private final ChatMessageService chatMessageService;

  @Override
  public OrderDto createOrder(OrderCreateDto dto, User user) {
    Item item = itemRepository.findById(dto.getItemId())
        .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    if(item.getStatus()== Status.SOLD) throw new IllegalArgumentException("You cannot buy sold items");
    Order order = new Order();
    order.setItem(item);
    order.setBuyer(user);
    order.setOrderDate(LocalDateTime.now());
    item.setStatus(Status.SOLD);
    Order savedOrder = orderRepository.save(order);

    Map<String, String> args = Map.of("user", savedOrder.getBuyer().getFirstName() +" "+savedOrder.getBuyer().getLastName()
        , "item", item.getTitle());
    String link = "/messages?itemId=" + item.getItemId() + "&recipientId=" + order.getBuyer().getEmail();
    notificationService.send(item.getSeller(),args, NotificationType.ITEM_SOLD,link);
    chatMessageService.send(ChatMessageCreateDto.builder()
        .senderId(savedOrder.getBuyer().getEmail())
        .recipientId(item.getSeller().getEmail())
        .itemId(item.getItemId())
        .type(MessageType.PURCHASE)
        .content(null)
        .build());
    return OrderMapper.toDto(savedOrder);
  }

  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders.stream().map(OrderMapper::toDto).toList();
  }


}
