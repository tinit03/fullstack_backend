package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Utility class for obtaining facet counts for the Item entity. This class contains various methods
 * to generate facet counts based on different field types (Enum, String, Long, Boolean).
 *
 * <p>The facet counts are based on a Specification which allows the use of complex queries with
 * criteria filtering.
 *
 * <p>Facet counts are often used for filtering or grouping data based on certain attributes. This
 * utility supports counting items based on different field types, such as Enums, Strings, Longs,
 * Booleans, and publication dates (e.g., published today).
 *
 * <p>It uses JPA Criteria API to dynamically build queries and fetch results based on provided
 * specifications.
 *
 * @version 1.0
 */
@Component
public class ItemFacetCountUtil {

  @PersistenceContext private EntityManager entityManager;

  /**
   * Retrieves facet counts for an enum field.
   *
   * <p>This method counts the number of distinct items for each enum value in the specified field,
   * using the provided Specification.
   *
   * @param spec The Specification used for filtering the items
   * @param field The field name in the Item entity that contains the Enum values
   * @param enumClass The class type of the Enum
   * @param <T> The type of the Enum
   * @return A map of Enum values and their corresponding counts
   */
  public <T extends Enum<T>> Map<T, Long> getEnumFacetCounts(
      Specification<Item> spec, String field, Class<T> enumClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
    Root<Item> root = query.from(Item.class);

    Path<T> path = getNestedPath(root, field);
    query.multiselect(path, cb.countDistinct(root.get("itemId")));
    query.where(spec.toPredicate(root, query, cb));
    query.groupBy(path);

    List<Object[]> results = entityManager.createQuery(query).getResultList();

    Map<T, Long> result = new EnumMap<>(enumClass);
    for (Object[] row : results) {
      if (row[0] != null) {
        result.put((T) row[0], (Long) row[1]);
      }
    }
    return result;
  }

  /**
   * Retrieves facet counts for a string field.
   *
   * <p>This method counts the number of distinct items for each string value in the specified
   * field, using the provided Specification.
   *
   * @param spec The Specification used for filtering the items
   * @param field The field name in the Item entity that contains the String values
   * @return A map of String values and their corresponding counts
   */
  public Map<String, Long> getStringFacetCounts(Specification<Item> spec, String field) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
    Root<Item> root = query.from(Item.class);

    Path<String> path = getNestedPath(root, field);
    query.multiselect(path, cb.countDistinct(root.get("itemId")));
    query.where(spec.toPredicate(root, query, cb));
    query.groupBy(path);

    List<Object[]> results = entityManager.createQuery(query).getResultList();
    Map<String, Long> result = new HashMap<>();
    for (Object[] row : results) {
      if (row[0] != null) {
        result.put((String) row[0], (Long) row[1]);
      }
    }
    return result;
  }

  /**
   * Retrieves facet counts for a long field.
   *
   * <p>This method counts the number of distinct items for each long value in the specified field,
   * using the provided Specification.
   *
   * @param spec The Specification used for filtering the items
   * @param field The field name in the Item entity that contains the Long values
   * @return A map of Long values and their corresponding counts
   */
  public Map<Long, Long> getLongFacetCounts(Specification<Item> spec, String field) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
    Root<Item> root = query.from(Item.class);

    Path<Long> path = getNestedPath(root, field);
    query.multiselect(path, cb.countDistinct(root.get("itemId")));
    query.where(spec.toPredicate(root, query, cb));
    query.groupBy(path);

    List<Object[]> results = entityManager.createQuery(query).getResultList();
    Map<Long, Long> result = new HashMap<>();
    for (Object[] row : results) {
      if (row[0] != null) {
        result.put((Long) row[0], (Long) row[1]);
      }
    }
    return result;
  }

  /**
   * Retrieves facet counts for a boolean field.
   *
   * <p>This method counts the number of distinct items for true and false values in the specified
   * boolean field, using the provided Specification.
   *
   * @param spec The Specification used for filtering the items
   * @param field The field name in the Item entity that contains the Boolean values
   * @return A map of "true" and "false" values with their corresponding counts
   */
  public Map<String, Long> getBooleanFacetCounts(Specification<Item> spec, String field) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<Item> root = query.from(Item.class);

    query
        .multiselect(
            cb.selectCase()
                .when(cb.isTrue(root.get(field)), "true")
                .otherwise("false")
                .alias("value"),
            cb.countDistinct(root.get("itemId")).alias("count"))
        .where(spec.toPredicate(root, query, cb))
        .groupBy(root.get(field));

    return entityManager.createQuery(query).getResultList().stream()
        .collect(
            Collectors.toMap(t -> t.get("value", String.class), t -> t.get("count", Long.class)));
  }

  /**
   * Retrieves facet counts for items published today.
   *
   * <p>This method counts the number of distinct items that were published today, based on the
   * "publishedAt" field, using the provided Specification.
   *
   * @param spec The Specification used for filtering the items
   * @return A map with "true" and "false" keys representing whether the item was published today or
   *     not, with their corresponding counts
   */
  public Map<String, Long> getPublishedTodayFacetCounts(Specification<Item> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<Item> root = query.from(Item.class);

    LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

    Expression<Object> isTodayExpr =
        cb.selectCase()
            .when(cb.greaterThanOrEqualTo(root.get("publishedAt"), todayStart), "true")
            .otherwise("false");

    query
        .multiselect(
            isTodayExpr.alias("isToday"), cb.countDistinct(root.get("itemId")).alias("count"))
        .where(spec.toPredicate(root, query, cb))
        .groupBy(isTodayExpr);

    return entityManager.createQuery(query).getResultList().stream()
        .collect(
            Collectors.toMap(t -> t.get("isToday", String.class), t -> t.get("count", Long.class)));
  }

  /**
   * Utility method for extracting a nested path from an entity based on a field name.
   *
   * @param root The root of the Criteria query
   * @param field The field name (possibly with nested properties, e.g., "category.name")
   * @param <T> The type of the field
   * @return The path corresponding to the specified field
   */
  private <T> Path<T> getNestedPath(Root<?> root, String field) {
    String[] parts = field.split("\\.");
    Path<?> path = root;
    for (String part : parts) {
      path = path.get(part);
    }
    return (Path<T>) path;
  }
}
