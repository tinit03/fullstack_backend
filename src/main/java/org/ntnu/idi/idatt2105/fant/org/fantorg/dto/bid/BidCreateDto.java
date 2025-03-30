package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidCreateDto {
  @NotNull(message = "Item ID is required")
  private Long itemId;

  @NotNull(message = "Bid amount is required")
  @DecimalMin(value = "0.01", message = "Bid amount must be greater than 0")
  private BigDecimal amount;
}
