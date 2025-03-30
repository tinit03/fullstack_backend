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
import jakarta.persistence.Table;
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
@Table(name = "\"Categories\"")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long categoryId;

  private String categoryName;

  @ManyToOne
  @JoinColumn(name="parent_id")
  @JsonIgnore
  private Category parentCategory; // self referencing for creating the category-subcategory system

  @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
  private List<Category> subCategories = new ArrayList<>();

  // to check if it's a subcategory
  public boolean isLeaf() {
    return subCategories == null || subCategories.isEmpty();
  }
}
