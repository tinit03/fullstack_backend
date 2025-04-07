package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
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
  public static Specification<Item> hasStatusIn(Status... statuses) {
    return (root, query, cb) -> root.get("status").in((Object[]) statuses);
  }
  public static Specification<Item> hasSeller(User seller) {
    return (root, query, cb) -> cb.equal(root.get("seller"), seller);
  }

  public static Specification<Item> hasNotSeller(User seller) {
    return (root, query, cb) -> cb.notEqual(root.get("seller"), seller);
  }

  public static Specification<Item> hasCategoryIn(List<Long> categoryIds) {
    return (root, query, cb) ->
        root.get("subCategory").get("parentCategory").get("id").in(categoryIds);
  }

  public static Specification<Item> hasSubCategoryIn(List<Long> subCategoryIds) {
    return (root, query, cb) ->
        root.get("subCategory").get("id").in(subCategoryIds);
  }

  public static Specification<Item> hasConditionIn(List<Condition> conditions) {
    return (root, query, cb) -> root.get("condition").in(conditions);
  }

  public static Specification<Item> isInCounties(List<String> counties) {
    return (root, query, cb) -> {
      List<String> lowered = counties.stream()
          .map(String::toLowerCase)
          .collect(Collectors.toList());
      return cb.lower(root.get("location").get("county")).in(lowered);
    };
  }

  public static Specification<Item> hasPriceBetween(BigDecimal min, BigDecimal max) {
    return (root, query, cb) -> {
      if (min != null && max != null) {
        return cb.between(root.get("price"), min, max);
      } else if (min != null) {
        return cb.greaterThanOrEqualTo(root.get("price"), min);
      } else if (max != null) {
        return cb.lessThanOrEqualTo(root.get("price"), max);
      } else {
        return cb.conjunction(); // No filter
      }
    };
  }
  public static Specification<Item> hasForSale(boolean forSale) {
    return (root, query, cb) -> cb.equal(root.get("forSale"), forSale);
  }
}
