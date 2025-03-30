package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;
@Getter
@Setter
public class BidDto {
  private Long id;
  private Long itemId;
  private BigDecimal amount;
  private LocalDateTime bidTime;
  private String status;
  private Long bidderId;
  private String bidderName;
}
