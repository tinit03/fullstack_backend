package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;

/**
 * Data Transfer Object (DTO) representing a bid.
 * <p>
 * This class encapsulates the details of a bid, including the bid amount, time, status, and information about the bidder.
 * It is used to transfer bid data between different layers of the application.
 * </p>
 *
 * <p>
 * Fields:
 * <ul>
 *   <li>{@code id} - Unique identifier for the bid.</li>
 *   <li>{@code itemId} - Identifier of the item on which the bid is placed.</li>
 *   <li>{@code amount} - The monetary amount of the bid.</li>
 *   <li>{@code bidTime} - The date and time when the bid was made.</li>
 *   <li>{@code status} - The current status of the bid (e.g., pending, accepted, rejected). Can be related to {@link BidStatus}.</li>
 *   <li>{@code bidderId} - Unique identifier of the bidder who placed the bid.</li>
 *   <li>{@code bidderName} - Name of the bidder.</li>
 * </ul>
 * </p>
 *
 * @see BidStatus
 * @author Tini Tran
 */
@Getter
@Setter
public class BidDto {

  /**
   * The unique identifier of the bid.
   */
  private Long id;

  /**
   * The identifier of the item being bid on.
   */
  private Long itemId;

  /**
   * The monetary amount offered in the bid.
   */
  private BigDecimal amount;

  /**
   * The timestamp indicating when the bid was placed.
   */
  private LocalDateTime bidTime;

  /**
   * The current status of the bid, represented as a String.
   * <p>
   * This status might correspond to values defined in the {@link BidStatus} enum.
   * </p>
   */
  private String status;

  /**
   * The unique identifier of the user who placed the bid.
   */
  private Long bidderId;

  /**
   * The name of the bidder.
   */
  private String bidderName;
}
