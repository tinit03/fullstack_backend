package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on {@link Bid} entities. This interface
 * extends {@link JpaRepository}, providing standard methods for interacting with the database such
 * as saving, deleting, and finding {@link Bid} objects. Custom query methods can also be defined
 * here.
 *
 * @author Tini Tran
 * @since 1.0
 */
public interface BidRepository extends JpaRepository<Bid, Long> {

  /**
   * Finds a list of {@link Bid} entities associated with a particular item. This method queries the
   * database for all bids placed on the item with the specified {@code id}.
   *
   * @param id the unique identifier of the item for which bids are being fetched
   * @return a list of {@link Bid} entities for the specified item
   */
  List<Bid> findByItem_ItemId(Long id);
}
