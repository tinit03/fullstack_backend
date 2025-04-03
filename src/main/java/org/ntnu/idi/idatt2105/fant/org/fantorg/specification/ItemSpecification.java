package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {
  public static Specification<Item> hasKeyWordInAnyField(String keyword) {
    return (root, query, cb) -> {
      // formats keyword to %keyword%, so it matches everywhere in the string, e.g.
      String like = "%" + keyword.toLowerCase() + "%";

      Join<Item, String> tagsJoin = root.join("tags", JoinType.LEFT);

      return cb.or(
          cb.like(cb.lower(root.get("title")), like),
          cb.like(cb.lower(root.get("description")), like),
          cb.like(cb.lower(tagsJoin), like)
      );
    };
  }

  public static Specification<Item> hasStatus(Status status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<Item> hasSeller(User seller) {
    return (root, query, cb) -> cb.equal(root.get("seller"), seller);
  }
}
