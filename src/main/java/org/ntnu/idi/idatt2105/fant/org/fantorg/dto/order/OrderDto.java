package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
  private Long id;
  private Long itemId;
  private String itemTitle;
  private Long buyerId;
  private String buyerName;
  private Long sellerId;
  private String sellerName;
  private LocalDateTime orderDate;
  private BigDecimal price;
}
