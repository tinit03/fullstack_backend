package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "\"Reviews\"")
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int rating; // 1-5 stars
  private String comment;

  private LocalDateTime createdAt = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;
}
