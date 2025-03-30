package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateDto {
  @NotNull(message = "Item ID is required")
  private Long itemId;
}
