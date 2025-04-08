package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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

@Component
public class ItemFacetCountUtil {
  @PersistenceContext
  private EntityManager entityManager;

  public <T extends Enum<T>> Map<T, Long> getEnumFacetCounts(Specification<Item> spec, String field, Class<T> enumClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class); // Ønsker å returnere Object[] (som er da value + count)
    Root<Item> root = query.from(Item.class); // Henter from ITEM

    Path<T> path = getNestedPath(root, field);
    query.multiselect(path, cb.count(root)); // SELECT COUNT(*) FROM ITEM
    query.where(spec.toPredicate(root, query, cb)); // WHERE-betingelsene blir fylt av specification
    query.groupBy(path); // GROUP BY field

    List<Object[]> results = entityManager.createQuery(query).getResultList(); //Kjør queryen

    Map<T, Long> result = new EnumMap<>(enumClass); //Bruker enum map for å mappe count og value
    for (Object[] row : results) {
      if (row[0] != null) {
        result.put((T) row[0], (Long) row[1]);
      }
    }
    return result;
  }

  public Map<String, Long> getStringFacetCounts(Specification<Item> spec, String field) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
    Root<Item> root = query.from(Item.class);

    Path<String> path = getNestedPath(root, field);
    query.multiselect(path, cb.count(root));
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

  public Map<Long, Long> getLongFacetCounts(Specification<Item> spec, String field) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
    Root<Item> root = query.from(Item.class);

    Path<Long> path = getNestedPath(root, field);
    query.multiselect(path, cb.count(root));
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

  public Map<String, Long> getBooleanFacetCounts(Specification<Item> spec, String field) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<Item> root = query.from(Item.class);

    query.multiselect(
        cb.selectCase().when(cb.isTrue(root.get(field)), "true").otherwise("false").alias("value"),
        cb.count(root).alias("count")
    ).where(spec.toPredicate(root, query, cb)).groupBy(root.get(field));

    return entityManager.createQuery(query).getResultList().stream()
        .collect(Collectors.toMap(
            t -> t.get("value", String.class),
            t -> t.get("count", Long.class)
        ));
  }
  public Map<String, Long> getPublishedTodayFacetCounts(Specification<Item> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<Item> root = query.from(Item.class);

    query.multiselect(
            root.get("publishedAt").alias("publishedAt"),
            cb.count(root).alias("count")
        ).where(spec.toPredicate(root, query, cb))
        .groupBy(root.get("publishedAt"));

    // Process results
    LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

    return entityManager.createQuery(query).getResultList().stream()
        .collect(Collectors.groupingBy(
            t -> t.get("publishedAt", LocalDateTime.class).isAfter(todayStart) ? "true" : "false",
            Collectors.summingLong(t -> t.get("count", Long.class))
        ));
  }

  private <T> Path<T> getNestedPath(Root<?> root, String field) {
    String[] parts = field.split("\\.");
    Path<?> path = root;
    for (String part : parts) {
      path = path.get(part);
    }
    return (Path<T>) path;
  }

}
