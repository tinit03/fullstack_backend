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

/**
 * Entity representing a bid in the marketplace.
 * A bid is placed by a user on an item, with a specific amount and time.
 * The status of the bid is tracked, indicating whether it's pending, accepted, or rejected.
 * <p>
 * This entity maps to the "Bids" table in the database and contains the following attributes:
 * <ul>
 *   <li><b>id</b>: The unique identifier of the bid (auto-generated).</li>
 *   <li><b>item</b>: The item the bid is placed on (many-to-one relationship with the {@link Item} entity).</li>
 *   <li><b>bidder</b>: The user placing the bid (many-to-one relationship with the {@link User} entity).</li>
 *   <li><b>amount</b>: The amount of money offered in the bid.</li>
 *   <li><b>bidTime</b>: The timestamp of when the bid was placed.</li>
 *   <li><b>status</b>: The status of the bid (e.g., pending, accepted, rejected) defined by {@link BidStatus} enum.</li>
 * </ul>
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Bids\"")
public class Bid {

    /**
     * The unique identifier of the bid.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The item the bid is placed on.
     * This is a many-to-one relationship with the {@link Item} entity.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * The user who placed the bid.
     * This is a many-to-one relationship with the {@link User} entity.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    /**
     * The amount of money offered in the bid.
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * The timestamp of when the bid was placed.
     */
    @Column(nullable = false)
    private LocalDateTime bidTime;

    /**
     * The status of the bid.
     * This is an enumerated field that can be {@link BidStatus#PENDING},
     * {@link BidStatus#ACCEPTED}, or {@link BidStatus#REJECTED}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;
}
