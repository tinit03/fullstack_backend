package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for filtering and querying items based on various conditions.
 * This class provides static methods that return Specifications for different criteria
 * related to the {@link Item} entity, allowing for flexible querying.
 *
 * <p>Each method in this class returns a {@link Specification<Item>} that can be used
 * to create dynamic queries with different conditions such as price ranges, categories,
 * status, location, etc. These Specifications can be combined and used in repository queries.</p>
 *
 * @version 1.0
 */
public class ItemSpecification {

    /**
     * Returns a Specification to filter items based on a keyword found in any of their fields
     * (title, description, or tags).
     *
     * @param keyword The keyword to search for in the item's title, description, or tags.
     * @return A Specification that filters items based on the given keyword.
     */
    public static Specification<Item> hasKeyWordInAnyField(String keyword) {
        return (root, query, cb) -> {
            String like = "%" + keyword.toLowerCase() + "%";

            Join<Item, String> tagsJoin = root.join("tags", JoinType.LEFT);

            return cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like),
                cb.like(cb.lower(tagsJoin), like)
            );
        };
    }

    /**
     * Returns a Specification to filter items based on their status.
     *
     * @param status The status to filter items by.
     * @return A Specification that filters items by the specified status.
     */
    public static Specification<Item> hasStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /**
     * Returns a Specification to filter items by a list of possible statuses.
     *
     * @param statuses A list of statuses to filter items by.
     * @return A Specification that filters items by any of the specified statuses.
     */
    public static Specification<Item> hasStatusIn(Status... statuses) {
        return (root, query, cb) -> root.get("status").in((Object[]) statuses);
    }

    /**
     * Returns a Specification to filter items by their seller.
     *
     * @param seller The seller to filter items by.
     * @return A Specification that filters items by the specified seller.
     */
    public static Specification<Item> hasSeller(User seller) {
        return (root, query, cb) -> cb.equal(root.get("seller"), seller);
    }

    /**
     * Returns a Specification to filter items that do not belong to a specific seller.
     *
     * @param seller The seller to filter items that do not belong to.
     * @return A Specification that filters items that do not belong to the specified seller.
     */
    public static Specification<Item> hasNotSeller(User seller) {
        return (root, query, cb) -> cb.notEqual(root.get("seller"), seller);
    }

    /**
     * Returns a Specification to filter items by their category.
     *
     * @param categoryIds A list of category IDs to filter items by.
     * @return A Specification that filters items by the specified categories.
     */
    public static Specification<Item> hasCategoryIn(List<Long> categoryIds) {
        return (root, query, cb) ->
            root.get("subCategory").get("parentCategory").get("id").in(categoryIds);
    }

    /**
     * Returns a Specification to filter items by their subcategory.
     *
     * @param subCategoryIds A list of subcategory IDs to filter items by.
     * @return A Specification that filters items by the specified subcategories.
     */
    public static Specification<Item> hasSubCategoryIn(List<Long> subCategoryIds) {
        return (root, query, cb) ->
            root.get("subCategory").get("id").in(subCategoryIds);
    }

    /**
     * Returns a Specification to filter items by their condition.
     *
     * @param conditions A list of conditions to filter items by.
     * @return A Specification that filters items by the specified conditions.
     */
    public static Specification<Item> hasConditionIn(List<Condition> conditions) {
        return (root, query, cb) -> root.get("condition").in(conditions);
    }

    /**
     * Returns a Specification to filter items based on their location's county.
     *
     * @param counties A list of counties to filter items by.
     * @return A Specification that filters items based on their county.
     */
    public static Specification<Item> isInCounties(List<String> counties) {
        return (root, query, cb) -> {
            List<String> lowered = counties.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
            return cb.lower(root.get("location").get("county")).in(lowered);
        };
    }

    /**
     * Returns a Specification to filter items by their price range.
     *
     * @param min The minimum price to filter items by.
     * @param max The maximum price to filter items by.
     * @return A Specification that filters items within the specified price range.
     */
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

    /**
     * Returns a Specification to filter items based on whether they are for sale or not.
     *
     * @param forSale A boolean value indicating whether to filter items that are for sale.
     * @return A Specification that filters items based on their sale status.
     */
    public static Specification<Item> hasForSale(boolean forSale) {
        return (root, query, cb) -> cb.equal(root.get("forSale"), forSale);
    }

    /**
     * Returns a Specification to filter items that were published today.
     *
     * @return A Specification that filters items published within the current day.
     */
    public static Specification<Item> isPublishedToday() {
        return (root, query, cb) -> {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            return cb.between(root.get("publishedAt"), startOfDay, endOfDay);
        };
    }
}
