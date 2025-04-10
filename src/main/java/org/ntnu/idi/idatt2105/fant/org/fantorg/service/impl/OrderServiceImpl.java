package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.OrderMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.OrderService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;

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
    return OrderMapper.toDto(savedOrder);
  }

  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders.stream().map(OrderMapper::toDto).toList();
  }


}
