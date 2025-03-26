package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;

  @Entity
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Table(name = "\"Bids\"")
  public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime bidTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;
}
