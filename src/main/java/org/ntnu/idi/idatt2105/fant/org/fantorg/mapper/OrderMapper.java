package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public class OrderMapper {
  public static OrderDto toDto(Order order) {
    OrderDto dto = new OrderDto();
    User seller = order.getItem().getSeller();
    dto.setId(order.getId());
    dto.setOrderDate(order.getOrderDate());
    dto.setPrice(order.getPrice());

    if (order.getItem() != null) {
      dto.setItemId(order.getItem().getItemId());
      dto.setItemTitle(order.getItem().getTitle());
    }

    if (order.getBuyer() != null) {
      dto.setBuyerId(order.getBuyer().getId());
      dto.setBuyerName(order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName());
    }

    if (seller != null) {
      dto.setSellerId(seller.getId());
      dto.setSellerName(seller.getFirstName() + " " + seller.getLastName());
    }

    return dto;
  }
}
