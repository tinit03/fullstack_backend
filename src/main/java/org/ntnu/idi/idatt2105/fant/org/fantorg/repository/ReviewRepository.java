package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Review} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations for {@link Review} entities and additional
 * operations for handling reviews related to orders.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

  /**
   * Checks if a review exists for the given order.
   *
   * @param order the order for which to check if a review exists.
   * @return {@code true} if a review exists for the given order, otherwise {@code false}.
   */
  boolean existsByOrder(Order order);

  /**
   * Finds all reviews for items sold by a specific seller.
   *
   * @param seller the seller whose items' reviews are to be found.
   * @return a list of reviews for items sold by the given seller.
   */
  List<Review> findAllByOrder_Item_Seller(User seller);

  /**
   * Finds all reviews for items sold by a seller, paginated.
   *
   * @param sellerId the ID of the seller whose items' reviews are to be found.
   * @param pageable the pagination information.
   * @return a page of reviews for items sold by the specified seller.
   */
  Page<Review> findAllByOrder_Item_Seller_Id(Long sellerId, Pageable pageable);

  /**
   * Counts the number of reviews for items sold by a specific seller.
   *
   * @param user the seller whose reviews are to be counted.
   * @return the number of reviews for items sold by the specified seller.
   */
  long countByOrder_Item_Seller(User user);
}
