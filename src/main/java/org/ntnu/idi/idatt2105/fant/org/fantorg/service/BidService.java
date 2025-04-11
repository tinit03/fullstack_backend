package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/** Service interface for managing bids on items. */
public interface BidService {

  /**
   * Places a bid on an item.
   *
   * @param dto The bid data transfer object containing item and bid info
   * @param bidder The user placing the bid
   * @return The created {@link BidDto}
   */
  BidDto placeBid(BidCreateDto dto, User bidder);

  /**
   * Gets the bids for an item.
   *
   * @param itemId identifies the item.
   * @return The bids for the item as a list.
   */
  List<BidDto> getBidsForItem(Long itemId);

  /**
   * Accepts a bid and generates an order.
   *
   * @param bidId The ID of the bid to accept
   * @param seller The seller accepting the bid
   * @return The generated {@link OrderDto}
   */
  OrderDto acceptBid(Long bidId, User seller);

  /**
   * Rejects a bid.
   *
   * @param bidId The ID of the bid to reject
   * @param seller The seller rejecting the bid
   */
  void rejectBid(Long bidId, User seller);

  /**
   * Retrieves a specific bid by its ID, with user access check.
   *
   * @param id The ID of the bid
   * @param user The user requesting the bid (must be bidder or seller)
   * @return The corresponding {@link BidDto}
   */
  BidDto getBidFromId(Long id, User user);
}
