package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {
  public static Specification<Item> hasKeyWordInAnyField(String keyword) {
    return (root, query, cb) -> {
      // formats keyword to %keyword%, so it matches every where in the string, e.g.
      String like = "%" + keyword.toLowerCase() + "%";

      Join<Item, String> tagsJoin = root.join("tags", JoinType.LEFT);

      return cb.or(
          cb.like(cb.lower(root.get("itemName")), like),
          cb.like(cb.lower(root.get("briefDescription")), like),
          cb.like(cb.lower(root.get("fullDescription")), like),
          cb.like(cb.lower(tagsJoin), like)
      );

    };
  }
}
