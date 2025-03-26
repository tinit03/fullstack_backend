package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;

@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "\"Items\"")
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  @Column(name = "item_id")
  private Long itemId;

  private String title;
  private String briefDescription;
  @Column(length = 2048)
  private String fullDescription;
  private BigDecimal price;

  @ManyToOne
  @JoinColumn(name = "subcategory_id")
  private Category subCategory;
  private LocalDateTime publishedAt;

  @ManyToOne
  @JoinColumn(name = "seller_id")
  private User seller;

  @ElementCollection
  private List<String> tags;

  @Embedded
  private Location location;

  @Enumerated(EnumType.STRING)
  private ListingType listingType;
}
