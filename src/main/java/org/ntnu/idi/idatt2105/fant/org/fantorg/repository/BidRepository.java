package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid,Long> {
  List<Bid> findByItem_ItemId(Long id);
}
