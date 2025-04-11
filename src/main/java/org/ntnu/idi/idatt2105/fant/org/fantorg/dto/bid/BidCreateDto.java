package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new bid.
 * <p>
 * This DTO is used to capture the necessary information required for placing a bid on an item.
 * It includes the item identifier and the bid amount.
 * </p>
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class BidCreateDto {

  /**
   * The identifier of the item on which the bid is being placed.
   * <p>
   * This field is required and must not be {@code null}.
   * </p>
   */
  @NotNull(message = "Item ID is required")
  private Long itemId;

  /**
   * The bid amount.
   * <p>
   * This field is required and must be a value greater than {@code 0.00}.
   * The {@link DecimalMin} annotation enforces that the amount is at least {@code 0.01}.
   * </p>
   */
  @NotNull(message = "Bid amount is required")
  @DecimalMin(value = "0.01", message = "Bid amount must be greater than 0")
  private BigDecimal amount;
}
