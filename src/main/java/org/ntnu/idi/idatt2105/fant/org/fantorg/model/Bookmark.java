package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a bookmark created by a user on an item. A bookmark allows a user to save an
 * item for later reference or interest.
 *
 * <p>This entity maps to the "bookmark" table in the database and contains the following
 * attributes:
 *
 * <ul>
 *   <li><b>id</b>: The unique identifier for the bookmark (auto-generated).
 *   <li><b>user</b>: The user who created the bookmark (many-to-one relationship with the {@link
 *       User} entity).
 *   <li><b>item</b>: The item that has been bookmarked (many-to-one relationship with the {@link
 *       Item} entity).
 *   <li><b>bookmarkedAt</b>: The timestamp when the bookmark was created.
 * </ul>
 */
@Entity
@Table(name = "bookmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

  /** The unique identifier of the bookmark. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who created the bookmark. This is a many-to-one relationship with the {@link User}
   * entity.
   */
  @ManyToOne(optional = false)
  private User user;

  /**
   * The item that has been bookmarked. This is a many-to-one relationship with the {@link Item}
   * entity.
   */
  @ManyToOne(optional = false)
  private Item item;

  /** The timestamp when the bookmark was created. */
  private LocalDateTime bookmarkedAt;
}
