package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a category in the system. Categories can be organized into a hierarchy, where
 * each category can have multiple subcategories.
 *
 * <p>This entity maps to the "Categories" table in the database and contains the following
 * attributes:
 *
 * <ul>
 *   <li><b>categoryId</b>: The unique identifier for the category (auto-generated).
 *   <li><b>categoryName</b>: The name of the category.
 *   <li><b>parentCategory</b>: The parent category of this category. This is a self-referencing
 *       relationship, meaning categories can have parent categories, forming a tree-like structure.
 *       It is ignored during JSON serialization.
 *   <li><b>subCategories</b>: A list of subcategories under this category. This is a one-to-many
 *       relationship with the same {@link Category} entity, where each subcategory points to its
 *       parent category.
 *   <li><b>items</b>: A list of items associated with this category. It is a one-to-many
 *       relationship with the {@link Item} entity.
 *   <li><b>image</b>: The image associated with the category. It is a one-to-one relationship with
 *       the {@link Image} entity.
 * </ul>
 */
@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "\"Categories\"")
public class Category {

  /** The unique identifier of the category. It is auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long categoryId;

  /** The name of the category. */
  private String categoryName;

  /**
   * The parent category of this category. This is a self-referencing relationship for building a
   * category-subcategory structure. This field is ignored during JSON serialization.
   */
  @ManyToOne
  @JoinColumn(name = "parent_id")
  @JsonIgnore
  private Category parentCategory; // self referencing for creating the category-subcategory system

  /**
   * A list of subcategories associated with this category. This is a one-to-many relationship with
   * the same {@link Category} entity.
   */
  @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
  private List<Category> subCategories = new ArrayList<>();

  /**
   * A list of items associated with this category. This is a one-to-many relationship with the
   * {@link Item} entity.
   */
  @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Item> items = new ArrayList<>();

  /**
   * The image associated with this category. This is a one-to-one relationship with the {@link
   * Image} entity.
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "image_id")
  private Image image;

  /**
   * Method to check if the category is a leaf (i.e., it has no subcategories).
   *
   * @return {@code true} if this category has no subcategories, {@code false} otherwise.
   */
  public boolean isLeaf() {
    return subCategories == null || subCategories.isEmpty();
  }
}
