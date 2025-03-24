package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private Long itemId;

  private String title;
  private String briefDescription;
  @Column(length = 2048)
  private String fullDescription;
  private double latitude;
  private double longitude;
  private double price;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;
  private LocalDateTime publishedAt;

  @ManyToOne
  @JoinColumn(name = "seller_id")
  private User seller;

  @ElementCollection
  private List<String> tags;

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews = new ArrayList<>();

  @Embedded
  private Location location;
}
